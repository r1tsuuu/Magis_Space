package app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.entity.Student;
import app.repository.AvailabilityRepository;
import app.repository.NotificationRepository;
import app.repository.StudentRepository;
import app.repository.SubscriptionRepository;
import app.rest.dto.StudentRequest;
import app.rest.errorhandlers.ApiException;

@Service
public class StudentService
{
	@Autowired
	private StudentRepository repo;

	@Autowired
	private AvailabilityRepository availabilityRepo;

	@Autowired
	private SubscriptionRepository subscriptionRepo;

	@Autowired
	private NotificationRepository notificationRepo;

	public List<Student> findAll()
	{
		return repo.findAll();
	}

	public Student get(Long id)
	{
		return repo.findById(id)
				.orElseThrow(() -> new ApiException(404, "Student with ID " + id + " does not exist."));
	}

	public Student create(StudentRequest dto)
	{
		String email = dto.getEmail().trim();
		if (repo.findByEmail(email) != null)
		{
			throw new ApiException(409, "Email " + email + " is already registered.");
		}
		Student student = new Student();
		student.setName(dto.getName().trim());
		student.setEmail(email);
		student.setPhone(dto.getPhone());
		return repo.save(student);
	}

	// Deletes the student's reports, subscriptions and notifications first, then the student.
	@Transactional
	public void delete(Long id)
	{
		Student student = get(id);
		availabilityRepo.deleteByStudent(student);
		subscriptionRepo.deleteByStudent(student);
		notificationRepo.deleteByStudent(student);
		repo.delete(student);
	}
}
