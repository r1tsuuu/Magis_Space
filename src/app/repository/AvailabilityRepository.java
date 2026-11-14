package app.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.entity.Availability;
import app.entity.AvailabilityStatus;

public interface AvailabilityRepository extends JpaRepository<Availability, Long>
{
	
	List<Availability> findByRoomId(Long roomId);
	
	List<Availability> findByStudentId(Long studentId);
	
	List<Availability> findByStatus(AvailabilityStatus status);
	
	List<Availability> findByVerifiedTrue();
	
	List<Availability> findByReportedAtBeforeAndVerifiedTrue(LocalDateTime time);
	
	List<Availability> findByRoomIdAndVerifiedTrue(Long roomId);
	
	Availability findTopByRoomIdOrderByReportedAtDesc(Long roomId);

}
