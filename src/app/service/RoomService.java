package app.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.entity.Amenity;
import app.entity.Room;
import app.repository.AmenityRepository;
import app.repository.RoomRepository;
import app.rest.errorhandlers.ApiException;

@Service
public class RoomService
{
	@Autowired
	private RoomRepository roomRepo;
	
	@Autowired
	private AmenityRepository amenityRepo;
	
	public List<Room> getRooms(String building, Integer minCapacity, String amenity)
	{
		List<Room> rooms = (building == null) ? roomRepo.findAll() : roomRepo.findByBuilding(building);
		
		return rooms.stream()
				.filter(r -> minCapacity == null || r.getCapacity() >= minCapacity)
				.filter(r -> amenity == null || hasAmenity(r, amenity))
				.collect(Collectors.toList());
	}
	
	public Room getRoom(Long id)
	{
		return roomRepo.findById(id)
				.orElseThrow(() -> new ApiException(404, "Room with ID " + id + " does not exist."));
	}
	
	public Room createRoom(Room room)
	{
		if (roomRepo.findByName(room.getName()) != null)
		{
			throw new ApiException(409, "A room named " + room.getName() + " already exists.");
		}
		
		// Clients send amenities by name only; swap each for its persisted row.
		Set<Amenity> resolved = new HashSet<>();
		if (room.getAmenities() != null)
		{
			for (Amenity a : room.getAmenities())
			{
				Amenity existing = amenityRepo.findByName(a.getName());
				if (existing == null)
				{
					throw new ApiException(422, "Amenity " + a.getName() + " does not exist.");
				}
				resolved.add(existing);
			}
		}
		room.setAmenities(resolved);
		room.setId(null);
		return roomRepo.save(room);
	}
	
	private boolean hasAmenity(Room room, String amenityName)
	{
		return room.getAmenities().stream()
				.anyMatch(a -> a.getName().equalsIgnoreCase(amenityName));
	}
}
