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

import app.entity.Notification;
import app.rest.dto.NotificationRequest;
import app.service.NotificationService;

@Component
@Path("/notifications")
public class NotificationController
{
	@Autowired
	NotificationService service;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Notification> getAllNotifications(@QueryParam("studentId") Long studentId)
	{
		return service.findAll(studentId);
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Notification getNotification(@PathParam("id") Long id)
	{
		return service.get(id);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createNotification(@NotNull @Valid NotificationRequest request)
	{
		Notification saved = service.sendAndSave(request.getStudentId(), request.getMessage());
		return Response.status(Response.Status.CREATED).entity(saved).build();
	}

	@DELETE
	@Path("/{id}")
	public Response deleteNotification(@PathParam("id") Long id)
	{
		service.delete(id);
		return Response.noContent().build();
	}
}
