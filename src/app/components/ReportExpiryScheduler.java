package app.components;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import app.entity.Availability;
import app.repository.AvailabilityRepository;

// Reports older than 60 minutes are marked unverified (not deleted), so room history is kept.
@Component
public class ReportExpiryScheduler
{
	@Autowired
	private AvailabilityRepository repo;

	@Scheduled(fixedRate = 300000)
	public void expireOldReports()
	{
		LocalDateTime cutoff = LocalDateTime.now().minusMinutes(60);
		// findByReportedAtBefore also returns reports that are already unverified; only save the others.
		List<Availability> expired = repo.findByReportedAtBefore(cutoff).stream()
				.filter(Availability::isVerified)
				.collect(Collectors.toList());
		expired.forEach(report -> report.setVerified(false));
		repo.saveAll(expired);
	}
}
