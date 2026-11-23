package app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.entity.Availability;
import app.entity.Room;
import app.entity.Student;
import app.repository.AvailabilityRepository;
import app.repository.StudentRepository;
import app.rest.dto.AvailabilityRequest;
import app.rest.dto.RoomStatus;
import app.rest.errorhandlers.ApiException;
import app.validation.ReportIntervalValidator;

@Service
public class ReportService
{
	private static final Logger log = LoggerFactory.getLogger(ReportService.class);
	@Autowired
	private RoomStatusService statusService;
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
			throw new ApiException(409, ReportIntervalValidator.MESSAGE);
		}
		// Status before this report decides whether it is a change to vacant.
		boolean wasVacant = statusService.compute(room).status == RoomStatus.VACANT;
		Availability report = new Availability(null, student, room, dto.getOccupied(), LocalDateTime.now(), true);
		Availability saved = availabilityRepo.save(report);
		if (!saved.isOccupied() && !wasVacant)
		{
			try
			{
				notificationService.notifyMatchingSubscribers(room, student.getId());
			}
			catch (RuntimeException e)
			{
				// a failed notification must never fail the report
				log.warn("Notification failed: {}", e.toString());
			}
		}
		return saved;
	}

	public List<Availability> findAll(Long roomId)
	{
		if (roomId != null)
		{
			return getReportsForRoom(roomId);
		}
		return availabilityRepo.findAllByOrderByReportedAtDescIdDesc();
	}
	public Availability get(Long id)
	{
		return availabilityRepo.findById(id)
				.orElseThrow(() -> new ApiException(404, "Report with ID " + id + " does not exist."));
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
