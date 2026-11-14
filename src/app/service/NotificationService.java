package app.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import app.entity.Notification;
import app.entity.Room;
import app.entity.Student;
import app.entity.Subscription;
import app.repository.NotificationRepository;
import app.repository.SubscriptionRepository;

@Service
public class NotificationService
{
	@Autowired
	private NotificationRepository notificationRepo;
	
	@Autowired
	private SubscriptionRepository subscriptionRepo;
	
	@Autowired
	private StudentService studentService;
	
	@Value("${notification.api.url:}")
	private String notificationApiUrl;
	
	@PostConstruct
	public void init()
	{
		// TODO: build the Retrofit client for the mock notification API here
		if (notificationApiUrl == null || notificationApiUrl.isEmpty())
		{
			System.out.println("[NotificationService] notification.api.url not set -- SMS sending disabled");
		}
		else
		{
			System.out.println("[NotificationService] using mock notification API at " + notificationApiUrl);
		}
	}
	
	public List<Notification> getNotifications(Long studentId, boolean unreadOnly)
	{
		studentService.getStudent(studentId);
		return unreadOnly
				? notificationRepo.findByStudentIdAndReadFalse(studentId)
				: notificationRepo.findByStudentId(studentId);
	}
	
	public void notifySubscribers(Room room, Student reporter)
	{
		List<Subscription> subs = subscriptionRepo.findByRoomId(room.getId());
		for (Subscription sub : subs)
		{
			// The reporter already knows the room is free.
			if (sub.getStudent().getId().equals(reporter.getId()))
			{
				continue;
			}
			sendNotification(sub.getStudent(), room);
		}
	}
	
	public Notification sendNotification(Student student, Room room)
	{
		LocalDateTime now = LocalDateTime.now();
		String message = room.getName() + " (" + room.getBuilding() + ") is now available as of "
				+ now.format(DateTimeFormatter.ofPattern("h:mm a")) + ".";
		
		// TODO: send through the mock notification API with Retrofit instead of printing
		System.out.println("[NotificationService] SMS to " + student.getPhone() + ": " + message);
		
		return notificationRepo.save(new Notification(null, student, room, message, now, false));
	}
}
