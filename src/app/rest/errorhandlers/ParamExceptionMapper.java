package app.rest.errorhandlers;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.server.ParamException;
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
