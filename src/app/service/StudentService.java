package app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.entity.Student;
import app.repository.StudentRepository;
import app.rest.errorhandlers.ApiException;

@Service
public class StudentService
{
	@Autowired
	private StudentRepository repo;
	
	public List<Student> getAllStudents()
	{
		return repo.findAll();
	}
	
	public Student getStudent(Long id)
	{
		return repo.findById(id)
				.orElseThrow(() -> new ApiException(404, "Student with ID " + id + " does not exist."));
	}
	
	public Student createStudent(Student student)
	{
		if (repo.findByStudentNumber(student.getStudentNumber()) != null)
		{
			throw new ApiException(409, "Student number " + student.getStudentNumber() + " is already registered.");
		}
		if (repo.findByEmail(student.getEmail()) != null)
		{
			throw new ApiException(409, "Email " + student.getEmail() + " is already registered.");
		}
		student.setId(null);
		return repo.save(student);
	}
}
