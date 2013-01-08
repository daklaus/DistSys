/**
 * 
 */
package at.ac.tuwien.dslab2.service.biddingClient;

import at.ac.tuwien.dslab2.service.net.NetworkServiceFactory;
import at.ac.tuwien.dslab2.service.net.TCPClientNetworkService;
import at.ac.tuwien.dslab2.service.security.HashMACService;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * @author klaus
 * 
 */
class BiddingClientServiceImpl implements BiddingClientService {
	private final String server;
	private final int serverPort;
	private final int udpPort;
	private TCPClientNetworkService ns;
	private NotificationListener notificationListener;
	private NotificationThread notificationThread;
	private UncaughtExceptionHandler notificationExHandler;
	private ReplyListener replyListener;
	private ReplyThread replyThread;
	private UncaughtExceptionHandler replyExHandler;
	private String userName;
	private HashMACService hashMACService;

	/**
	 * Sets the server, server port and own UDP port for the networking
	 * 
	 * @param server
	 *            the host name or IP address of the auction server
	 * @param serverPort
	 *            the TCP port of the auction server
	 * @param udpPort
	 *            the UDP port on which to listen for notifications from the
	 *            server
	 */
	public BiddingClientServiceImpl(String server, int serverPort, int udpPort) {
		if (server == null || server.isEmpty() || serverPort <= 0
				|| udpPort <= 0)
			throw new IllegalArgumentException(
					"The server or the server port are not set properly");

		this.server = server;
		this.serverPort = serverPort;
		this.udpPort = udpPort;
	}

	@Override
	public void setNotificationListener(NotificationListener listener,
			UncaughtExceptionHandler exHandler) {
		this.notificationListener = listener;
		this.notificationExHandler = exHandler;
	}

	@Override
	public void setReplyListener(ReplyListener listener,
			UncaughtExceptionHandler exHandler) {
		this.replyListener = listener;
		this.replyExHandler = exHandler;
	}

	@Override
	public void submitCommand(String command) throws IOException {
		if (!isConnected())
			throw new IllegalStateException("Service not connected!");

		if (!ns.isConnected()) {
			if (!command.matches("^!bid.*")) {
				throw new IllegalStateException(
						"The server is down! Wait for it being online again.");
			} else {
				// If it was a !bid command
				// TODO: Get random clients' signed timestamps
				// TODO: Retry sending with !signedBid
			}
		}

		// Send the command to the server
		ns.send(command);
	}

	private void reconnect() {

	}

	@Override
	public void connect() throws IOException {
		if (ns == null) {
			ns = NetworkServiceFactory.newTCPClientNetworkService(server,
					serverPort);
		}

		// Start listening for notifications
		// startNotification(udpPort); <- disabled for dslab2

		// Start listening for replies
		startReplying();
	}

	/**
	 * Start receiving notifications from the server
	 * 
	 * @throws IOException
	 */
	private void startNotification(int udpPort) throws IOException {
		if (notificationThread != null && notificationThread.isAlive())
			return;
		if (udpPort <= 0)
			throw new IllegalArgumentException(
					"The UDP port is not set properly");

		// Start notification thread
		notificationThread = new NotificationThread(udpPort,
				notificationListener);
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
		replyThread = new ReplyThread(ns, replyListener, this);
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
	public void setHashMACService(HashMACService hashMACService) {
		this.hashMACService = hashMACService;
	}

	@Override
	public HashMACService getHashMACService() {
		return this.hashMACService;
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
	public boolean isConnected() {
		return ns != null;
	}

}
