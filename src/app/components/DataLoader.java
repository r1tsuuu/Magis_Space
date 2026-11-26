package app.components;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.entity.Amenity;
import app.entity.ClassSchedule;
import app.entity.Room;
import app.entity.Student;
import app.entity.Subscription;
import app.repository.AmenityRepository;
import app.repository.RoomRepository;
import app.repository.ScheduleRepository;
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

	@Autowired
	private ScheduleRepository scheduleRepo;

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
			roomRepo.save(room("F-204", "Faura", 2, 30, 120.0, 45.0, 14.6395, 121.0780, "Outlets", "Aircon"));
			roomRepo.save(room("F-227", "Faura", 2, 35, 135.0, 45.0, 14.6392, 121.0780, "Whiteboard", "Aircon"));
			roomRepo.save(room("SEC-A 210", "SEC-A", 2, 40, 60.0, 20.0, 14.6401, 121.0772,
					"Aircon", "Whiteboard", "Outlets"));
			roomRepo.save(room("SEC-B 305", "SEC-B", 3, 30, 75.0, 20.0, 14.6403, 121.0775, "Aircon"));
			roomRepo.save(room("CTC 102", "CTC", 1, 45, 180.0, 90.0, 14.6386, 121.0788, "Aircon", "Outlets"));
			roomRepo.save(room("B-204", "Berchmans", 2, 25, 150.0, 70.0, 14.6397, 121.0784, "Whiteboard"));
			roomRepo.save(room("K-301", "Kostka", 3, 30, 100.0, 60.0, 14.6395, 121.0775, "Whiteboard", "Outlets"));
		}
		if (studentRepo.count() == 0)
		{
			studentRepo.save(new Student(null, "Juan Dela Cruz", "juan.delacruz@example.com", "09171234567"));
		}
		Student student = studentRepo.findByEmail("juan.delacruz@example.com");
		if (student != null && subscriptionRepo.count() == 0)
		{
			subscriptionRepo.save(new Subscription(null, student, "Faura", null, amenityRepo.findByName("Outlets")));
		}
		if (scheduleRepo.count() == 0)
		{
			for (Room r : roomRepo.findAll())
			{
				if ("F-204".equals(r.getName()) && "Faura".equals(r.getBuilding()))
				{
					scheduleRepo.save(new ClassSchedule(null, r, DayOfWeek.THURSDAY, LocalTime.of(13, 0),
							LocalTime.of(14, 30), true));
					scheduleRepo.save(new ClassSchedule(null, r, DayOfWeek.MONDAY, LocalTime.of(9, 0),
							LocalTime.of(10, 30), false));
				}
			}
		}
	}

	private Room room(String name, String building, int floor, int capacity, double mapX, double mapY,
			double latitude, double longitude, String... amenityNames)
	{
		Room room = new Room(null, name, building, floor, capacity, mapX, mapY, latitude, longitude);
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
