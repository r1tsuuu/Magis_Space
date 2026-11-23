package app.validation;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import app.entity.Availability;
import app.repository.AvailabilityRepository;
import app.rest.dto.AvailabilityRequest;

public class ReportIntervalValidator implements ConstraintValidator<ValidReportInterval, AvailabilityRequest>
{
	public static final int MIN_MINUTES = 10;

	// Set once at startup by ReportIntervalConfig. Null means "not wired yet": the check is skipped.
	private static volatile AvailabilityRepository repository;

	static void setRepository(AvailabilityRepository repo)
	{
		repository = repo;
	}

	@Override
	public boolean isValid(AvailabilityRequest request, ConstraintValidatorContext context)
	{
		if (request == null)
		{
			return true;
		}
		return isIntervalOk(repository, request.getStudentId(), request.getRoomId());
	}

	/** True when the student has no report for the room, or the latest one is at least 10 minutes old. */
	public static boolean isIntervalOk(AvailabilityRepository repo, Long studentId, Long roomId)
	{
		if (repo == null || studentId == null || roomId == null)
		{
			return true;
		}
		Availability latest = repo.findLatestByStudentAndRoom(studentId, roomId);
		if (latest == null || latest.getReportedAt() == null)
		{
			return true;
		}
		return Duration.between(latest.getReportedAt(), LocalDateTime.now()).toMinutes() >= MIN_MINUTES;
	}
}
