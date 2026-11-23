package app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long>
{
	// All three filters are optional (pass null to skip). Building and amenity match ignoring case.
	// minCapacity means capacity >= minCapacity. Sorted by building, then name.
	@Query("select distinct r from room_table r left join r.amenities a "
			+ "where (:building is null or lower(r.building) = lower(:building)) "
			+ "and (:minCapacity is null or r.capacity >= :minCapacity) "
			+ "and (:amenity is null or lower(a.name) = lower(:amenity)) "
			+ "order by r.building, r.name")
	List<Room> findByFilters(@Param("building") String building, @Param("minCapacity") Integer minCapacity,
			@Param("amenity") String amenity);

	boolean existsByNameAndBuilding(String name, String building);

	List<Room> findByBuildingAndFloor(String building, int floor);

	// Not in the spec, but needed by the map/amenity code:
	List<Room> findByBuilding(String building);

	boolean existsByBuilding(String building);

	// rooms that have the given amenity (used before deleting an amenity)
	List<Room> findByAmenities_Id(Long amenityId);
}
