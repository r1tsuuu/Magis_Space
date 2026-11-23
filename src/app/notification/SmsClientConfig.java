package app.notification;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

// Builds the SmsClient from notification.api.url. The bean ALWAYS exists (never null): when the url is
// empty or invalid it falls back to http://localhost:8080/, so calls simply fail with an IOException
// that the caller catches.
@Configuration
public class SmsClientConfig
{
	static final String FALLBACK_URL = "http://localhost:8080/";

	@Value("${notification.api.url:}")
	private String apiUrl;

	@Bean
	public SmsClient smsClient()
	{
		OkHttpClient http = new OkHttpClient.Builder()
				.connectTimeout(3, TimeUnit.SECONDS)
				.readTimeout(3, TimeUnit.SECONDS)
				.writeTimeout(3, TimeUnit.SECONDS)
				.build();
		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try
		{
			return build(baseUrl(apiUrl), http, mapper);
		}
		catch (IllegalArgumentException e)
		{
			return build(FALLBACK_URL, http, mapper);
		}
	}

	private SmsClient build(String url, OkHttpClient http, ObjectMapper mapper)
	{
		return new Retrofit.Builder()
				.baseUrl(url)
				.client(http)
				.addConverterFactory(JacksonConverterFactory.create(mapper))
				.build()
				.create(SmsClient.class);
	}

	// Retrofit requires the base url to end with "/".
	static String baseUrl(String url)
	{
		String u = url == null ? "" : url.trim();
		if (u.isEmpty())
		{
			return FALLBACK_URL;
		}
		return u.endsWith("/") ? u : u + "/";
	}
}
