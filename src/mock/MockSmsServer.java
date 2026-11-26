package mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tiny mock Twilio for demos. Only needs the JDK (Java 8), no other jars.
 *
 * In Eclipse: right-click this file > Run As > Java Application (default port 8080).
 * To use another port: Run > Run Configurations > Arguments > Program arguments: 8081
 * (then set notification.api.url in config/application.properties to match).
 * In a terminal, from the project folder:
 *   javac -d out src/mock/MockSmsServer.java && java -cp out mock.MockSmsServer [port]
 *
 * POST /sms/send {"to":"...","body":"..."} -> 200 {"sid":"SM...","status":"queued"}; prints each message.
 *
 * It uses only java.net.ServerSocket. It does not use com.sun.net.httpserver, because Eclipse with a
 * Java 8 JRE marks that package as restricted API and refuses to compile it ("Access restriction").
 *
 * This class is in package "mock", outside "app", so Spring Boot's component scan never picks it up.
 */
public class MockSmsServer
{
	private static final Random RANDOM = new Random();
	private static final int MAX_BODY = 64 * 1024;
	private static int count = 0;

	@SuppressWarnings("resource") // the server socket stays open until the program is stopped
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
		ServerSocket server;
		try
		{
			server = new ServerSocket(port);
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
		System.out.println("Mock SMS server is running on http://localhost:" + port + "/sms/send");
		System.out.println("Waiting for messages from Magis Space. Each SMS is printed below. Stop it with the red square in Eclipse or Ctrl+C.");
		while (true)
		{
			try
			{
				final Socket socket = server.accept();
				Thread t = new Thread(() -> serve(socket));
				t.setDaemon(true);
				t.start();
			}
			catch (IOException e)
			{
				System.err.println("Mock SMS server: could not accept a connection: " + e.getMessage());
			}
		}
	}

	// Handles one request on one connection, then closes it.
	private static void serve(Socket socket)
	{
		try (Socket s = socket)
		{
			s.setSoTimeout(5000);
			InputStream in = s.getInputStream();
			OutputStream out = s.getOutputStream();

			String requestLine = readLine(in);
			if (requestLine == null || requestLine.isEmpty())
			{
				return;
			}
			String[] parts = requestLine.split(" ");
			String method = parts[0];
			String path = parts.length > 1 ? parts[1] : "";
			int contentLength = 0;
			String line;
			while ((line = readLine(in)) != null && !line.isEmpty())
			{
				int colon = line.indexOf(':');
				if (colon > 0 && line.substring(0, colon).trim().toLowerCase(Locale.ROOT).equals("content-length"))
				{
					try
					{
						contentLength = Integer.parseInt(line.substring(colon + 1).trim());
					}
					catch (NumberFormatException e)
					{
						contentLength = 0;
					}
				}
			}

			int q = path.indexOf('?');
			if (q >= 0)
			{
				path = path.substring(0, q);
			}
			if (!path.equals("/sms/send") && !path.equals("/sms/send/"))
			{
				reply(out, 404, "{\"error\":\"not found\"}");
				return;
			}
			if (!"POST".equals(method))
			{
				reply(out, 405, "{\"error\":\"POST only\"}");
				return;
			}
			if (contentLength < 0 || contentLength > MAX_BODY)
			{
				reply(out, 413, "{\"error\":\"body too large\"}");
				return;
			}
			String json = readBody(in, contentLength);
			String to = field(json, "to");
			String body = field(json, "body");
			if (to == null || body == null)
			{
				reply(out, 400, "{\"error\":\"to and body are required\"}");
				return;
			}
			String sid = String.format("SM%016x%016x", RANDOM.nextLong(), RANDOM.nextLong());
			String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
			synchronized (MockSmsServer.class)
			{
				int n = ++count;
				System.out.println("==================== SMS #" + n + " received " + time + " ====================");
				System.out.println("  To:   " + to);
				System.out.println("  Text: " + body);
				System.out.println("  Sid:  " + sid);
				System.out.println("====================================================================");
			}
			reply(out, 200, "{\"sid\":\"" + sid + "\",\"status\":\"queued\"}");
		}
		catch (IOException | RuntimeException e)
		{
			// A broken connection only affects that one SMS; the app logs the failure and carries on.
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

	// Reads one header line (ending in \r\n or \n), without the line ending. Returns null at end of stream.
	private static String readLine(InputStream in) throws IOException
	{
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int b;
		while ((b = in.read()) != -1)
		{
			if (b == '\n')
			{
				break;
			}
			if (b != '\r')
			{
				buf.write(b);
			}
			if (buf.size() > 8192)
			{
				throw new IOException("header line too long");
			}
		}
		if (b == -1 && buf.size() == 0)
		{
			return null;
		}
		return new String(buf.toByteArray(), StandardCharsets.ISO_8859_1);
	}

	private static String readBody(InputStream in, int length) throws IOException
	{
		byte[] bytes = new byte[length];
		int read = 0;
		while (read < length)
		{
			int n = in.read(bytes, read, length - read);
			if (n < 0)
			{
				break;
			}
			read += n;
		}
		return new String(bytes, 0, read, StandardCharsets.UTF_8);
	}

	private static void reply(OutputStream out, int status, String json) throws IOException
	{
		String reason;
		switch (status)
		{
			case 200: reason = "OK"; break;
			case 400: reason = "Bad Request"; break;
			case 404: reason = "Not Found"; break;
			case 405: reason = "Method Not Allowed"; break;
			case 413: reason = "Payload Too Large"; break;
			default: reason = "Error"; break;
		}
		byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
		String head = "HTTP/1.1 " + status + " " + reason + "\r\n"
				+ "Content-Type: application/json\r\n"
				+ "Content-Length: " + bytes.length + "\r\n"
				+ "Connection: close\r\n\r\n";
		out.write(head.getBytes(StandardCharsets.ISO_8859_1));
		out.write(bytes);
		out.flush();
	}
}
