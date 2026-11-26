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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import app.validation.TimeRange;
import app.validation.ValidTimeRange;

@Entity(name = "class_schedule_table")
@ValidTimeRange
public class ClassSchedule implements TimeRange
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
	@Enumerated(EnumType.STRING)
	@NotNull
	private DayOfWeek dayOfWeek;

	@Column
	@NotNull
	private LocalTime startTime;

	@Column
	@NotNull
	private LocalTime endTime;

	@Column(name = "is_async")
	private boolean async = false;

	public ClassSchedule()
	{
	}

	public ClassSchedule(Long id, Room room, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime,
			boolean async) {
		this.id = id;
		this.room = room;
		this.dayOfWeek = dayOfWeek;
		this.startTime = startTime;
		this.endTime = endTime;
		this.async = async;
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

	@JsonProperty("roomId")
	public Long roomId() {
		return room == null ? null : room.getId();
	}

	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	@Override
	@JsonFormat(pattern = "HH:mm")
	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	@Override
	@JsonFormat(pattern = "HH:mm")
	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	@JsonProperty("isAsync")
	public boolean isAsync() {
		return async;
	}

	@JsonProperty("isAsync")
	public void setAsync(boolean async) {
		this.async = async;
	}

	@Override
	public String toString() {
		return "ClassSchedule [id=" + id + ", roomId=" + roomId() + ", dayOfWeek=" + dayOfWeek + ", startTime="
				+ startTime + ", endTime=" + endTime + ", async=" + async + "]";
	}
}
