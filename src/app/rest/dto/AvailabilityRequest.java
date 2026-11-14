package app.rest.dto;

import javax.validation.constraints.NotNull;

public class AvailabilityRequest
{
	@NotNull
	private Long studentId;
	
	@NotNull
	private Long roomId;
	
	@NotNull
	private String status;

	public Long getStudentId() { return studentId; }
	public void setStudentId(Long studentId) { this.studentId = studentId; }

	public Long getRoomId() { return roomId; }
	public void setRoomId(Long roomId) { this.roomId = roomId; }

	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
}
