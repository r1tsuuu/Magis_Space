package app.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.entity.Room;
import app.repository.RoomRepository;
import app.rest.dto.BuildingSummary;
import app.rest.dto.MapRoomView;
import app.rest.dto.RoomStatus;
import app.rest.errorhandlers.ApiException;

@Service
public class MapService
{
	@Autowired
	private RoomRepository roomRepo;

	@Autowired
	private RoomStatusService statusService;

	// floor arrives as text so that a non-numeric value can answer 400.
	@Transactional(readOnly = true)
	public List<MapRoomView> getMap(String building, String floor, String fromBuilding)
	{
		Integer floorNo = null;
		if (floor != null && !floor.trim().isEmpty())
		{
			try
			{
				floorNo = Integer.valueOf(floor.trim());
			}
			catch (NumberFormatException e)
			{
				throw new ApiException(400, "floor must be a number.");
			}
		}
		boolean hasBuilding = building != null && !building.trim().isEmpty();
		List<Room> rooms;
		if (hasBuilding)
		{
			rooms = roomRepo.findAll();
			String wanted = building.trim();
			rooms.removeIf(r -> !r.getBuilding().equalsIgnoreCase(wanted));
			if (rooms.isEmpty())
			{
				throw new ApiException(404, "Building " + wanted + " does not exist.");
			}
		}
		else
		{
			rooms = roomRepo.findAll();
		}
		if (floorNo != null)
		{
			final int f = floorNo;
			rooms.removeIf(r -> r.getFloor() != f);
		}
		boolean hasFrom = fromBuilding != null && !fromBuilding.trim().isEmpty();
		double[] origin = null;
		boolean originGeo = false;
		if (hasFrom)
		{
			String from = fromBuilding.trim();
			List<Room> fromRooms = new ArrayList<>();
			for (Room r : roomRepo.findAll())
			{
				if (r.getBuilding().equalsIgnoreCase(from))
				{
					fromRooms.add(r);
				}
			}
			if (fromRooms.isEmpty())
			{
				throw new ApiException(404, "Building " + from + " does not exist.");
			}
			origin = centroid(fromRooms, true);
			originGeo = origin != null;
			if (!originGeo)
			{
				origin = centroid(fromRooms, false);
			}
			if (origin != null)
			{
				final double[] o = origin;
				final boolean geo = originGeo;
				rooms.sort(Comparator.comparingDouble(r -> distance(r, o, geo)));
			}
		}
		List<MapRoomView> result = new ArrayList<>();
		for (Room r : rooms)
		{
			MapRoomView v = MapRoomView.of(r);
			RoomStatusService.Info info = statusService.compute(r);
			v.setStatus(info.status);
			v.setPredictedVacant(info.predictedVacant);
			v.setLastReportedAt(info.lastReportedAt);
			v.setMinutesAgo(info.minutesAgo);
			result.add(v);
		}
		return result;
	}

	@Transactional(readOnly = true)
	public List<BuildingSummary> getBuildingSummary()
	{
		Map<String, List<Room>> byBuilding = new LinkedHashMap<>();
		for (Room r : roomRepo.findAll())
		{
			byBuilding.computeIfAbsent(r.getBuilding(), k -> new ArrayList<>()).add(r);
		}
		List<BuildingSummary> result = new ArrayList<>();
		for (Map.Entry<String, List<Room>> e : byBuilding.entrySet())
		{
			long vacant = e.getValue().stream()
					.filter(r -> statusService.compute(r).status == RoomStatus.VACANT).count();
			double[] c = centroid(e.getValue(), true);
			result.add(new BuildingSummary(e.getKey(), c == null ? null : c[0], c == null ? null : c[1], vacant,
					e.getValue().size()));
		}
		result.sort(Comparator.comparing(BuildingSummary::getBuilding));
		return result;
	}

	// Average of the rooms that have the coordinates (lat/long when geo, else mapX/mapY); null if none do.
	private double[] centroid(List<Room> rooms, boolean geo)
	{
		double a = 0;
		double b = 0;
		int n = 0;
		for (Room r : rooms)
		{
			Double x = geo ? r.getLatitude() : r.getMapX();
			Double y = geo ? r.getLongitude() : r.getMapY();
			if (x != null && y != null)
			{
				a += x;
				b += y;
				n++;
			}
		}
		return n == 0 ? null : new double[] { a / n, b / n };
	}

	// Rooms without the needed coordinates sort last.
	private double distance(Room r, double[] origin, boolean geo)
	{
		Double x = geo ? r.getLatitude() : r.getMapX();
		Double y = geo ? r.getLongitude() : r.getMapY();
		if (x == null || y == null)
		{
			return Double.MAX_VALUE;
		}
		return Math.hypot(x - origin[0], y - origin[1]);
	}
}
