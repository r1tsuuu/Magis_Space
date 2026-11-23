package app.rest.controllers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.entity.Availability;
import app.rest.dto.AvailabilityRequest;
import app.service.ReportService;

@Component
@Path("/availability")
public class ReportController
{
	@Autowired
	ReportService service;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response submitReport(@NotNull @Valid AvailabilityRequest request)
	{
		Availability saved = service.submitReport(request);
		return Response.status(Response.Status.CREATED).entity(saved).build();
	}

	@DELETE
	@Path("/{id}")
	public Response deleteReport(@PathParam("id") Long id)
	{
		service.deleteReport(id);
		return Response.noContent().build();
	}
}
