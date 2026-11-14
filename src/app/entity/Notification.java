package app.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name = "notification_table")
public class Notification 
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
	@NotNull
	private String message;
	
	@Column
	@NotNull
	private LocalDateTime createdAt;
	
	// "read" is a reserved word in MySQL, so the column is named is_read
	@Column(name = "is_read")
	@NotNull
	private Boolean read = false;
	
	public Notification()
	{
		
	}

	public Notification(Long id, Student student, Room room, String message, LocalDateTime createdAt, Boolean read) {
		this.id = id;
		this.student = student;
		this.room = room;
		this.message = message;
		this.createdAt = createdAt;
		this.read = read;
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
	
	public Long getStudentId() {
		return student == null ? null : student.getId();
	}
	
	public Long getRoomId() {
		return room == null ? null : room.getId();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Boolean getRead() {
		return read;
	}

	public void setRead(Boolean read) {
		this.read = read;
	}

	@Override
	public String toString() {
		return "Notification [id=" + id + ", studentId=" + getStudentId() + ", roomId=" + getRoomId() + ", message="
				+ message + ", createdAt=" + createdAt + ", read=" + read + "]";
	}
	
}
