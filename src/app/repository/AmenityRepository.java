package app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import app.entity.Amenity;

public interface AmenityRepository extends JpaRepository<Amenity, Long> 
{
	// null when not found
	Amenity findByName(String name);

	boolean existsByName(String name);
}
