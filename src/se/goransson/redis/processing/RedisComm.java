package se.goransson.redis.processing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

public class RedisComm implements Runnable {

	private String host;
	private int port = 6379;

	private Socket publishSocket;
	private InputStream publish_is;
	private OutputStream publish_os;

	private Socket subscribeSocket;
	private InputStream subscribe_is;
	private OutputStream subscribe_os;
	private Thread subscribe_thread;

	// Listenerlist (really only one listener, the sketch!)
	private Vector<RedisListener> redislisteners;

	public RedisComm() {
		redislisteners = new Vector<RedisListener>();
	}

	public void addRedisListener(RedisListener listener) {
		if (redislisteners.contains(listener))
			return;
		redislisteners.addElement(listener);
	}

	private void createRedisEvent(String[] arguments) {
		RedisEvent wse = new RedisEvent(this, arguments);

		Vector vtemp = (Vector) redislisteners.clone();
		for (int x = 0; x < vtemp.size(); x++) {
			RedisListener target = null;
			target = (RedisListener) vtemp.elementAt(x);
			if (arguments[0].contains("subscribe")) {
				target.onSubscribe(arguments);
			} else if (arguments[0].contains("publish")) {
				target.onPublish(arguments);
			} else if (arguments[0].contains("message")) {
				target.onMessage(arguments);
			}
		}
	}

	/**
	 * Connect to Redis server.
	 * 
	 * @param host
	 * @param port
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	protected void connect(String host, int port) throws UnknownHostException,
			IOException {
		if (!isConnected(publishSocket)) {
			this.host = host;
			this.port = port;

			publishSocket = new Socket(host, port);

			publish_os = publishSocket.getOutputStream();
			publish_is = publishSocket.getInputStream();
		}
	}

	/**
	 * Used for publishing.
	 * 
	 * @param cmd
	 * @throws IOException
	 */
	protected void writePublishSocket(String cmd) throws IOException {
		publish_os.write(cmd.getBytes());
	}

	/**
	 * Used for subscribing.
	 * 
	 * @param cmd
	 * @throws IOException
	 */
	protected void writeSubscribeSocket(String cmd) throws IOException {
		if (subscribeSocket == null) {
			subscribeSocket = new Socket(host, port);

			subscribe_is = subscribeSocket.getInputStream();
			subscribe_os = subscribeSocket.getOutputStream();

			subscribe_thread = new Thread(this);
			subscribe_thread.start();
		}

		subscribe_os.write(cmd.getBytes());
	}

	protected boolean isConnected(Socket socket) {
		return socket != null && socket.isBound() && !socket.isClosed()
				&& socket.isConnected() && !socket.isInputShutdown()
				&& !socket.isOutputShutdown();
	}

	@Override
	public void run() {
		byte[] buffer = new byte[1024];
		int bytes;

		// Keep listening to the InputStream while connected
		while (isConnected(subscribeSocket)) {
			if (subscribe_is != null)
				try {
					// Read from the InputStream
					bytes = subscribe_is.read(buffer);

					String s = new String(buffer, 0, bytes);

					String[] arguments = processMessage(s);

					createRedisEvent(arguments);

				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
		}
	}

	private String[] processMessage(String s) {
		String newline = System.getProperty("line.separator");
		int numberOfArguments = Integer.parseInt(s.substring(s.indexOf('*') + 1,
				s.indexOf(newline, 0)));

		String[] arguments = new String[numberOfArguments];

		if (numberOfArguments > 0) {
			int argumentIndex = 0;
			for (int i = 0; i < arguments.length; i++) {
				argumentIndex = s.indexOf('$', argumentIndex + newline.length());

				int argumentStart = s.indexOf(newline, argumentIndex);
				int argumentEnd = s.indexOf(newline, argumentStart + newline.length());

				arguments[i] = s.substring(argumentStart, argumentEnd).trim();
			}
		} else {
			System.out.println("args < 0!   " + s);
		}

		return arguments;
	}
}
