package app.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity(name="student_table")
public class Student 
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;
	
	@Column(unique = true)
	@NotBlank
	private String studentNumber;
	
	@Column
	@NotNull
	@Size(max = 100)
	private String name;
	
	@Column(unique = true)
	@NotNull
	@Email
	private String email;
	
	@Column
	@NotNull
	private String phone;

	public Student(){
		
	}
	
	public Student(Long id, String studentNumber, String name, String email, String phone) {
		this.id = id;
		this.studentNumber = studentNumber;
		this.name = name;
		this.email = email;
		this.phone = phone;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStudentNumber() {
		return studentNumber;
	}

	public void setStudentNumber(String studentNumber) {
		this.studentNumber = studentNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", studentNumber=" + studentNumber + ", name=" + name + ", email=" + email
				+ ", phone=" + phone + "]";
	}
	
}
