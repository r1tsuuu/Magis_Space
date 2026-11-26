package app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.entity.Availability;
import app.entity.Room;
import app.entity.Student;
import app.repository.AvailabilityRepository;
import app.rest.dto.AvailabilityRequest;
import app.rest.errorhandlers.ApiException;

@Service
public class ReportService
{
	private static final Logger log = LoggerFactory.getLogger(ReportService.class);

	@Autowired
	private AvailabilityRepository availabilityRepo;

	@Autowired
	private StudentService studentService;

	@Autowired
	private RoomService roomService;

	@Autowired
	private NotificationService notificationService;

	public Availability submitReport(AvailabilityRequest dto)
	{
		Student student = studentService.get(dto.getStudentId());
		Room room = roomService.getRoom(dto.getRoomId());
		Availability saved = availabilityRepo
				.save(new Availability(null, student, room, dto.getOccupied(), LocalDateTime.now(), true));
		if (!saved.isOccupied())
		{
			try
			{
				notificationService.notifySubscribers(room);
			}
			catch (RuntimeException e)
			{
				log.warn("Could not notify subscribers of room {}: {}", room.getId(), e.toString());
			}
		}
		return saved;
	}

	public List<Availability> findAll(Long roomId)
	{
		if (roomId != null)
		{
			return availabilityRepo.findByRoomOrderByReportedAtDescIdDesc(roomService.getRoom(roomId));
		}
		return availabilityRepo.findAllByOrderByReportedAtDescIdDesc();
	}

	public Availability get(Long id)
	{
		return availabilityRepo.findById(id)
				.orElseThrow(() -> new ApiException(404, "Report with ID " + id + " does not exist."));
	}

	public void deleteReport(Long id)
	{
		availabilityRepo.delete(get(id));
	}
}
