package app.rest.controllers;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.rest.dto.BuildingSummary;
import app.rest.dto.MapRoomView;
import app.service.MapService;

@Component
@Path("/map")
public class MapController
{
	@Autowired
	MapService service;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<MapRoomView> getMap(@QueryParam("building") String building, @QueryParam("floor") String floor,
			@QueryParam("fromBuilding") String fromBuilding)
	{
		return service.getMap(building, floor, fromBuilding);
	}

	@GET
	@Path("/buildings")
	@Produces(MediaType.APPLICATION_JSON)
	public List<BuildingSummary> getBuildings()
	{
		return service.getBuildingSummary();
	}
}
