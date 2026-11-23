package app.validation;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.repository.AvailabilityRepository;

// Gives the (non-Spring) ReportIntervalValidator access to the repository.
@Component
public class ReportIntervalConfig
{
	@Autowired
	private AvailabilityRepository repo;

	@PostConstruct
	public void wire()
	{
		ReportIntervalValidator.setRepository(repo);
	}
}
