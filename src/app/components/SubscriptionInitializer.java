package app.components;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import app.entity.Room;
import app.entity.Student;
import app.entity.Subscription;
import app.repository.RoomRepository;
import app.repository.StudentRepository;
import app.repository.SubscriptionRepository;

@Component
@DependsOn({"studentInitializer", "roomInitializer"})
public class SubscriptionInitializer
{
	@Autowired
	private SubscriptionRepository repo;
	
	@Autowired
	private StudentRepository studentRepo;
	
	@Autowired
	private RoomRepository roomRepo;
	
	@PostConstruct
	public void populate()
	{
		if (repo.count() == 0)
		{
			Student gretchen = studentRepo.findByStudentNumber("220002");
			Room secA = roomRepo.findByName("SEC-A 210");
			if (gretchen != null && secA != null)
			{
				repo.save(new Subscription(null, gretchen, secA, LocalDateTime.now()));
			}
		}
	}
}
