package app.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Mock Twilio reply: {"sid": "...", "status": "..."}. Other fields are ignored.
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmsResponse
{
	private String sid;
	private String status;

	public String getSid() { return sid; }
	public void setSid(String sid) { this.sid = sid; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
}
