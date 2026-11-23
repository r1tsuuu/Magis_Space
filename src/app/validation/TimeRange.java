package app.validation;

import java.time.LocalTime;

// Implemented by anything that has a start and end time, so @ValidTimeRange can check it.
public interface TimeRange
{
	LocalTime getStartTime();

	LocalTime getEndTime();
}
