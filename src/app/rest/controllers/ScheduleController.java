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

import app.entity.ClassSchedule;
import app.rest.dto.ScheduleRequest;
import app.service.ScheduleService;

@Component
@Path("/schedules")
public class ScheduleController
{
	@Autowired
	private ScheduleService scheduleService;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createSchedule(@NotNull @Valid ScheduleRequest request)
	{
		return Response.status(201).entity(scheduleService.create(request)).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ClassSchedule> getSchedules(@QueryParam("roomId") Long roomId, @QueryParam("day") String day)
	{
		return scheduleService.findAll(roomId, day);
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public ClassSchedule getSchedule(@PathParam("id") Long id)
	{
		return scheduleService.get(id);
	}

	@DELETE
	@Path("/{id}")
	public Response deleteSchedule(@PathParam("id") Long id)
	{
		scheduleService.delete(id);
		return Response.noContent().build();
	}
}
