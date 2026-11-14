package app.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name = "availability_table")
public class Availability 
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "student_id")
	@NotNull
	private Student student;
	
	@ManyToOne
	@JoinColumn(name = "room_id")
	@NotNull
	private Room room;
	
	@Column
	@Enumerated(EnumType.STRING)
	@NotNull
	private AvailabilityStatus status;
	
	@Column
	@NotNull
	private LocalDateTime reportedAt;
	
	@Column
	@NotNull
	private Boolean verified = true;
	
	public Availability()
	{
		
	}

	public Availability(Long id, Student student, Room room, AvailabilityStatus status, LocalDateTime reportedAt,
			Boolean verified) {
		this.id = id;
		this.student = student;
		this.room = room;
		this.status = status;
		this.reportedAt = reportedAt;
		this.verified = verified;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	// JSON exposes studentId/roomId instead of the nested objects.
	@JsonIgnore
	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	@JsonIgnore
	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
	
	public Long getStudentId() {
		return student == null ? null : student.getId();
	}
	
	public Long getRoomId() {
		return room == null ? null : room.getId();
	}

	public AvailabilityStatus getStatus() {
		return status;
	}

	public void setStatus(AvailabilityStatus status) {
		this.status = status;
	}

	public LocalDateTime getReportedAt() {
		return reportedAt;
	}

	public void setReportedAt(LocalDateTime reportedAt) {
		this.reportedAt = reportedAt;
	}

	public Boolean getVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
	}

	@Override
	public String toString() {
		return "Availability [id=" + id + ", studentId=" + getStudentId() + ", roomId=" + getRoomId() + ", status="
				+ status + ", reportedAt=" + reportedAt + ", verified=" + verified + "]";
	}
	
}
