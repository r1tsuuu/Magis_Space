package app.notification;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Retrofit interface (raw okhttp bodies, no converter jar; see NotificationService for the Jackson JSON) for the mock Twilio API: POST {notification.api.url}sms/send.
 * Callers must catch IOException (unreachable server) and check Response.isSuccessful() themselves;
 * an SMS failure must never fail the request that triggered it.
 */
public interface SmsClient
{
	@POST("sms/send")
	Call<ResponseBody> sendSms(@Body RequestBody request);
}
