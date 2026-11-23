package app.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Class-level constraint for an availability report request: the same student may not
 * report the same room twice within {@link ReportIntervalValidator#MIN_MINUTES} minutes.
 *
 * How it works: {@link ReportIntervalValidator} reads studentId and roomId from the
 * annotated {@code AvailabilityRequest}, asks AvailabilityRepository.findLatestByStudentAndRoom()
 * for that pair's newest report, and fails when it is younger than 10 minutes.
 * Missing ids and unknown student/room pass here (the @NotNull checks and the service's 404 handle them).
 *
 * Jersey builds validators itself, so @Autowired does not work inside them. The repository
 * is handed over through ReportIntervalConfig (a Spring bean) into a static field instead.
 *
 * A violation of this constraint is mapped to HTTP 409 by ValidationExceptionMapper.
 * The same rule is available to services as ReportIntervalValidator.isIntervalOk(...).
 */
@Documented
@Constraint(validatedBy = ReportIntervalValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidReportInterval
{
	String message() default "this student already reported this room less than 10 minutes ago";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
