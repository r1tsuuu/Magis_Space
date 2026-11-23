package app.rest.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class NotificationRequest
{
	@NotNull
	private Long studentId;

	@NotBlank
	@Size(max = 300)
	private String message;

	public Long getStudentId() { return studentId; }
	public void setStudentId(Long studentId) { this.studentId = studentId; }
	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }
}
