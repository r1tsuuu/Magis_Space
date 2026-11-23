package app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.entity.Availability;
import app.entity.Room;
import app.entity.Student;
import app.repository.AvailabilityRepository;
import app.repository.StudentRepository;
import app.rest.dto.AvailabilityRequest;
import app.rest.errorhandlers.ApiException;
import app.validation.ReportIntervalValidator;

@Service
public class ReportService
{
	@Autowired
	private AvailabilityRepository availabilityRepo;

	@Autowired
	private StudentRepository studentRepo;

	@Autowired
	private RoomService roomService;

	@Autowired
	private NotificationService notificationService;

	public Availability submitReport(AvailabilityRequest dto)
	{
		Student student = studentRepo.findById(dto.getStudentId()).orElseThrow(
				() -> new ApiException(404, "Student with ID " + dto.getStudentId() + " does not exist."));
		Room room = roomService.getRoom(dto.getRoomId());
		if (!ReportIntervalValidator.isIntervalOk(availabilityRepo, student.getId(), room.getId()))
		{
			throw new ApiException(409, "You already reported this room in the last "
					+ ReportIntervalValidator.MIN_MINUTES + " minutes.");
		}
		Availability report = new Availability(null, student, room, dto.getOccupied(), LocalDateTime.now(), true);
		Availability saved = availabilityRepo.save(report);
		if (!saved.isOccupied())
		{
			try
			{
				notificationService.notifyMatchingSubscribers(room);
			}
			catch (RuntimeException e)
			{
				// a failed notification must never fail the report
				System.err.println("Notification failed: " + e.getMessage());
			}
		}
		return saved;
	}

	public List<Availability> getReportsForRoom(Long roomId)
	{
		Room room = roomService.getRoom(roomId);
		return availabilityRepo.findByRoomOrderByReportedAtDesc(room);
	}

	@Transactional
	public void deleteReport(Long id)
	{
		if (!availabilityRepo.existsById(id))
		{
			throw new ApiException(404, "Report with ID " + id + " does not exist.");
		}
		availabilityRepo.deleteById(id);
	}
}
