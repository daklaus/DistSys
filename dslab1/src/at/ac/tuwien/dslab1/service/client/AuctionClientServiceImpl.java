/**
 * 
 */
package at.ac.tuwien.dslab1.service.client;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.SocketException;
import java.util.Scanner;

import at.ac.tuwien.dslab1.service.TCPClientNetworkService;
import at.ac.tuwien.dslab1.service.TCPClientNetworkServiceImpl;
import at.ac.tuwien.dslab1.service.UDPServerNetworkService;
import at.ac.tuwien.dslab1.service.UDPServerNetworkServiceImpl;

/**
 * @author klaus
 * 
 */
public class AuctionClientServiceImpl implements AuctionClientService {
	private TCPClientNetworkService ns;
	private NotificationListener listener;
	private NotificationThread notificationThread;
	private UncaughtExceptionHandler notificationExHandler;
	private String userName;

	// Private constructor prevents instantiation from other classes
	private AuctionClientServiceImpl() {
	}

	private static class AuctionClientServiceHolder {
		public static final AuctionClientService INSTANCE = new AuctionClientServiceImpl();
	}

	public static AuctionClientService getInstance() {
		return AuctionClientServiceHolder.INSTANCE;
	}

	@Override
	public void setNotificationListener(NotificationListener listener,
			UncaughtExceptionHandler exHandler) {
		if (listener == null)
			throw new IllegalArgumentException("listener is null");
		if (exHandler == null)
			throw new IllegalArgumentException("exHandler is null");
		this.listener = listener;
		this.notificationExHandler = exHandler;
	}

	@Override
	public String submitCommand(String command) throws IOException {
		if (!isConnected())
			throw new IllegalStateException("Service not connected!");

		// Send the command to the server
		ns.send(command);

		// Return the reply from the server
		return ns.receive();
	}

	@Override
	public void connect(String server, Integer serverPort, Integer udpPort)
			throws IOException {

		if (ns == null) {
			if (server == null || server.isEmpty() || serverPort == null
					|| serverPort <= 0 || udpPort == null || udpPort <= 0)
				throw new IllegalArgumentException(
						"The server or the server port are not set properly");

			ns = new TCPClientNetworkServiceImpl(server, serverPort);
		}

		// Start listening for notifications
		startNotification(udpPort);
	}

	@Override
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String getUserName() {
		return this.userName;
	}

	@Override
	public void close() throws IOException {
		if (notificationThread != null)
			notificationThread.close();
		if (ns != null)
			ns.close();
	}

	@Override
	public Boolean isConnected() {
		return ns != null;
	};

	/**
	 * Start receiving notifications from the server
	 * 
	 * @throws IOException
	 */
	private void startNotification(Integer udpPort) throws IOException {
		if (notificationThread != null && notificationThread.isAlive())
			return;
		if (udpPort == null || udpPort <= 0)
			throw new IllegalArgumentException(
					"The UDP port is not set properly");

		// Start notification thread
		notificationThread = new NotificationThread(udpPort);
		notificationThread.setName("Notification thread");
		notificationThread.setUncaughtExceptionHandler(notificationExHandler);
		notificationThread.start();
	}

	private class NotificationThread extends Thread {
		private volatile Boolean stop;
		private UDPServerNetworkService ns;

		public NotificationThread(Integer udpPort) throws IOException {
			if (udpPort == null || udpPort <= 0)
				throw new IllegalArgumentException(
						"The UDP port is not set properly");

			ns = new UDPServerNetworkServiceImpl(udpPort);
		}

		public Boolean isConnected() {
			return ns != null;
		}

		@Override
		public void run() {
			String command = null;
			stop = false;

			if (!isConnected())
				throw new IllegalStateException("Service not connected!");

			try {
				while (!stop) {
					command = ns.receive();

					parseNotificationCommand(command);
				}
			} catch (IOException e) {
				// The if-clause down here is because of what is described in
				// http://docs.oracle.com/javase/6/docs/technotes/guides/concurrency/threadPrimitiveDeprecation.html
				// under "What if a thread doesn't respond to Thread.interrupt?"

				// So the socket is closed when the ClientNetworkService is
				// closed and
				// in that special case no error should be propagated.
				if (!(stop && e.getClass() == SocketException.class)) {
					UncaughtExceptionHandler eh = this
							.getUncaughtExceptionHandler();
					if (eh != null)
						eh.uncaughtException(this, e);
				}
			}
		}

		/**
		 * Parse command and execute the methods of the notification listener
		 * 
		 * @param command
		 */
		private void parseNotificationCommand(String command) {

			if (listener == null)
				throw new IllegalStateException(
						"Cannot notify because the listener is not yet set");

			// Commands:
			// !new-bid <description>
			// !auction-ended <winner> <amount> <description>

			String cmdRegex = "![a-zA-Z-]+";
			String tmp;

			Scanner sc = new Scanner(command);
			sc.useDelimiter("\\s+");
			sc.skip("\\s*");

			if (!sc.hasNext(cmdRegex))
				return;

			tmp = sc.next();
			if (tmp.equalsIgnoreCase("!new-bid")) {
				if (!sc.hasNext())
					return;
				String description = sc.skip("\\s*").nextLine();

				listener.newBid(description);
			} else if (tmp.equalsIgnoreCase("!auction-ended")) {
				if (!sc.hasNext())
					return;
				String winner = sc.next();
				if (!sc.hasNextDouble())
					return;
				double amount = sc.nextDouble();
				if (!sc.hasNext())
					return;
				String description = sc.skip("\\s*").nextLine();

				listener.auctionEnded(winner, amount, description);
			}
		}

		public void close() throws IOException {
			this.stop = true;
			this.interrupt();
			ns.close();
		}

	}

}
