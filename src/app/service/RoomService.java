package app.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.entity.Amenity;
import app.entity.Room;
import app.repository.AmenityRepository;
import app.repository.AvailabilityRepository;
import app.repository.RoomRepository;
import app.repository.ScheduleRepository;
import app.rest.dto.HourlyCount;
import app.rest.dto.RoomRequest;
import app.rest.dto.RoomView;
import app.rest.errorhandlers.ApiException;

@Service
public class RoomService
{
	@Autowired
	private RoomRepository roomRepo;

	@Autowired
	private AmenityRepository amenityRepo;

	@Autowired
	private AvailabilityRepository availabilityRepo;

	@Autowired
	private ScheduleRepository scheduleRepo;

	@Autowired
	private RoomStatusService statusService;

	// minCapacity arrives as text so that "abc" and "0" can both answer 400.
	@Transactional(readOnly = true)
	public List<RoomView> findRooms(String building, String minCapacity, String amenity)
	{
		Integer min = null;
		if (minCapacity != null && !minCapacity.trim().isEmpty())
		{
			try
			{
				min = Integer.valueOf(minCapacity.trim());
			}
			catch (NumberFormatException e)
			{
				throw new ApiException(400, "minCapacity must be a positive number.");
			}
			if (min <= 0)
			{
				throw new ApiException(400, "minCapacity must be a positive number.");
			}
		}
		return roomRepo.findByFilters(blankToNull(building), min, blankToNull(amenity)).stream()
				.map(this::toView).collect(Collectors.toList());
	}

	public Room getRoom(Long id)
	{
		return roomRepo.findById(id)
				.orElseThrow(() -> new ApiException(404, "Room with ID " + id + " does not exist."));
	}

	@Transactional(readOnly = true)
	public RoomView getRoomView(Long id)
	{
		return toView(getRoom(id));
	}

	@Transactional
	public RoomView createRoom(RoomRequest dto)
	{
		String name = dto.getName().trim();
		String building = dto.getBuilding().trim();
		if (roomRepo.existsByNameAndBuilding(name, building))
		{
			throw new ApiException(409, "Room " + name + " already exists in " + building + ".");
		}
		Set<Amenity> amenities = new LinkedHashSet<>();
		List<Long> ids = new ArrayList<>();
		if (dto.getAmenityId() != null)
		{
			ids.add(dto.getAmenityId());
		}
		if (dto.getAmenityIds() != null)
		{
			ids.addAll(dto.getAmenityIds());
		}
		for (Long id : ids)
		{
			amenities.add(amenityRepo.findById(id)
					.orElseThrow(() -> new ApiException(404, "Amenity with ID " + id + " does not exist.")));
		}
		if (dto.getAmenities() != null)
		{
			for (String amenityName : dto.getAmenities())
			{
				String trimmed = amenityName.trim();
				Amenity a = amenityRepo.findByName(trimmed);
				if (a == null)
				{
					throw new ApiException(404, "Amenity " + trimmed + " does not exist.");
				}
				amenities.add(a);
			}
		}
		Room room = new Room(null, name, building, dto.getFloor(), dto.getCapacity(),
				dto.getMapX(), dto.getMapY(), dto.getLatitude(), dto.getLongitude());
		room.setAmenities(amenities);
		return toView(roomRepo.save(room));
	}

	@Transactional
	public void deleteRoom(Long id)
	{
		Room room = getRoom(id);
		availabilityRepo.deleteByRoom(room);
		scheduleRepo.deleteByRoom(room);
		roomRepo.delete(room);
	}

	@Transactional(readOnly = true)
	public List<HourlyCount> getHourlyHistory(Long roomId)
	{
		getRoom(roomId);
		return availabilityRepo.countByHour(roomId).stream().map(HourlyCount::fromRow)
				.collect(Collectors.toList());
	}

	private RoomView toView(Room room)
	{
		RoomView v = RoomView.of(room);
		RoomStatusService.Info info = statusService.compute(room);
		v.setStatus(info.status);
		v.setPredictedVacant(info.predictedVacant);
		v.setLastReportedAt(info.lastReportedAt);
		v.setMinutesAgo(info.minutesAgo);
		return v;
	}

	private static String blankToNull(String s)
	{
		return s == null || s.trim().isEmpty() ? null : s.trim();
	}
}
