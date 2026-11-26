package app.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.entity.Availability;
import app.entity.Room;
import app.entity.Student;

public interface AvailabilityRepository extends JpaRepository<Availability, Long>
{
	Availability findTopByRoomOrderByReportedAtDesc(Room room);

	@Query("select a from availability_table a where a.student.id = :studentId and a.room.id = :roomId "
			+ "and a.id = (select max(b.id) from availability_table b "
			+ "where b.student.id = :studentId and b.room.id = :roomId)")
	Availability findLatestByStudentAndRoom(@Param("studentId") Long studentId, @Param("roomId") Long roomId);

	List<Availability> findByReportedAtBefore(LocalDateTime time);

	@Query("select hour(a.reportedAt), sum(case when a.occupied = false then 1 else 0 end), count(a) "
			+ "from availability_table a where a.room.id = :roomId "
			+ "group by hour(a.reportedAt) order by hour(a.reportedAt)")
	List<Object[]> countByHour(@Param("roomId") Long roomId);

	List<Availability> findByRoomOrderByReportedAtDesc(Room room);

	List<Availability> findAllByOrderByReportedAtDescIdDesc();

	void deleteByRoom(Room room);

	void deleteByStudent(Student student);
}
