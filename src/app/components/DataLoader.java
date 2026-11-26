package app.components;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.entity.Amenity;
import app.entity.Room;
import app.entity.Student;
import app.entity.Subscription;
import app.repository.AmenityRepository;
import app.repository.RoomRepository;
import app.repository.StudentRepository;
import app.repository.SubscriptionRepository;

@Component
public class DataLoader
{
	@Autowired
	private AmenityRepository amenityRepo;

	@Autowired
	private RoomRepository roomRepo;

	@Autowired
	private StudentRepository studentRepo;

	@Autowired
	private SubscriptionRepository subscriptionRepo;

	@PostConstruct
	public void init()
	{
		if (amenityRepo.count() == 0)
		{
			for (String name : new String[] { "Outlets", "Aircon", "Whiteboard" })
			{
				amenityRepo.save(new Amenity(null, name));
			}
		}
		if (roomRepo.count() == 0)
		{
			roomRepo.save(room("F-204", "Faura", 2, 30, "Outlets", "Aircon"));
			roomRepo.save(room("F-227", "Faura", 2, 35, "Whiteboard", "Aircon"));
			roomRepo.save(room("SEC-A 210", "SEC-A", 2, 40, "Aircon", "Whiteboard", "Outlets"));
			roomRepo.save(room("CTC 102", "CTC", 1, 45, "Aircon", "Outlets"));
			roomRepo.save(room("K-301", "Kostka", 3, 30, "Whiteboard", "Outlets"));
		}
		if (studentRepo.count() == 0)
		{
			studentRepo.save(new Student(null, "Juan Dela Cruz", "juan.delacruz@example.com", "09171234567"));
			studentRepo.save(new Student(null, "Maria Santos", "maria.santos@example.com", "09181234567"));
			studentRepo.save(new Student(null, "Paolo Reyes", "paolo.reyes@example.com", "+639191234567"));
		}
		if (subscriptionRepo.count() == 0)
		{
			Student student = studentRepo.findByEmail("juan.delacruz@example.com");
			Room room = roomRepo.findByBuildingIgnoreCaseOrderByName("Faura").stream()
					.filter(r -> "F-204".equals(r.getName())).findFirst().orElse(null);
			if (student != null && room != null)
			{
				subscriptionRepo.save(new Subscription(null, student, room));
			}
		}
	}

	private Room room(String name, String building, int floor, int capacity, String... amenityNames)
	{
		Room room = new Room(null, name, building, floor, capacity);
		Set<Amenity> amenities = new HashSet<>();
		for (String amenityName : amenityNames)
		{
			Amenity a = amenityRepo.findByName(amenityName);
			if (a != null)
			{
				amenities.add(a);
			}
		}
		room.setAmenities(amenities);
		return room;
	}
}
