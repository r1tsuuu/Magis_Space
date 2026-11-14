package app.rest.controllers;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
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
import app.service.RoomService;

@Component
@Path("/rooms")
public class RoomController
{
    @Autowired
    RoomService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Room> getAllRooms(@QueryParam("building") String building,
                                  @QueryParam("minCapacity") Integer minCapacity,
                                  @QueryParam("amenity") String amenity)
    {
        return service.getRooms(building, minCapacity, amenity);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Room getRoomById(@PathParam("id") Long id)
    {
        return service.getRoom(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(@Valid Room room)
    {
        Room saved = service.createRoom(room);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }
}
