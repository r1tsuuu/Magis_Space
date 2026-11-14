package app.entity;

import java.time.DayOfWeek;
import java.time.LocalTime;

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

// Placeholder for marking rooms occupied during classes. Not used by any endpoint yet.
@Entity(name = "class_schedule_table")
public class ClassSchedule 
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "room_id")
	@NotNull
	private Room room;
	
	@Column
	@NotNull
	private String courseCode;
	
	@Column
	@Enumerated(EnumType.STRING)
	@NotNull
	private DayOfWeek dayOfWeek;
	
	@Column
	@NotNull
	private LocalTime startTime;
	
	@Column
	@NotNull
	private LocalTime endTime;
	
	public ClassSchedule()
	{
		
	}

	public ClassSchedule(Long id, Room room, String courseCode, DayOfWeek dayOfWeek, LocalTime startTime,
			LocalTime endTime) {
		this.id = id;
		this.room = room;
		this.courseCode = courseCode;
		this.dayOfWeek = dayOfWeek;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonIgnore
	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
	
	public Long getRoomId() {
		return room == null ? null : room.getId();
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	@Override
	public String toString() {
		return "ClassSchedule [id=" + id + ", roomId=" + getRoomId() + ", courseCode=" + courseCode + ", dayOfWeek="
				+ dayOfWeek + ", startTime=" + startTime + ", endTime=" + endTime + "]";
	}
	
}
