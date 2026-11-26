package app.rest.errorhandlers;

import javax.annotation.Priority;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonParseException;

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
