package app.rest.dto;

import javax.validation.constraints.NotNull;

public class AvailabilityRequest
{
	@NotNull
	private Long roomId;

	@NotNull
	private Long studentId;

	@NotNull
	private Boolean occupied;

	public Long getRoomId() { return roomId; }
	public void setRoomId(Long roomId) { this.roomId = roomId; }
	public Long getStudentId() { return studentId; }
	public void setStudentId(Long studentId) { this.studentId = studentId; }
	public Boolean getOccupied() { return occupied; }
	public void setOccupied(Boolean occupied) { this.occupied = occupied; }
}
