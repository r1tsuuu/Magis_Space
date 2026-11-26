package app.rest.dto;

public class HourlyCount
{
	private int hour;
	private long vacantCount;
	private long totalCount;

	public HourlyCount()
	{
	}

	public HourlyCount(int hour, long vacantCount, long totalCount)
	{
		this.hour = hour;
		this.vacantCount = vacantCount;
		this.totalCount = totalCount;
	}

	public static HourlyCount fromRow(Object[] row)
	{
		return new HourlyCount(((Number) row[0]).intValue(),
				row[1] == null ? 0 : ((Number) row[1]).longValue(),
				((Number) row[2]).longValue());
	}

	public int getHour() { return hour; }
	public void setHour(int hour) { this.hour = hour; }
	public long getVacantCount() { return vacantCount; }
	public void setVacantCount(long vacantCount) { this.vacantCount = vacantCount; }
	public long getTotalCount() { return totalCount; }
	public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
}
