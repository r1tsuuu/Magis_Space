package app.notification;

// Body of POST sms/send on the mock Twilio: {"to": "...", "body": "..."}
public class SmsRequest
{
	private String to;
	private String body;

	public SmsRequest()
	{
	}

	public SmsRequest(String to, String body)
	{
		this.to = to;
		this.body = body;
	}

	public String getTo() { return to; }
	public void setTo(String to) { this.to = to; }
	public String getBody() { return body; }
	public void setBody(String body) { this.body = body; }
}
