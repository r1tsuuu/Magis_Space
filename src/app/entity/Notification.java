package app.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity(name = "notification_table")
public class Notification 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;

	@ManyToOne
	@JoinColumn(name = "student_id")
	@NotNull
	private Student student;

	@Column(length = 300)
	@NotBlank
	@Size(max = 300)
	private String message;

	@Column
	@NotNull
	private LocalDateTime sentAt;

	public Notification()
	{
	}

	public Notification(Long id, Student student, String message, LocalDateTime sentAt) {
		this.id = id;
		this.student = student;
		this.message = message;
		this.sentAt = sentAt;
	}

	@PrePersist
	void defaultSentAt()
	{
		if (sentAt == null)
		{
			sentAt = LocalDateTime.now();
		}
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

	@JsonProperty("studentId")
	public Long studentId() {
		return student == null ? null : student.getId();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getSentAt() {
		return sentAt;
	}

	public void setSentAt(LocalDateTime sentAt) {
		this.sentAt = sentAt;
	}

	@Override
	public String toString() {
		return "Notification [id=" + id + ", studentId=" + studentId() + ", message=" + message + ", sentAt="
				+ sentAt + "]";
	}
}
