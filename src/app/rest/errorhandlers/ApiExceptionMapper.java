package app.rest.errorhandlers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<ApiException>
{
	@Override
	public Response toResponse(ApiException e)
	{
		return ErrorBody.toResponse(e.getStatus(), e.getMessage());
	}
}
