package app.components;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.entity.Student;
import app.repository.StudentRepository;

@Component
public class StudentInitializer 
{

	@Autowired
	private StudentRepository repo;
	
	@PostConstruct
	public void populate()
	{
		if (repo.count() == 0)
		{
			repo.save(new Student(null, "220001", "Bobby Yap", "byap@ateneo.edu.ph", "09176763167"));
			repo.save(new Student(null, "220002", "Gretchen Ho", "gho@ateneo.edu.ph", "09171717171"));
			repo.save(new Student(null, "220003", "Juan dela Cruz", "jdelacruz@ateneo.edu.ph", "09181234567"));
		}
	}

}
