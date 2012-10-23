/**
 * 
 */
package at.ac.tuwien.dslab1.service.client;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.SocketException;
import java.util.Scanner;

import at.ac.tuwien.dslab1.service.NetworkService;
import at.ac.tuwien.dslab1.service.NetworkServiceImpl;

/**
 * @author klaus
 * 
 */
public class AuctionClientServiceImpl implements AuctionClientService {
	private NetworkService _ns;
	private NotificationListener listener;
	private NotificationThread notificationThread;
	private UncaughtExceptionHandler notificationExHandler;
	private String server;
	private Integer serverPort;
	private Integer udpPort;
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

	/**
	 * Gets and if necessary initializes the network service
	 * 
	 * @return the network service
	 * @throws IOException
	 */
	synchronized private NetworkService getNetwortService() throws IOException {
		if (_ns != null)
			return _ns;
		if (server == null || server.isEmpty() || serverPort == null
				|| serverPort == 0 || udpPort == null || udpPort == 0)
			throw new IllegalStateException(
					"Cannot initialize NetworkService because one or more of "
							+ "the values 'server', 'serverPort' or 'udpPort' are not yet set");

		_ns = new NetworkServiceImpl(server, serverPort, udpPort);

		return _ns;
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
		NetworkService ns = getNetwortService();

		// Send the command to the server
		ns.tcpSend(command);

		// Return the reply from the server
		return ns.tcpReceive();
	}

	@Override
	public void setNetworkParameter(String server, Integer serverPort,
			Integer udpPort) {
		if (server == null || server.isEmpty() || serverPort == null
				|| serverPort == 0 || udpPort == null || udpPort == 0)
			throw new IllegalArgumentException(
					"One or more of the values 'server', 'serverPort' or "
							+ "'udpPort' are not set properly");
		this.server = server;
		this.serverPort = serverPort;
		this.udpPort = udpPort;
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
		if (_ns != null)
			_ns.close();
	}

	@Override
	public void startNotification() {
		if (notificationThread != null && notificationThread.isAlive())
			return;

		// Start notification thread
		notificationThread = new NotificationThread();
		notificationThread.setName("Notification thread");
		notificationThread.setUncaughtExceptionHandler(notificationExHandler);
		notificationThread.start();
	}

	private class NotificationThread extends Thread {
		private volatile Boolean stop;

		@Override
		public void run() {
			NetworkService ns = null;
			String command = null;
			stop = false;

			try {
				ns = getNetwortService();

				while (!stop) {
					command = ns.udpReceive();

					parseNotificationCommand(command);
				}
			} catch (IOException e) {
				// The if-clause down here is because of what is described in
				// http://docs.oracle.com/javase/6/docs/technotes/guides/concurrency/threadPrimitiveDeprecation.html
				// under "What if a thread doesn't respond to Thread.interrupt?"

				// So the socket is closed when the NetworkService is closed and
				// in that special case no error should be propagated.
				if (!(stop && e.getClass() == SocketException.class))
					this.getUncaughtExceptionHandler().uncaughtException(this,
							e);
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

		public void close() {
			this.stop = true;
			this.interrupt();
		}

	}

}
