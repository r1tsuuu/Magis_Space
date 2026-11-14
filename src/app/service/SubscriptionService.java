package app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.entity.Room;
import app.entity.Student;
import app.entity.Subscription;
import app.repository.SubscriptionRepository;
import app.rest.dto.SubscriptionRequest;
import app.rest.errorhandlers.ApiException;

@Service
public class SubscriptionService
{
	@Autowired
	private SubscriptionRepository repo;
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private RoomService roomService;
	
	public List<Subscription> getSubscriptions(Long studentId)
	{
		studentService.getStudent(studentId);
		return repo.findByStudentId(studentId);
	}
	
	public Subscription subscribe(SubscriptionRequest request)
	{
		Student student = studentService.getStudent(request.getStudentId());
		Room room = roomService.getRoom(request.getRoomId());
		
		if (repo.findByStudentIdAndRoomId(student.getId(), room.getId()) != null)
		{
			throw new ApiException(409, "Student " + student.getId() + " is already subscribed to room " + room.getId() + ".");
		}
		return repo.save(new Subscription(null, student, room, LocalDateTime.now()));
	}
}
