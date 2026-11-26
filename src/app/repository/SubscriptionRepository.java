package app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.entity.Room;
import app.entity.Student;
import app.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>
{
	List<Subscription> findByStudent(Student student);

	List<Subscription> findByRoom(Room room);

	boolean existsByStudentAndRoom(Student student, Room room);

	void deleteByStudent(Student student);

	void deleteByRoom(Room room);
}
