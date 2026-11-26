package app.rest.errorhandlers;

import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path.Node;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException>
{
	@Override
	public Response toResponse(ConstraintViolationException e)
	{
		for (ConstraintViolation<?> v : e.getConstraintViolations())
		{
			Node last = null;
			for (Node n : v.getPropertyPath())
			{
				last = n;
			}
			if (last != null && last.getKind() == ElementKind.PARAMETER)
			{
				return ErrorBody.toResponse(400, "Request body is required.");
			}
		}
		String message = e.getConstraintViolations().stream()
				.map(this::describe)
				.collect(Collectors.joining("; "));
		return ErrorBody.toResponse(422, message);
	}
	
	private String describe(ConstraintViolation<?> v)
	{
		String property = null;
		boolean element = false;
		for (Node n : v.getPropertyPath())
		{
			if (n.getKind() == ElementKind.PROPERTY)
			{
				property = n.getName();
				element = false;
			}
			else if (n.getKind() == ElementKind.CONTAINER_ELEMENT)
			{
				element = true;
			}
		}
		if (property == null)
		{
			return lastPart(v.getPropertyPath().toString()) + " " + v.getMessage();
		}
		if (element && v.getConstraintDescriptor().getAnnotation() instanceof javax.validation.constraints.NotNull)
		{
			return property + " must not contain null";
		}
		return property + " " + v.getMessage();
	}
	private String lastPart(String path)
	{
		int dot = path.lastIndexOf('.');
		return dot < 0 ? path : path.substring(dot + 1);
	}
}
