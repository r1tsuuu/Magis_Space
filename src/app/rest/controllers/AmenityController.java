package app.rest.controllers;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.entity.Amenity;
import app.rest.dto.AmenityRequest;
import app.service.AmenityService;

@Component
@Path("/amenities")
public class AmenityController
{
	@Autowired
	AmenityService service;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Amenity> getAllAmenities()
	{
		return service.findAll();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Amenity getAmenity(@PathParam("id") Long id)
	{
		return service.get(id);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAmenity(@NotNull @Valid AmenityRequest request)
	{
		return Response.status(Response.Status.CREATED).entity(service.create(request)).build();
	}

	@DELETE
	@Path("/{id}")
	public Response deleteAmenity(@PathParam("id") Long id)
	{
		service.delete(id);
		return Response.noContent().build();
	}
}
