import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * Tiny mock Twilio for demos. No dependencies, Java 8.
 *   javac MockSmsServer.java && java MockSmsServer [port]      (default port 8080)
 * POST /sms/send {"to":"...","body":"..."} -> 200 {"sid":"SM...","status":"queued"}; prints each message.
 */
public class MockSmsServer
{
	private static final Random RANDOM = new Random();

	public static void main(String[] args) throws IOException
	{
		int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/sms/send", MockSmsServer::handle);
		server.start();
		System.out.println("Mock SMS server listening on http://localhost:" + port + "/sms/send");
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
			System.out.println("[SMS] to=" + to + " body=" + body);
			String sid = String.format("SM%032x", RANDOM.nextLong() & Long.MAX_VALUE);
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
