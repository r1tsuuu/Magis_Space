package app.rest.errorhandlers;

import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException>
{
	@Override
	public Response toResponse(ConstraintViolationException e)
	{
		String message = e.getConstraintViolations().stream()
				.map(v -> lastPart(v.getPropertyPath().toString()) + " " + v.getMessage())
				.collect(Collectors.joining("; "));
		return ErrorBody.toResponse(422, message);
	}
	
	// "createRoom.arg0.capacity" -> "capacity"
	private String lastPart(String path)
	{
		int dot = path.lastIndexOf('.');
		return dot < 0 ? path : path.substring(dot + 1);
	}
}
