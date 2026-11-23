package app.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.entity.Availability;
import app.entity.ClassSchedule;
import app.entity.Room;
import app.repository.AvailabilityRepository;
import app.repository.ScheduleRepository;
import app.rest.dto.RoomStatus;

// Works out the live status of a room from its latest report and the class schedule.
@Service
public class RoomStatusService
{
	public static final int EXPIRY_MINUTES = 60;

	@Autowired
	private AvailabilityRepository availabilityRepo;

	@Autowired
	private ScheduleRepository scheduleRepo;

	public static class Info
	{
		public final RoomStatus status;
		public final boolean predictedVacant;
		public final LocalDateTime lastReportedAt;
		public final Long minutesAgo;

		Info(RoomStatus status, boolean predictedVacant, LocalDateTime lastReportedAt, Long minutesAgo)
		{
			this.status = status;
			this.predictedVacant = predictedVacant;
			this.lastReportedAt = lastReportedAt;
			this.minutesAgo = minutesAgo;
		}
	}

	public Info compute(Room room)
	{
		return compute(room, LocalDateTime.now());
	}

	public Info compute(Room room, LocalDateTime now)
	{
		Availability latest = availabilityRepo.findTopByRoomOrderByReportedAtDesc(room);
		RoomStatus status;
		LocalDateTime reportedAt = null;
		Long minutes = null;
		if (latest == null)
		{
			status = RoomStatus.NO_DATA;
		}
		else
		{
			reportedAt = latest.getReportedAt();
			minutes = Math.max(0, Duration.between(reportedAt, now).toMinutes());
			if (minutes > EXPIRY_MINUTES)
			{
				status = RoomStatus.UNVERIFIED;
			}
			else
			{
				status = latest.isOccupied() ? RoomStatus.OCCUPIED : RoomStatus.VACANT;
			}
		}
		boolean predicted = false;
		if (status == RoomStatus.NO_DATA || status == RoomStatus.UNVERIFIED)
		{
			List<ClassSchedule> current = scheduleRepo.findCurrentClass(room.getId(), now.getDayOfWeek(),
					now.toLocalTime());
			predicted = current.isEmpty() || current.stream().allMatch(ClassSchedule::isAsync);
		}
		return new Info(status, predicted, reportedAt, minutes);
	}
}
