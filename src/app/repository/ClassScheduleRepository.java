package app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.entity.ClassSchedule;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long>
{
	
	List<ClassSchedule> findByRoomId(Long roomId);

}
