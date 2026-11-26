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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.entity.Room;
import app.rest.dto.RoomRequest;
import app.service.RoomService;

@Component
@Path("/rooms")
public class RoomController
{
	@Autowired
	RoomService service;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Room> getRooms(@QueryParam("building") String building)
	{
		return service.findRooms(building);
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Room getRoom(@PathParam("id") Long id)
	{
		return service.getRoom(id);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createRoom(@NotNull @Valid RoomRequest request)
	{
		Room saved = service.createRoom(request);
		return Response.status(Response.Status.CREATED).entity(saved).build();
	}

	@DELETE
	@Path("/{id}")
	public Response deleteRoom(@PathParam("id") Long id)
	{
		service.deleteRoom(id);
		return Response.noContent().build();
	}
}
