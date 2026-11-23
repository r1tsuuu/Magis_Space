package app.repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.entity.ClassSchedule;
import app.entity.Room;

public interface ScheduleRepository extends JpaRepository<ClassSchedule, Long>
{
	List<ClassSchedule> findByRoom(Room room);

	// Classes of the room in progress at that day/time (startTime <= time < endTime).
	// Empty list = no class. Normally at most one element.
	@Query("select c from class_schedule_table c where c.room.id = :roomId and c.dayOfWeek = :day "
			+ "and c.startTime <= :time and c.endTime > :time")
	List<ClassSchedule> findCurrentClass(@Param("roomId") Long roomId, @Param("day") DayOfWeek day,
			@Param("time") LocalTime time);

	// Not in the spec, for GET /schedules?roomId=&day=:
	List<ClassSchedule> findByRoomId(Long roomId);

	List<ClassSchedule> findByDayOfWeek(DayOfWeek day);

	List<ClassSchedule> findByRoomIdAndDayOfWeek(Long roomId, DayOfWeek day);

	// derived delete: needs an active transaction (@Transactional on the calling service method)
	void deleteByRoom(Room room);
}
