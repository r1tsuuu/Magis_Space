package app.components;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import app.entity.Amenity;
import app.entity.Room;
import app.repository.AmenityRepository;
import app.repository.RoomRepository;

// Amenities must exist before rooms can reference them.
@Component
@DependsOn("amenityInitializer")
public class RoomInitializer 
{

	@Autowired
	private RoomRepository repo;
	
	@Autowired
	private AmenityRepository amenityRepo;
	
	@PostConstruct
	public void populate()
	{
		if (repo.count() == 0)
		{
			// TODO: fill in map and lat/long coordinates once we have the campus map
			repo.save(withAmenities(new Room(null, "SEC-A 210", "SEC-A", 2, 40, null, null, null, null),
					"Projector", "Air Conditioning", "Whiteboard", "Outlets"));
			repo.save(withAmenities(new Room(null, "SEC-B 305", "SEC-B", 3, 30, null, null, null, null),
					"Projector", "Wi-Fi", "Air Conditioning"));
			repo.save(withAmenities(new Room(null, "F-227", "Faura Hall", 2, 35, null, null, null, null),
					"Whiteboard", "Air Conditioning"));
			repo.save(withAmenities(new Room(null, "CTC 102", "CTC", 1, 45, null, null, null, null),
					"Projector", "Wi-Fi", "Air Conditioning", "Outlets"));
			repo.save(withAmenities(new Room(null, "B-204", "Berchmans Hall", 2, 25, null, null, null, null),
					"Whiteboard"));
			repo.save(withAmenities(new Room(null, "K-301", "Kostka Hall", 3, 30, null, null, null, null),
					"Projector", "Whiteboard", "Outlets"));
		}
	}
	
	private Room withAmenities(Room room, String... amenityNames)
	{
		Set<Amenity> amenities = new HashSet<>();
		for (String name : amenityNames)
		{
			Amenity a = amenityRepo.findByName(name);
			if (a != null)
			{
				amenities.add(a);
			}
		}
		room.setAmenities(amenities);
		return room;
	}
	
}
