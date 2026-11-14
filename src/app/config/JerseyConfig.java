package app.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        
		// Jersey does not use Spring's component scan, so it needs the package explicitly.
		packages("app.rest");  
				    


    }
}