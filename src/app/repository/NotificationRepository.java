package app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>
{
	
	List<Notification> findByStudentId(Long studentId);
	
	List<Notification> findByStudentIdAndReadFalse(Long studentId);

}
