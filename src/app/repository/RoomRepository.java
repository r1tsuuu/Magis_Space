package app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long>
{
	List<Room> findByBuildingIgnoreCaseOrderByName(String building);

	boolean existsByNameAndBuilding(String name, String building);

	List<Room> findByAmenities_Id(Long amenityId);
}
