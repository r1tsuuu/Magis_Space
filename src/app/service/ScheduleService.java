package app.service;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.entity.ClassSchedule;
import app.entity.Room;
import app.repository.RoomRepository;
import app.repository.ScheduleRepository;
import app.rest.dto.ScheduleRequest;
import app.rest.errorhandlers.ApiException;

@Service
public class ScheduleService
{
	@Autowired
	private ScheduleRepository scheduleRepo;

	@Autowired
	private RoomRepository roomRepo;

	@Autowired
	private NotificationService notificationService;

	@Transactional
	public ClassSchedule create(ScheduleRequest dto)
	{
		Room room = loadRoom(dto.getRoomId());
		if (dto.getStartTime() == null || dto.getEndTime() == null || dto.getDayOfWeek() == null)
		{
			throw new ApiException(422, "dayOfWeek, startTime and endTime are required.");
		}
		if (!dto.getEndTime().isAfter(dto.getStartTime()))
		{
			throw new ApiException(400, "endTime must be after startTime.");
		}
		// Two slots overlap when each starts before the other ends; back-to-back slots do not overlap.
		for (ClassSchedule existing : scheduleRepo.findByRoomIdAndDayOfWeek(room.getId(), dto.getDayOfWeek()))
		{
			if (existing.getStartTime().isBefore(dto.getEndTime())
					&& dto.getStartTime().isBefore(existing.getEndTime()))
			{
				throw new ApiException(409, "Room " + room.getName() + " already has a class on "
						+ existing.getDayOfWeek() + " from " + existing.getStartTime() + " to "
						+ existing.getEndTime() + " that overlaps " + dto.getStartTime() + "-" + dto.getEndTime() + ".");
			}
		}
		boolean async = Boolean.TRUE.equals(dto.getIsAsync());
		ClassSchedule saved = scheduleRepo.save(new ClassSchedule(null, room, dto.getDayOfWeek(),
				dto.getStartTime(), dto.getEndTime(), async));
		if (async)
		{
			// An async class frees the room, so tell subscribers. A notification failure must not undo the save.
			try
			{
				notificationService.notifyMatchingSubscribers(room);
			}
			catch (RuntimeException e)
			{
				System.err.println("Could not notify subscribers: " + e.getMessage());
			}
		}
		return saved;
	}

	public List<ClassSchedule> findByRoom(Long roomId)
	{
		return scheduleRepo.findByRoom(loadRoom(roomId));
	}

	public List<ClassSchedule> findAll(Long roomId, String day)
	{
		DayOfWeek dayOfWeek = null;
		if (day != null && !day.trim().isEmpty())
		{
			try
			{
				dayOfWeek = DayOfWeek.valueOf(day.trim().toUpperCase());
			}
			catch (IllegalArgumentException e)
			{
				throw new ApiException(400, "day must be a day of the week such as MONDAY.");
			}
		}
		if (roomId != null)
		{
			loadRoom(roomId);
			return dayOfWeek == null ? scheduleRepo.findByRoomId(roomId)
					: scheduleRepo.findByRoomIdAndDayOfWeek(roomId, dayOfWeek);
		}
		return dayOfWeek == null ? scheduleRepo.findAll() : scheduleRepo.findByDayOfWeek(dayOfWeek);
	}

	public ClassSchedule get(Long id)
	{
		return scheduleRepo.findById(id)
				.orElseThrow(() -> new ApiException(404, "Schedule with ID " + id + " does not exist."));
	}

	public void delete(Long id)
	{
		scheduleRepo.delete(get(id));
	}

	private Room loadRoom(Long roomId)
	{
		if (roomId == null)
		{
			throw new ApiException(422, "roomId is required.");
		}
		return roomRepo.findById(roomId)
				.orElseThrow(() -> new ApiException(404, "Room with ID " + roomId + " does not exist."));
	}
}
