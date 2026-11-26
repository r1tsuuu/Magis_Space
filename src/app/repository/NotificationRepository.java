package app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.entity.Notification;
import app.entity.Student;

public interface NotificationRepository extends JpaRepository<Notification, Long>
{
	List<Notification> findByStudentOrderBySentAtDesc(Student student);

	void deleteByStudent(Student student);
}
