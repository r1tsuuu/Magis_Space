package app.rest.errorhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


// Catch-all: any unhandled exception becomes a JSON ErrorBody (500, or 409 for constraint violations).

@Provider
public class ServiceException extends Exception implements
                ExceptionMapper<Throwable> 
{
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(ServiceException.class);
 
    public ServiceException() {
        super("Something bad has happened");
    }
 
    public ServiceException(String string) {
        super(string);
    }
 
    @Override
    public Response toResponse(Throwable exception) 
    {
    	// Unique-constraint races (duplicate room/email/amenity) are conflicts, not server errors.
    	for (Throwable t = exception; t != null; t = t.getCause())
    	{
    		if (t instanceof DataIntegrityViolationException)
    		{
    			return ErrorBody.toResponse(409, "The request conflicts with existing data.");
    		}
    	}
    	// Never send a stack trace to the client; always answer with the JSON ErrorBody.
    	log.error("Unexpected server error", exception);
    	return ErrorBody.toResponse(500, "Unexpected server error.");
    }
}
