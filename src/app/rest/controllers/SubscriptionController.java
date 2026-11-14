package app.rest.controllers;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.entity.Subscription;
import app.rest.dto.SubscriptionRequest;
import app.service.SubscriptionService;

@Component
@Path("/subscriptions")
public class SubscriptionController
{
    @Autowired
    SubscriptionService service;

    @GET
    @Path("/{studentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Subscription> getSubscriptions(@PathParam("studentId") Long studentId)
    {
        return service.getSubscriptions(studentId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response subscribe(@NotNull @Valid SubscriptionRequest request)
    {
        Subscription saved = service.subscribe(request);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }
}
