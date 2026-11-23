package app.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

// A subscription is a condition ("a room in this building, with at least this capacity and this amenity
// became vacant"), not a specific room. minCapacity and amenity are optional.
@Entity(name = "subscription_table")
public class Subscription 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;

	@ManyToOne
	@JoinColumn(name = "student_id")
	@NotNull
	private Student student;

	@Column
	@NotBlank
	private String building;

	@Column
	@Min(1)
	private Integer minCapacity;

	@ManyToOne
	@JoinColumn(name = "amenity_id")
	private Amenity amenity;

	public Subscription()
	{
	}

	public Subscription(Long id, Student student, String building, Integer minCapacity, Amenity amenity) {
		this.id = id;
		this.student = student;
		this.building = building;
		this.minCapacity = minCapacity;
		this.amenity = amenity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonIgnore
	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	// Not named getStudentId(): Spring Data would read findByStudentId as
	// that getter instead of student.id and fail at startup.
	@JsonProperty("studentId")
	public Long studentId() {
		return student == null ? null : student.getId();
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public Integer getMinCapacity() {
		return minCapacity;
	}

	public void setMinCapacity(Integer minCapacity) {
		this.minCapacity = minCapacity;
	}

	@JsonIgnore
	public Amenity getAmenity() {
		return amenity;
	}

	public void setAmenity(Amenity amenity) {
		this.amenity = amenity;
	}

	// JSON shows the amenity as its name (or null).
	@JsonProperty("amenity")
	public String amenityName() {
		return amenity == null ? null : amenity.getName();
	}

	@Override
	public String toString() {
		return "Subscription [id=" + id + ", studentId=" + studentId() + ", building=" + building
				+ ", minCapacity=" + minCapacity + ", amenity=" + amenityName() + "]";
	}
}
