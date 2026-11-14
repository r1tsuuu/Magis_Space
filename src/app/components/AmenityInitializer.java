package app.components;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.entity.Amenity;
import app.repository.AmenityRepository;

@Component
public class AmenityInitializer
{
    @Autowired
    private AmenityRepository repo;

    @PostConstruct
    public void populate()
    {
        if (repo.count() == 0)
        {
            repo.save(new Amenity(null, "Projector"));
            repo.save(new Amenity(null, "Wi-Fi"));
            repo.save(new Amenity(null, "Air Conditioning"));
            repo.save(new Amenity(null, "Whiteboard"));
            repo.save(new Amenity(null, "Outlets"));
        }
    }
}
