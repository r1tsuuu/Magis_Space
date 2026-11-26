package app.rest.errorhandlers;

import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path.Node;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import app.validation.ValidReportInterval;
import app.validation.ValidTimeRange;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException>
{
	@Override
	public Response toResponse(ConstraintViolationException e)
	{
		boolean onlyInterval = true;
		for (ConstraintViolation<?> v : e.getConstraintViolations())
		{
			onlyInterval &= v.getConstraintDescriptor().getAnnotation() instanceof ValidReportInterval;
		}
		if (onlyInterval && !e.getConstraintViolations().isEmpty())
		{
			return ErrorBody.toResponse(409, e.getConstraintViolations().iterator().next().getMessage());
		}
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
				.map(v -> isClassLevel(v)
						? v.getMessage()
						: describe(v))
				.collect(Collectors.joining("; "));
		return ErrorBody.toResponse(422, message);
	}
	
	private boolean isClassLevel(ConstraintViolation<?> v)
	{
		Object a = v.getConstraintDescriptor().getAnnotation();
		return a instanceof ValidTimeRange || a instanceof ValidReportInterval;
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
