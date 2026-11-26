package app.rest.errorhandlers;

import javax.annotation.Priority;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonParseException;

// Replaces Jersey's built-in Jackson mapper (plain text) so bad JSON or bad values
// (e.g. "dayOfWeek": "FUNDAY", "startTime": "8am") come back as a 400 JSON ErrorBody.
@Provider
@Priority(1)
public class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException>
{
	@Override
	public Response toResponse(JsonParseException e)
	{
		return ErrorBody.toResponse(400, JsonMappingExceptionMapper.message(e));
	}

}
