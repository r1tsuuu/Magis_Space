package app.rest.controllers;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.entity.Notification;
import app.service.NotificationService;

@Component
@Path("/notifications")
public class NotificationController
{
    @Autowired
    NotificationService service;

    @GET
    @Path("/{studentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Notification> getNotifications(@PathParam("studentId") Long studentId,
                                               @QueryParam("unread") boolean unread)
    {
        return service.getNotifications(studentId, unread);
    }
}
