package app.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.entity.Availability;
import app.entity.Room;
import app.entity.Student;

public interface AvailabilityRepository extends JpaRepository<Availability, Long>
{
	List<Availability> findByReportedAtBefore(LocalDateTime time);

	List<Availability> findByRoomOrderByReportedAtDescIdDesc(Room room);

	List<Availability> findAllByOrderByReportedAtDescIdDesc();

	void deleteByRoom(Room room);

	void deleteByStudent(Student student);
}
