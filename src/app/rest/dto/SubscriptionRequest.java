package app.rest.dto;

import javax.validation.constraints.NotNull;

public class SubscriptionRequest
{
	@NotNull
	private Long studentId;
	
	@NotNull
	private Long roomId;

	public Long getStudentId() { return studentId; }
	public void setStudentId(Long studentId) { this.studentId = studentId; }

	public Long getRoomId() { return roomId; }
	public void setRoomId(Long roomId) { this.roomId = roomId; }
}
