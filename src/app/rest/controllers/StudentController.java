package app.rest.controllers;

import java.util.List;

import javax.validation.Valid;
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

import app.entity.Student;
import app.service.StudentService;

@Component
@Path("/students")
public class StudentController
{
    @Autowired
    StudentService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Student> getAllStudents()
    {
        return service.getAllStudents();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Student getStudentById(@PathParam("id") Long id)
    {
        return service.getStudent(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStudent(@Valid Student student)
    {
        Student saved = service.createStudent(student);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }
}
