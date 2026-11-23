package app.repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.entity.Amenity;
import app.entity.Room;
import app.entity.Student;
import app.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>
{
	// Subscriptions whose condition the room satisfies: same building (ignoring case), minCapacity null or
	// <= capacity, amenity null or one of amenityIds. amenityIds must not be empty
	// (use findMatchingForRoom, which handles that).
	@Query("select s from subscription_table s where lower(s.building) = lower(:building) "
			+ "and (s.minCapacity is null or s.minCapacity <= :capacity) "
			+ "and (s.amenity is null or s.amenity.id in :amenityIds)")
	List<Subscription> findMatching(@Param("building") String building, @Param("capacity") int capacity,
			@Param("amenityIds") Collection<Long> amenityIds);

	default List<Subscription> findMatchingForRoom(Room room)
	{
		Collection<Long> ids = room.getAmenities().stream().map(Amenity::getId).collect(Collectors.toList());
		if (ids.isEmpty())
		{
			ids = Collections.singleton(-1L);   // never matches, avoids an empty IN ()
		}
		return findMatching(room.getBuilding(), room.getCapacity(), ids);
	}

	List<Subscription> findByStudent(Student student);

	// derived deletes need an active transaction (@Transactional on the calling service method)
	void deleteByStudent(Student student);

	void deleteByAmenity(Amenity amenity);
}
