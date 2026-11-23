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

import app.entity.Availability;
import app.entity.ClassSchedule;
import app.rest.dto.HourlyCount;
import app.rest.dto.RoomRequest;
import app.rest.dto.RoomView;
import app.service.ReportService;
import app.service.RoomService;
import app.service.ScheduleService;

@Component
@Path("/rooms")
public class RoomController
{
	@Autowired
	RoomService service;

	@Autowired
	ReportService reportService;

	@Autowired
	ScheduleService scheduleService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<RoomView> getRooms(@QueryParam("building") String building,
			@QueryParam("minCapacity") String minCapacity, @QueryParam("amenity") String amenity)
	{
		return service.findRooms(building, minCapacity, amenity);
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public RoomView getRoom(@PathParam("id") Long id)
	{
		return service.getRoomView(id);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createRoom(@NotNull @Valid RoomRequest request)
	{
		RoomView saved = service.createRoom(request);
		return Response.status(Response.Status.CREATED).entity(saved).build();
	}

	@DELETE
	@Path("/{id}")
	public Response deleteRoom(@PathParam("id") Long id)
	{
		service.deleteRoom(id);
		return Response.noContent().build();
	}

	@GET
	@Path("/{id}/history")
	@Produces(MediaType.APPLICATION_JSON)
	public List<HourlyCount> getHistory(@PathParam("id") Long id)
	{
		return service.getHourlyHistory(id);
	}

	@GET
	@Path("/{id}/availability")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Availability> getAvailability(@PathParam("id") Long id)
	{
		return reportService.getReportsForRoom(id);
	}

	// Owned by the schedule feature; lives here because Jersey needs one class per @Path("/rooms").
	@GET
	@Path("/{id}/schedules")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ClassSchedule> getSchedules(@PathParam("id") Long id)
	{
		return scheduleService.findByRoom(id);
	}
}
