package app.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Class-level constraint: endTime must be after startTime. Null times pass (use @NotNull on the fields).
 * Applied to ClassSchedule and ScheduleRequest. A violation yields 422 through ValidationExceptionMapper.
 */
@Documented
@Constraint(validatedBy = TimeRangeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTimeRange
{
	String message() default "endTime must be after startTime";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
