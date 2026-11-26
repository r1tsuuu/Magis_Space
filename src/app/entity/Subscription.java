package app.entity;

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
@Table(name = "subscription_table", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "room_id"}))
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

	public Subscription()
	{
	}

	public Subscription(Long id, Student student, Room room) {
		this.id = id;
		this.student = student;
		this.room = room;
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

	@JsonProperty("studentId")
	public Long studentId() {
		return student == null ? null : student.getId();
	}

	@JsonIgnore
	public Room getRoom() {
		return room;
	}
	public void setRoom(Room room) {
		this.room = room;
	}
	@JsonProperty("roomId")
	public Long roomId() {
		return room == null ? null : room.getId();
	}
	@Override
	public String toString() {
		return "Subscription [id=" + id + ", studentId=" + studentId() + ", roomId=" + roomId() + "]";
	}
}
