package app.rest.dto;

import java.time.LocalDateTime;

import app.entity.Room;

public class MapRoomView
{
	private Long roomId;
	private String name;
	private String building;
	private int floor;
	private Double mapX;
	private Double mapY;
	private RoomStatus status = RoomStatus.NO_DATA;
	private boolean predictedVacant;
	private LocalDateTime lastReportedAt;
	private Long minutesAgo;

	public MapRoomView()
	{
	}

	public static MapRoomView of(Room room)
	{
		MapRoomView v = new MapRoomView();
		v.roomId = room.getId();
		v.name = room.getName();
		v.building = room.getBuilding();
		v.floor = room.getFloor();
		v.mapX = room.getMapX();
		v.mapY = room.getMapY();
		return v;
	}

	public Long getRoomId() { return roomId; }
	public void setRoomId(Long roomId) { this.roomId = roomId; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getBuilding() { return building; }
	public void setBuilding(String building) { this.building = building; }
	public int getFloor() { return floor; }
	public void setFloor(int floor) { this.floor = floor; }
	public Double getMapX() { return mapX; }
	public void setMapX(Double mapX) { this.mapX = mapX; }
	public Double getMapY() { return mapY; }
	public void setMapY(Double mapY) { this.mapY = mapY; }
	public RoomStatus getStatus() { return status; }
	public void setStatus(RoomStatus status) { this.status = status; }
	public boolean isPredictedVacant() { return predictedVacant; }
	public void setPredictedVacant(boolean predictedVacant) { this.predictedVacant = predictedVacant; }
	public LocalDateTime getLastReportedAt() { return lastReportedAt; }
	public void setLastReportedAt(LocalDateTime lastReportedAt) { this.lastReportedAt = lastReportedAt; }
	public Long getMinutesAgo() { return minutesAgo; }
	public void setMinutesAgo(Long minutesAgo) { this.minutesAgo = minutesAgo; }
}
