package app.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// SMS delivery is asynchronous (Retrofit enqueue): the request thread only saves the Notification and
// hands the SMS to OkHttp's dispatcher, so a slow or dead SMS server never delays or fails a request.
// Failures (unreachable, timeout, non-2xx) are only logged. The Notification row is saved regardless.
@Service
public class NotificationService
{
	private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
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

	// Notifies every student with a subscription matching the room. Never throws.
	public void notifyMatchingSubscribers(Room room)
	{
		try
		{
			String message = room.getName() + " (" + room.getBuilding() + ") is now vacant as of "
					+ TIME_FORMAT.format(LocalDateTime.now()) + ".";
			for (Subscription sub : subscriptionRepo.findMatchingForRoom(room))
			{
				try
				{
					send(sub.getStudent(), message);
				}
				catch (RuntimeException e)
				{
					log.warn("Could not notify student {}: {}", sub.studentId(), e.toString());
				}
			}
		}
		catch (RuntimeException e)
		{
			log.warn("Could not notify subscribers of room {}: {}", room.getId(), e.toString());
		}
	}

	public Notification sendAndSave(Long studentId, String message)
	{
		return send(studentService.get(studentId), message);
	}

	private Notification send(Student student, String message)
	{
		Notification saved = repo.save(new Notification(null, student, message, LocalDateTime.now()));
		sendSms(student.getPhone(), message);
		return saved;
	}

	private void sendSms(String phone, String message)
	{
		try
		{
			smsClient.sendSms(new SmsRequest(phone, message)).enqueue(new Callback<SmsResponse>()
			{
				@Override
				public void onResponse(Call<SmsResponse> call, Response<SmsResponse> response)
				{
					if (!response.isSuccessful())
					{
						log.warn("SMS to {} rejected: HTTP {}", phone, response.code());
					}
				}

				@Override
				public void onFailure(Call<SmsResponse> call, Throwable t)
				{
					log.warn("SMS to {} failed: {}", phone, t.toString());
				}
			});
		}
		catch (RuntimeException e)
		{
			log.warn("SMS to {} could not be sent: {}", phone, e.toString());
		}
	}
}
