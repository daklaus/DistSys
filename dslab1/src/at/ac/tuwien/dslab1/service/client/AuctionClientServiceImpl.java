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
	private NotificationListener notificationListener;
	private NotificationThread notificationThread;
	private UncaughtExceptionHandler notificationExHandler;
	private ReplyListener replyListener;
	private ReplyThread replyThread;
	private UncaughtExceptionHandler replyExHandler;
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
			throw new IllegalArgumentException("notificationListener is null");
		if (exHandler == null)
			throw new IllegalArgumentException("exHandler is null");
		this.notificationListener = listener;
		this.notificationExHandler = exHandler;
	}

	@Override
	public void setReplyListener(ReplyListener listener,
			UncaughtExceptionHandler exHandler) {
		if (listener == null)
			throw new IllegalArgumentException("notificationListener is null");
		if (exHandler == null)
			throw new IllegalArgumentException("exHandler is null");
		this.replyListener = listener;
		this.replyExHandler = exHandler;
	}

	@Override
	public void submitCommand(String command) throws IOException {
		if (!isConnected())
			throw new IllegalStateException("Service not connected!");

		// Send the command to the server
		ns.send(command);
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

		// Start listening for replies
		startReplying();
	}

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

	/**
	 * Start receiving replies from the server
	 * 
	 * @throws IOException
	 */
	private void startReplying() throws IOException {
		if (replyThread != null && replyThread.isAlive())
			return;
		if (!isConnected())
			throw new IllegalStateException("Service not connected!");

		// Start reply thread
		replyThread = new ReplyThread(ns);
		replyThread.setName("Reply thread");
		replyThread.setUncaughtExceptionHandler(replyExHandler);
		replyThread.start();
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
		if (replyThread != null)
			replyThread.close();
		if (ns != null)
			ns.close();
	}

	@Override
	public Boolean isConnected() {
		return ns != null;
	};

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
				// closed and in that special case no error should be
				// propagated.
				if (!(stop && e.getClass() == SocketException.class)) {
					UncaughtExceptionHandler eh = this
							.getUncaughtExceptionHandler();
					if (eh != null)
						eh.uncaughtException(this, e);
				}
			}
		}

		/**
		 * Parse command and execute the methods of the notification
		 * notificationListener
		 * 
		 * @param command
		 */
		private void parseNotificationCommand(String command) {

			if (notificationListener == null)
				throw new IllegalStateException(
						"Cannot notify because the notificationListener is not yet set");

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

				notificationListener.newBid(description);
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

				notificationListener.auctionEnded(winner, amount, description);
			}
		}

		public void close() throws IOException {
			this.stop = true;
			this.interrupt();
			ns.close();
		}

	}

	private class ReplyThread extends Thread {
		private volatile Boolean stop;
		private TCPClientNetworkService ns;

		public ReplyThread(TCPClientNetworkService ns) throws IOException {
			if (ns == null)
				throw new IllegalArgumentException(
						"The TCPClientNetworkService is null");

			this.ns = ns;
		}

		public Boolean isConnected() {
			return ns != null;
		}

		@Override
		public void run() {
			String reply = null;
			stop = false;

			if (!isConnected())
				throw new IllegalStateException("Service not connected!");

			try {
				while (!stop) {
					reply = ns.receive();

					if (replyListener == null)
						throw new IllegalStateException(
								"Cannot display reply because the replyListener is not set");

					replyListener.displayReply(reply);
				}
			} catch (IOException e) {
				// The if-clause down here is because of what is described in
				// http://docs.oracle.com/javase/6/docs/technotes/guides/concurrency/threadPrimitiveDeprecation.html
				// under "What if a thread doesn't respond to Thread.interrupt?"

				// So the socket is closed when the ClientNetworkService is
				// closed and in that special case no error should be
				// propagated.
				if (!(stop && e.getClass() == SocketException.class)) {
					UncaughtExceptionHandler eh = this
							.getUncaughtExceptionHandler();
					if (eh != null)
						eh.uncaughtException(this, e);
				}
			}
		}

		public void close() throws IOException {
			this.stop = true;
			this.interrupt();
		}

	}

}
