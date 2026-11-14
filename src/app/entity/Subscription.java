package app.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity(name = "subscription_table")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "room_id"}))
public class Subscription 
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
	private LocalDateTime createdAt;
	
	public Subscription()
	{
		
	}

	public Subscription(Long id, Student student, Room room, LocalDateTime createdAt) {
		this.id = id;
		this.student = student;
		this.room = room;
		this.createdAt = createdAt;
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
	
	// Not named getStudentId()/getRoomId(): Spring Data would read findByRoomId as
	// that getter instead of room.id and fail at startup.
	@JsonProperty("studentId")
	public Long studentId() {
		return student == null ? null : student.getId();
	}
	
	@JsonProperty("roomId")
	public Long roomId() {
		return room == null ? null : room.getId();
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "Subscription [id=" + id + ", studentId=" + studentId() + ", roomId=" + roomId() + ", createdAt="
				+ createdAt + "]";
	}
	
}
