package app.rest.errorhandlers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

// Without this, Jersey's own 404/405/400 errors fall through to ServiceException and become 500s.
@Provider
public class WebAppExceptionMapper implements ExceptionMapper<WebApplicationException>
{
	@Override
	public Response toResponse(WebApplicationException e)
	{
		return ErrorBody.toResponse(e.getResponse().getStatus(), e.getMessage());
	}
}
