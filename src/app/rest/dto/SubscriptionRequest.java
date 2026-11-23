package app.rest.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SubscriptionRequest
{
	@NotNull
	private Long studentId;

	@NotBlank
	@Size(max = 100)
	private String building;

	@Min(1)
	private Integer minCapacity;

	private Long amenityId;

	public Long getStudentId() { return studentId; }
	public void setStudentId(Long studentId) { this.studentId = studentId; }
	public String getBuilding() { return building; }
	public void setBuilding(String building) { this.building = building; }
	public Integer getMinCapacity() { return minCapacity; }
	public void setMinCapacity(Integer minCapacity) { this.minCapacity = minCapacity; }
	public Long getAmenityId() { return amenityId; }
	public void setAmenityId(Long amenityId) { this.amenityId = amenityId; }
}
