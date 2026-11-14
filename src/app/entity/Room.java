package app.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity(name = "room_table")
public class Room 
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;
	
	@Column
	@NotNull
	private String name;
	
	@Column
	@NotNull
	private String building;
	
	@Column
	@Min(0)
	private int floor;
	
	@Column
	@Min(1)
	private int capacity;
	
	@Column
	private Double mapX;
	
	@Column
	private Double mapY;
	
	@Column
	private Double latitude;
	
	@Column
	private Double longitude;
	
	// EAGER: Jersey serializes after the JPA session closes, so LAZY would throw.
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name = "room_amenity_table",
		joinColumns = @JoinColumn(name = "room_id"),
		inverseJoinColumns = @JoinColumn(name = "amenity_id")
	)
	private Set<Amenity> amenities = new HashSet<>();
	
	public Room()
	{
		
	}

	public Room(Long id, @NotNull String name, @NotNull String building, @Min(0) int floor, @Min(1) int capacity,
			Double mapX, Double mapY, Double latitude, Double longitude) {
		super();
		this.id = id;
		this.name = name;
		this.building = building;
		this.floor = floor;
		this.capacity = capacity;
		this.mapX = mapX;
		this.mapY = mapY;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public Double getMapX() {
		return mapX;
	}

	public void setMapX(Double mapX) {
		this.mapX = mapX;
	}

	public Double getMapY() {
		return mapY;
	}

	public void setMapY(Double mapY) {
		this.mapY = mapY;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Set<Amenity> getAmenities() {
		return amenities;
	}

	public void setAmenities(Set<Amenity> amenities) {
		this.amenities = amenities;
	}

	@Override
	public String toString() {
		return "Room [id=" + id + ", name=" + name + ", building=" + building + ", floor=" + floor + ", capacity="
				+ capacity + ", mapX=" + mapX + ", mapY=" + mapY + ", latitude=" + latitude + ", longitude=" + longitude
				+ "]";
	}

}
