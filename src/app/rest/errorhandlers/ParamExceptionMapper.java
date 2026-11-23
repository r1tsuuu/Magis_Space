package app.rest.errorhandlers;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.server.ParamException;
// A path or query parameter that cannot be converted (GET /rooms/abc, ?roomId=abc, an id too large for a
// Long) is a client error. Jersey reports it as 404, so without this mapper it looks like an unknown URL.
@Provider
public class ParamExceptionMapper implements ExceptionMapper<ParamException>
{
	@Override
	public Response toResponse(ParamException e)
	{
		String kind = e instanceof ParamException.PathParamException ? "path parameter"
				: e instanceof ParamException.QueryParamException ? "query parameter" : "parameter";
		return ErrorBody.toResponse(400, "Invalid " + kind + " '" + e.getParameterName() + "': value is not valid.");
	}
}
