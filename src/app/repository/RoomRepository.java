package app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long>
{

	Room findByName(String name);
	
	List<Room> findByBuilding(String building);
	
	List<Room> findByCapacityGreaterThanEqual(Integer capacity);
	
	List<Room> findByAmenities_Name(String amenityName);
	
}
