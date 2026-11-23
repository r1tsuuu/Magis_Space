package app.rest.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import app.validation.TimeRange;
import app.validation.ValidTimeRange;

// JSON: {"roomId":1,"dayOfWeek":"THURSDAY","startTime":"13:00","endTime":"14:30","isAsync":true}
// isAsync is optional and defaults to false.
@ValidTimeRange
public class ScheduleRequest implements TimeRange
{
	@NotNull
	private Long roomId;

	@NotNull
	private DayOfWeek dayOfWeek;

	@NotNull
	private LocalTime startTime;

	@NotNull
	private LocalTime endTime;

	@JsonProperty("isAsync")
	private Boolean isAsync = false;

	public Long getRoomId() { return roomId; }
	public void setRoomId(Long roomId) { this.roomId = roomId; }
	public DayOfWeek getDayOfWeek() { return dayOfWeek; }
	public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }
	@Override
	public LocalTime getStartTime() { return startTime; }
	public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
	@Override
	public LocalTime getEndTime() { return endTime; }
	public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

	@JsonProperty("isAsync")
	public Boolean getIsAsync() { return isAsync; }
	@JsonProperty("isAsync")
	public void setIsAsync(Boolean isAsync) { this.isAsync = isAsync == null ? Boolean.FALSE : isAsync; }
}
