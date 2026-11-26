package app.rest.dto;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RoomRequest
{
	@NotBlank
	@Size(max = 50)
	private String name;

	@NotBlank
	@Size(max = 100)
	private String building;

	@NotNull
	@Min(0)
	private Integer floor;

	@NotNull
	@Min(1)
	private Integer capacity;

	private List<@NotNull Long> amenityIds;

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getBuilding() { return building; }
	public void setBuilding(String building) { this.building = building; }
	public Integer getFloor() { return floor; }
	public void setFloor(Integer floor) { this.floor = floor; }
	public Integer getCapacity() { return capacity; }
	public void setCapacity(Integer capacity) { this.capacity = capacity; }
	public List<Long> getAmenityIds() { return amenityIds; }
	public void setAmenityIds(List<Long> amenityIds) { this.amenityIds = amenityIds; }
}
