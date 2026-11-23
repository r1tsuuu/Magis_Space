package app.rest.dto;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

// Amenities can be given as amenityId (one), amenityIds (several) or amenities (names). All are optional;
// the service combines whichever are present and answers 404 for an unknown id or name.
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

	private Double mapX;
	private Double mapY;
	private Double latitude;
	private Double longitude;

	private Long amenityId;
	private List<@NotNull Long> amenityIds;
	private List<@NotNull String> amenities;

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getBuilding() { return building; }
	public void setBuilding(String building) { this.building = building; }
	public Integer getFloor() { return floor; }
	public void setFloor(Integer floor) { this.floor = floor; }
	public Integer getCapacity() { return capacity; }
	public void setCapacity(Integer capacity) { this.capacity = capacity; }
	public Double getMapX() { return mapX; }
	public void setMapX(Double mapX) { this.mapX = mapX; }
	public Double getMapY() { return mapY; }
	public void setMapY(Double mapY) { this.mapY = mapY; }
	public Double getLatitude() { return latitude; }
	public void setLatitude(Double latitude) { this.latitude = latitude; }
	public Double getLongitude() { return longitude; }
	public void setLongitude(Double longitude) { this.longitude = longitude; }
	public Long getAmenityId() { return amenityId; }
	public void setAmenityId(Long amenityId) { this.amenityId = amenityId; }
	public List<Long> getAmenityIds() { return amenityIds; }
	public void setAmenityIds(List<Long> amenityIds) { this.amenityIds = amenityIds; }
	public List<String> getAmenities() { return amenities; }
	public void setAmenities(List<String> amenities) { this.amenities = amenities; }
}
