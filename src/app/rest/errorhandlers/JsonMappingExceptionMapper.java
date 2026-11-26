package app.rest.errorhandlers;

import javax.annotation.Priority;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Provider
@Priority(1)
public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException>
{
	@Override
	public Response toResponse(JsonMappingException e)
	{
		return ErrorBody.toResponse(400, message(e));
	}

	static String message(JsonProcessingException e)
	{
		return "Malformed request body: " + e.getOriginalMessage();
	}
}
