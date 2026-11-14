package app.rest.controllers;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.entity.Amenity;
import app.repository.AmenityRepository;
import app.rest.errorhandlers.ApiException;

@Component
@Path("/amenities")
public class AmenityController
{
    @Autowired
    AmenityRepository repo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Amenity> getAllAmenities()
    {
        return repo.findAll();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Amenity getAmenityById(@PathParam("id") Long id)
    {
        return repo.findById(id)
                .orElseThrow(() -> new ApiException(404, "Amenity with ID " + id + " does not exist."));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Amenity addAmenity(@Valid Amenity amenity)
    {
        if (repo.findByName(amenity.getName()) != null)
        {
            throw new ApiException(409, "Amenity " + amenity.getName() + " already exists.");
        }
        amenity.setId(null);
        return repo.save(amenity);
    }
}
