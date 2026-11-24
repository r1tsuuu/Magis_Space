package app.rest.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AmenityRequest
{
	@NotBlank
	@Size(max = 50)
	private String name;

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
}
