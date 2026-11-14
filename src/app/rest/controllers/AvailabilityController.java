package app.rest.controllers;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.entity.Availability;
import app.rest.dto.AvailabilityRequest;
import app.service.AvailabilityService;

@Component
@Path("/availability")
public class AvailabilityController
{
    @Autowired
    AvailabilityService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Availability> getAvailabilityReports(@QueryParam("roomId") Long roomId,
                                                     @QueryParam("status") String status)
    {
        return service.getReports(roomId, status);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response submitAvailability(@NotNull @Valid AvailabilityRequest request)
    {
        Availability saved = service.submitReport(request);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }
}
