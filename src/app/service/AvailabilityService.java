package app.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.entity.Availability;
import app.entity.AvailabilityStatus;
import app.entity.Room;
import app.entity.Student;
import app.repository.AvailabilityRepository;
import app.rest.dto.AvailabilityRequest;
import app.rest.errorhandlers.ApiException;

@Service
public class AvailabilityService
{
	@Autowired
	private AvailabilityRepository repo;
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private RoomService roomService;
	
	@Autowired
	private NotificationService notificationService;
	
	// Newest first: the latest report for a room is its current status.
	public List<Availability> getReports(Long roomId, String status)
	{
		AvailabilityStatus parsedStatus = (status == null) ? null : parseStatus(status);
		
		List<Availability> reports = (roomId == null) ? repo.findAll() : repo.findByRoomId(roomId);
		
		return reports.stream()
				.filter(r -> parsedStatus == null || r.getStatus() == parsedStatus)
				.sorted(Comparator.comparing(Availability::getReportedAt).reversed())
				.collect(Collectors.toList());
	}
	
	public Availability submitReport(AvailabilityRequest request)
	{
		AvailabilityStatus status = parseStatus(request.getStatus());
		
		Student student = studentService.getStudent(request.getStudentId());
		Room room = roomService.getRoom(request.getRoomId());
		
		// Read the previous status before saving, or the new report would be "previous".
		Availability previous = repo.findTopByRoomIdOrderByReportedAtDesc(room.getId());
		
		Availability report = new Availability(null, student, room, status, LocalDateTime.now(), true);
		Availability saved = repo.save(report);
		
		if (status == AvailabilityStatus.AVAILABLE
				&& previous != null
				&& previous.getStatus() == AvailabilityStatus.OCCUPIED)
		{
			notificationService.notifySubscribers(room, student);
		}
		
		return saved;
	}
	
	private AvailabilityStatus parseStatus(String status)
	{
		try
		{
			return AvailabilityStatus.valueOf(status.trim().toUpperCase());
		}
		catch (IllegalArgumentException e)
		{
			throw new ApiException(400, "Status must be AVAILABLE or OCCUPIED.");
		}
	}
}
