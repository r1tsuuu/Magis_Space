package app.components;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import app.entity.Availability;
import app.repository.AvailabilityRepository;

// Expired reports are marked unverified, not deleted, so room history is kept.
@Component
public class AvailabilityScheduler
{
	@Autowired
	private AvailabilityRepository repo;
	
	@Scheduled(fixedRate = 60000)
	public void expireOldReports()
	{
		LocalDateTime cutoff = LocalDateTime.now().minusHours(1);
		List<Availability> expired = repo.findByReportedAtBeforeAndVerifiedTrue(cutoff);
		
		expired.forEach(report -> report.setVerified(false));
		repo.saveAll(expired);
	}
}
