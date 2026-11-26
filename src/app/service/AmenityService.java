package app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.entity.Amenity;
import app.entity.Room;
import app.repository.AmenityRepository;
import app.repository.RoomRepository;
import app.rest.dto.AmenityRequest;
import app.rest.errorhandlers.ApiException;

@Service
public class AmenityService
{
	@Autowired
	private AmenityRepository repo;

	@Autowired
	private RoomRepository roomRepo;

	public List<Amenity> findAll()
	{
		return repo.findAll();
	}

	public Amenity get(Long id)
	{
		return repo.findById(id)
				.orElseThrow(() -> new ApiException(404, "Amenity with ID " + id + " does not exist."));
	}

	public Amenity create(AmenityRequest dto)
	{
		String name = dto.getName().trim();
		if (repo.existsByName(name))
		{
			throw new ApiException(409, "Amenity " + name + " already exists.");
		}
		Amenity amenity = new Amenity();
		amenity.setName(name);
		return repo.save(amenity);
	}

	@Transactional
	public void delete(Long id)
	{
		Amenity amenity = get(id);
		for (Room room : roomRepo.findByAmenities_Id(id))
		{
			room.getAmenities().removeIf(a -> id.equals(a.getId()));
			roomRepo.save(room);
		}
		repo.delete(amenity);
	}
}
