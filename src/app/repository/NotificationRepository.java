package app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.entity.Notification;
import app.entity.Student;

public interface NotificationRepository extends JpaRepository<Notification, Long>
{
	List<Notification> findByStudentOrderBySentAtDescIdDesc(Student student);

	List<Notification> findAllByOrderBySentAtDescIdDesc();

	void deleteByStudent(Student student);
}
