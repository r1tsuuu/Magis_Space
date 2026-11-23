package app.rest.dto;

// VACANT/OCCUPIED: latest report is at most 60 minutes old. UNVERIFIED: older than that. NO_DATA: no reports.
public enum RoomStatus
{
	VACANT,
	OCCUPIED,
	UNVERIFIED,
	NO_DATA
}
