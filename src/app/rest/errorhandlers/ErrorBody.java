package app.rest.errorhandlers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ErrorBody
{
	private int status;
	private String error;
	private String message;

	public ErrorBody(int status, String message)
	{
		this.status = status;
		this.error = reasonPhrase(status);
		this.message = message;
	}
	
	public static Response toResponse(int status, String message)
	{
		return Response.status(status)
				.entity(new ErrorBody(status, message))
				.type(MediaType.APPLICATION_JSON)
				.build();
	}

	private static String reasonPhrase(int status)
	{
		if (status == 422) return "Unprocessable Entity";   // JAX-RS's Status enum has no 422
		Response.Status s = Response.Status.fromStatusCode(status);
		return s == null ? "Error" : s.getReasonPhrase();
	}

	public int getStatus() { return status; }
	public String getError() { return error; }
	public String getMessage() { return message; }
}
