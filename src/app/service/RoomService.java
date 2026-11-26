package app.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.entity.Amenity;
import app.entity.Room;
import app.repository.AmenityRepository;
import app.repository.AvailabilityRepository;
import app.repository.RoomRepository;
import app.repository.SubscriptionRepository;
import app.rest.dto.RoomRequest;
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
	private SubscriptionRepository subscriptionRepo;

	public List<Room> findRooms(String building)
	{
		if (building == null || building.trim().isEmpty())
		{
			return roomRepo.findAll(Sort.by("building", "name"));
		}
		return roomRepo.findByBuildingIgnoreCaseOrderByName(building.trim());
	}

	public Room getRoom(Long id)
	{
		return roomRepo.findById(id)
				.orElseThrow(() -> new ApiException(404, "Room with ID " + id + " does not exist."));
	}

	@Transactional
	public Room createRoom(RoomRequest dto)
	{
		String name = dto.getName().trim();
		String building = dto.getBuilding().trim();
		if (roomRepo.existsByNameAndBuilding(name, building))
		{
			throw new ApiException(409, "Room " + name + " already exists in " + building + ".");
		}
		Set<Amenity> amenities = new LinkedHashSet<>();
		if (dto.getAmenityIds() != null)
		{
			for (Long id : dto.getAmenityIds())
			{
				amenities.add(amenityRepo.findById(id)
						.orElseThrow(() -> new ApiException(404, "Amenity with ID " + id + " does not exist.")));
			}
		}
		Room room = new Room(null, name, building, dto.getFloor(), dto.getCapacity());
		room.setAmenities(amenities);
		return roomRepo.save(room);
	}

	@Transactional
	public void deleteRoom(Long id)
	{
		Room room = getRoom(id);
		availabilityRepo.deleteByRoom(room);
		subscriptionRepo.deleteByRoom(room);
		roomRepo.delete(room);
	}
}
