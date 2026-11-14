package app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>
{
	
	List<Subscription> findByRoomId(Long roomId);
	
	List<Subscription> findByStudentId(Long studentId);
	
	Subscription findByStudentIdAndRoomId(Long studentId, Long roomId);

}
