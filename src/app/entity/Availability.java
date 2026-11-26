package app.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
	private boolean occupied;

	@Column
	@NotNull
	private LocalDateTime reportedAt;

	@Column
	private boolean verified = true;

	public Availability()
	{
	}

	public Availability(Long id, Student student, Room room, boolean occupied, LocalDateTime reportedAt,
			boolean verified) {
		this.id = id;
		this.student = student;
		this.room = room;
		this.occupied = occupied;
		this.reportedAt = reportedAt;
		this.verified = verified;
	}

	@PrePersist
	void defaultReportedAt()
	{
		if (reportedAt == null)
		{
			reportedAt = LocalDateTime.now();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	@JsonProperty("studentId")
	public Long studentId() {
		return student == null ? null : student.getId();
	}

	@JsonProperty("roomId")
	public Long roomId() {
		return room == null ? null : room.getId();
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}

	public LocalDateTime getReportedAt() {
		return reportedAt;
	}

	public void setReportedAt(LocalDateTime reportedAt) {
		this.reportedAt = reportedAt;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	@Override
	public String toString() {
		return "Availability [id=" + id + ", studentId=" + studentId() + ", roomId=" + roomId() + ", occupied="
				+ occupied + ", reportedAt=" + reportedAt + ", verified=" + verified + "]";
	}
}
