package app.components;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import app.entity.Availability;
import app.repository.AvailabilityRepository;

@Component
public class ReportExpiryScheduler
{
	@Autowired
	private AvailabilityRepository repo;

	@Scheduled(fixedRate = 300000)
	public void expireOldReports()
	{
		LocalDateTime cutoff = LocalDateTime.now().minusMinutes(60);
		List<Availability> expired = repo.findByReportedAtBefore(cutoff).stream()
				.filter(Availability::isVerified)
				.collect(Collectors.toList());
		expired.forEach(report -> report.setVerified(false));
		repo.saveAll(expired);
	}
}
