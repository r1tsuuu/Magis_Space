package mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * Tiny mock Twilio for demos. Only needs the JDK (Java 8), no other jars.
 *
 * In Eclipse: right-click this file > Run As > Java Application (default port 8080).
 * To use another port: Run > Run Configurations > Arguments > Program arguments: 8081
 * (then set notification.api.url in config/application.properties to match).
 * In a terminal, from the project folder after Eclipse or javac has compiled it:
 *   javac -d out src/mock/MockSmsServer.java && java -cp out mock.MockSmsServer [port]
 *
 * POST /sms/send {"to":"...","body":"..."} -> 200 {"sid":"SM...","status":"queued"}; prints each message.
 *
 * This class is in package "mock", outside "app", so Spring Boot's component scan never picks it up.
 */
public class MockSmsServer
{
	private static final Random RANDOM = new Random();
	private static int count = 0;

	public static void main(String[] args)
	{
		int port = 8080;
		if (args.length > 0)
		{
			try
			{
				port = Integer.parseInt(args[0].trim());
			}
			catch (NumberFormatException e)
			{
				System.err.println("ERROR: \"" + args[0] + "\" is not a port number. Example: 8081");
				System.exit(1);
			}
		}
		HttpServer server;
		try
		{
			server = HttpServer.create(new InetSocketAddress(port), 0);
		}
		catch (BindException e)
		{
			System.err.println("ERROR: port " + port + " is already in use, so the mock SMS server cannot start.");
			System.err.println("Another program (XAMPP Apache, another app, or an older mock SMS server still running) is using it.");
			System.err.println("Stop that program, or start this one on another port (program argument, e.g. 8081)");
			System.err.println("and set notification.api.url=http://localhost:8081/ in config/application.properties.");
			System.exit(1);
			return;
		}
		catch (IOException e)
		{
			System.err.println("ERROR: could not start the mock SMS server on port " + port + ": " + e.getMessage());
			System.exit(1);
			return;
		}
		server.createContext("/sms/send", MockSmsServer::handle);
		server.start();
		System.out.println("Mock SMS server is running on http://localhost:" + port + "/sms/send");
		System.out.println("Waiting for messages from Magis Space. Each SMS is printed below. Stop it with the red square in Eclipse or Ctrl+C.");
	}

	private static void handle(HttpExchange ex) throws IOException
	{
		try
		{
			if (!"POST".equals(ex.getRequestMethod()))
			{
				reply(ex, 405, "{\"error\":\"POST only\"}");
				return;
			}
			String json = read(ex.getRequestBody());
			String to = field(json, "to");
			String body = field(json, "body");
			if (to == null || body == null)
			{
				reply(ex, 400, "{\"error\":\"to and body are required\"}");
				return;
			}
			String sid = String.format("SM%032x", RANDOM.nextLong() & Long.MAX_VALUE);
			int n;
			synchronized (MockSmsServer.class)
			{
				n = ++count;
			}
			String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
			System.out.println("==================== SMS #" + n + " received " + time + " ====================");
			System.out.println("  To:   " + to);
			System.out.println("  Text: " + body);
			System.out.println("  Sid:  " + sid);
			System.out.println("====================================================================");
			reply(ex, 200, "{\"sid\":\"" + sid + "\",\"status\":\"queued\"}");
		}
		catch (RuntimeException e)
		{
			reply(ex, 500, "{\"error\":\"server error\"}");
		}
	}

	// Minimal extraction of a string field from a flat JSON object (handles \" and \\ escapes).
	private static String field(String json, String name)
	{
		Matcher m = Pattern.compile("\"" + name + "\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"").matcher(json);
		if (!m.find())
		{
			return null;
		}
		return m.group(1).replace("\\\"", "\"").replace("\\n", "\n").replace("\\\\", "\\");
	}

	private static String read(InputStream in) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int n;
		while ((n = in.read(buf)) > 0)
		{
			out.write(buf, 0, n);
		}
		return new String(out.toByteArray(), StandardCharsets.UTF_8);
	}

	private static void reply(HttpExchange ex, int status, String json) throws IOException
	{
		byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
		ex.getResponseHeaders().set("Content-Type", "application/json");
		ex.sendResponseHeaders(status, bytes.length);
		try (OutputStream os = ex.getResponseBody())
		{
			os.write(bytes);
		}
	}
}
