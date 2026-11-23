package app.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Retrofit interface for the mock Twilio API: POST {notification.api.url}sms/send.
 * Callers must catch IOException (unreachable server) and check Response.isSuccessful() themselves;
 * an SMS failure must never fail the request that triggered it.
 */
public interface SmsClient
{
	@POST("sms/send")
	Call<SmsResponse> sendSms(@Body SmsRequest request);
}
