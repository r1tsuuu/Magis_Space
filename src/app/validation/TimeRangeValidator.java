package app.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TimeRangeValidator implements ConstraintValidator<ValidTimeRange, TimeRange>
{
	@Override
	public boolean isValid(TimeRange value, ConstraintValidatorContext context)
	{
		if (value == null || value.getStartTime() == null || value.getEndTime() == null)
		{
			return true;
		}
		return value.getEndTime().isAfter(value.getStartTime());
	}
}
