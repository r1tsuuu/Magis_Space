package app.validation;

import java.time.LocalTime;

public interface TimeRange
{
	LocalTime getStartTime();

	LocalTime getEndTime();
}
