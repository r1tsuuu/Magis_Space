package app.rest.dto;

public class BuildingSummary
{
	private String building;
	private Double latitude;
	private Double longitude;
	private long vacantCount;
	private long totalRooms;

	public BuildingSummary()
	{
	}

	public BuildingSummary(String building, Double latitude, Double longitude, long vacantCount, long totalRooms)
	{
		this.building = building;
		this.latitude = latitude;
		this.longitude = longitude;
		this.vacantCount = vacantCount;
		this.totalRooms = totalRooms;
	}

	public String getBuilding() { return building; }
	public void setBuilding(String building) { this.building = building; }
	public Double getLatitude() { return latitude; }
	public void setLatitude(Double latitude) { this.latitude = latitude; }
	public Double getLongitude() { return longitude; }
	public void setLongitude(Double longitude) { this.longitude = longitude; }
	public long getVacantCount() { return vacantCount; }
	public void setVacantCount(long vacantCount) { this.vacantCount = vacantCount; }
	public long getTotalRooms() { return totalRooms; }
	public void setTotalRooms(long totalRooms) { this.totalRooms = totalRooms; }
}
