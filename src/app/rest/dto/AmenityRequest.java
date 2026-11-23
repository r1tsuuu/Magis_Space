package app.rest.dto;

import javax.validation.constraints.NotBlank;

public class AmenityRequest
{
	@NotBlank
	private String name;

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
}
