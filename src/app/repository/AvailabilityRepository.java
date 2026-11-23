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
	// newest report of the room, null when the room has none
	Availability findTopByRoomOrderByReportedAtDesc(Room room);

	// newest report by this student for this room (highest id), null when none
	@Query("select a from availability_table a where a.student.id = :studentId and a.room.id = :roomId "
			+ "and a.id = (select max(b.id) from availability_table b "
			+ "where b.student.id = :studentId and b.room.id = :roomId)")
	Availability findLatestByStudentAndRoom(@Param("studentId") Long studentId, @Param("roomId") Long roomId);

	List<Availability> findByReportedAtBefore(LocalDateTime time);

	// One row per hour of day that has reports: [0] hour 0-23 (Integer), [1] vacant reports (Long),
	// [2] all reports (Long). Use HourlyCount.fromRow(row) to convert.
	@Query("select hour(a.reportedAt), sum(case when a.occupied = false then 1 else 0 end), count(a) "
			+ "from availability_table a where a.room.id = :roomId "
			+ "group by hour(a.reportedAt) order by hour(a.reportedAt)")
	List<Object[]> countByHour(@Param("roomId") Long roomId);

	// Not in the spec, but needed for GET /rooms/{id}/availability and for deletes:
	List<Availability> findByRoomOrderByReportedAtDesc(Room room);

	// derived deletes need an active transaction (@Transactional on the calling service method)
	void deleteByRoom(Room room);

	void deleteByStudent(Student student);
}
