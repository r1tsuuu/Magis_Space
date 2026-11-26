package app.service;

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

	public List<Subscription> findAll(Long studentId)
	{
		if (studentId != null)
		{
			return repo.findByStudent(studentService.get(studentId));
		}
		return repo.findAll();
	}

	public Subscription get(Long id)
	{
		return repo.findById(id)
				.orElseThrow(() -> new ApiException(404, "Subscription with ID " + id + " does not exist."));
	}

	public Subscription create(SubscriptionRequest dto)
	{
		Student student = studentService.get(dto.getStudentId());
		Room room = roomService.getRoom(dto.getRoomId());
		if (repo.existsByStudentAndRoom(student, room))
		{
			throw new ApiException(409, "Student " + student.getId() + " is already subscribed to room "
					+ room.getId() + ".");
		}
		return repo.save(new Subscription(null, student, room));
	}

	public void delete(Long id)
	{
		repo.delete(get(id));
	}
}
