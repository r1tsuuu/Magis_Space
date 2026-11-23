package app.rest.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;

import app.entity.Amenity;
import app.entity.Room;

// Response JSON of a room; amenities are names. lastReportedAt/minutesAgo are null when the room has no reports.
@JsonInclude(JsonInclude.Include.ALWAYS)
public class RoomView
{
	private Long id;
	private String name;
	private String building;
	private int floor;
	private int capacity;
	private Double mapX;
	private Double mapY;
	private Double latitude;
	private Double longitude;
	private List<String> amenities;
	private RoomStatus status = RoomStatus.NO_DATA;
	private boolean predictedVacant;
	private LocalDateTime lastReportedAt;
	private Long minutesAgo;

	public RoomView()
	{
	}

	// Copies the room's own fields (amenity names sorted). Status fields keep their defaults for the caller to set.
	public static RoomView of(Room room)
	{
		RoomView v = new RoomView();
		v.id = room.getId();
		v.name = room.getName();
		v.building = room.getBuilding();
		v.floor = room.getFloor();
		v.capacity = room.getCapacity();
		v.mapX = room.getMapX();
		v.mapY = room.getMapY();
		v.latitude = room.getLatitude();
		v.longitude = room.getLongitude();
		v.amenities = room.getAmenities().stream().map(Amenity::getName).sorted().collect(Collectors.toList());
		return v;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getBuilding() { return building; }
	public void setBuilding(String building) { this.building = building; }
	public int getFloor() { return floor; }
	public void setFloor(int floor) { this.floor = floor; }
	public int getCapacity() { return capacity; }
	public void setCapacity(int capacity) { this.capacity = capacity; }
	public Double getMapX() { return mapX; }
	public void setMapX(Double mapX) { this.mapX = mapX; }
	public Double getMapY() { return mapY; }
	public void setMapY(Double mapY) { this.mapY = mapY; }
	public Double getLatitude() { return latitude; }
	public void setLatitude(Double latitude) { this.latitude = latitude; }
	public Double getLongitude() { return longitude; }
	public void setLongitude(Double longitude) { this.longitude = longitude; }
	public List<String> getAmenities() { return amenities; }
	public void setAmenities(List<String> amenities) { this.amenities = amenities; }
	public RoomStatus getStatus() { return status; }
	public void setStatus(RoomStatus status) { this.status = status; }
	public boolean isPredictedVacant() { return predictedVacant; }
	public void setPredictedVacant(boolean predictedVacant) { this.predictedVacant = predictedVacant; }
	public LocalDateTime getLastReportedAt() { return lastReportedAt; }
	public void setLastReportedAt(LocalDateTime lastReportedAt) { this.lastReportedAt = lastReportedAt; }
	public Long getMinutesAgo() { return minutesAgo; }
	public void setMinutesAgo(Long minutesAgo) { this.minutesAgo = minutesAgo; }
}
