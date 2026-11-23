package app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.entity.Amenity;
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
	private AmenityService amenityService;

	public List<Subscription> findAll()
	{
		return repo.findAll();
	}

	public Subscription get(Long id)
	{
		return repo.findById(id)
				.orElseThrow(() -> new ApiException(404, "Subscription with ID " + id + " does not exist."));
	}

	public List<Subscription> findByStudent(Long studentId)
	{
		return repo.findByStudent(studentService.get(studentId));
	}

	public Subscription create(SubscriptionRequest dto)
	{
		Student student = studentService.get(dto.getStudentId());
		Amenity amenity = dto.getAmenityId() == null ? null : amenityService.get(dto.getAmenityId());
		Subscription sub = new Subscription();
		sub.setStudent(student);
		sub.setBuilding(dto.getBuilding().trim());
		sub.setMinCapacity(dto.getMinCapacity());
		sub.setAmenity(amenity);
		return repo.save(sub);
	}

	public void delete(Long id)
	{
		repo.delete(get(id));
	}
}
