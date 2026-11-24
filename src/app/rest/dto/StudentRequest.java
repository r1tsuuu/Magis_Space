package app.rest.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import app.entity.Student;

public class StudentRequest
{
	@NotBlank
	@Size(max = 100)
	private String name;

	@NotBlank
	@Email
	@Size(max = 100)
	private String email;

	@NotBlank
	@Pattern(regexp = Student.PHONE_REGEX, message = "must be a Philippine mobile number like 09171234567 or +639171234567")
	private String phone;

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getPhone() { return phone; }
	public void setPhone(String phone) { this.phone = phone; }
}
