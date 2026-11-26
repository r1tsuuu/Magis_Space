package app.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.entity.Notification;
import app.entity.Room;
import app.entity.Student;
import app.entity.Subscription;
import app.notification.SmsClient;
import app.notification.SmsRequest;
import app.notification.SmsResponse;
import app.repository.NotificationRepository;
import app.repository.SubscriptionRepository;
import app.rest.errorhandlers.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Service
public class NotificationService
{
	private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
	private static final int MAX_MESSAGE = 300;
	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

	@Autowired
	private NotificationRepository repo;

	@Autowired
	private SubscriptionRepository subscriptionRepo;

	@Autowired
	private StudentService studentService;

	@Autowired
	private SmsClient smsClient;

	public List<Notification> findAll()
	{
		return repo.findAll();
	}

	public Notification get(Long id)
	{
		return repo.findById(id)
				.orElseThrow(() -> new ApiException(404, "Notification with ID " + id + " does not exist."));
	}

	public List<Notification> findByStudent(Long studentId)
	{
		return repo.findByStudentOrderBySentAtDesc(studentService.get(studentId));
	}

	public void delete(Long id)
	{
		repo.delete(get(id));
	}

	public void notifyMatchingSubscribers(Room room, Long excludeStudentId)
	{
		try
		{
			String message = truncate("" + room.getName() + " (" + room.getBuilding() + ") is now vacant as of "
					+ TIME_FORMAT.format(LocalDateTime.now()) + ".");
			Set<Long> notified = new HashSet<>();
			for (Subscription sub : subscriptionRepo.findMatchingForRoom(room))
			{
				Student student = sub.getStudent();
				if (student == null || student.getId().equals(excludeStudentId) || !notified.add(student.getId()))
				{
					continue;
				}
				try
				{
					send(student, message);
				}
				catch (RuntimeException e)
				{
					log.warn("Could not notify student {}: {}", student.getId(), e.toString());
				}
			}
		}
		catch (RuntimeException e)
		{
			log.warn("Could not notify subscribers of room {}: {}", room.getId(), e.toString());
		}
	}
	static String truncate(String message)
	{
		if (message.length() <= MAX_MESSAGE)
		{
			return message;
		}
		return message.substring(0, MAX_MESSAGE - 3) + "...";
	}

	public Notification sendAndSave(Long studentId, String message)
	{
		return send(studentService.get(studentId), message);
	}

	private Notification send(Student student, String message)
	{
		Notification saved = repo.save(new Notification(null, student, truncate(message), LocalDateTime.now()));
		sendSms(student.getPhone(), saved.getMessage());
		return saved;
	}

	private void sendSms(String phone, String message)
	{
		try
		{
			RequestBody body = RequestBody.create(JSON, MAPPER.writeValueAsBytes(new SmsRequest(phone, message)));
			smsClient.sendSms(body).enqueue(new Callback<ResponseBody>()
			{
				@Override
				public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
				{
					if (!response.isSuccessful())
					{
						log.warn("SMS to {} rejected: HTTP {}", phone, response.code());
					}
					else if (response.body() != null)
					{
						try
						{
							SmsResponse parsed = MAPPER.readValue(response.body().string(), SmsResponse.class);
							log.debug("SMS to {} {}: {}", phone, parsed.getStatus(), parsed.getSid());
						}
						catch (IOException | RuntimeException e)
						{
							log.debug("SMS to {} sent; reply not readable: {}", phone, e.toString());
						}
					}
				}
				@Override
				public void onFailure(Call<ResponseBody> call, Throwable t)
				{
					log.warn("SMS to {} failed: {}", phone, t.toString());
				}
			});
		}
		catch (IOException | RuntimeException e)
		{
			log.warn("SMS to {} could not be sent: {}", phone, e.toString());
		}
	}
}
