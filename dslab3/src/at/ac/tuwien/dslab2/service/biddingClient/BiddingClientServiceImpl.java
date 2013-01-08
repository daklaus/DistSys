/**
 * 
 */
package at.ac.tuwien.dslab2.service.biddingClient;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Scanner;

import org.bouncycastle.util.encoders.Base64;

import at.ac.tuwien.dslab2.service.net.NetworkServiceFactory;
import at.ac.tuwien.dslab2.service.net.TCPClientNetworkService;
import at.ac.tuwien.dslab2.service.security.HashMACService;
import at.ac.tuwien.dslab2.service.security.HashMACServiceFactory;

/**
 * @author klaus
 * 
 */
class BiddingClientServiceImpl implements BiddingClientService {
	private static final double RECONNECT_TIMEOUT = 5; // in seconds

	private final String server;
	private final int serverPort;
	private final int udpPort;
	private final String serverPublicKeyFileLocation;
	private final String clientsKeysDirectory;
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
	public BiddingClientServiceImpl(String server, int serverPort, int udpPort,
			String serverPublicKeyFileLocation, String clientsKeysDirectory) {
		if (server == null || server.isEmpty() || serverPort <= 0
				|| udpPort <= 0)
			throw new IllegalArgumentException(
					"The server or the server port are not set properly");

		this.server = server;
		this.serverPort = serverPort;
		this.udpPort = udpPort;
		this.serverPublicKeyFileLocation = serverPublicKeyFileLocation;
		this.clientsKeysDirectory = clientsKeysDirectory;
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
			if (!command.matches("^!bid")) {
				throw new IllegalStateException(
						"The server is down! Wait for it being online again.");
			} else {
				// If it was a !bid command
				// TODO: Get random clients' signed timestamps
				// TODO: Retry sending with !signedBid
			}
		}

		command = parseCommand(command);

		// Send the command to the server
		ns.send(command);
	}

	/**
	 * Parses the command to see if the client should do something.
	 * 
	 * @param command
	 * @return the modified command
	 * @throws IOException
	 */
	private String parseCommand(String command) throws IOException {
		if (command == null)
			throw new IllegalArgumentException("The command is null");

		// Commands:
		// !login <username>
		// !logout
		// !list
		// !create <duration> <description>
		// !bid <auction-id> <amount>
		// !end

		String cmdRegex = "![a-zA-Z-]+";
		String tmp;

		Scanner sc = new Scanner(command);
		sc.useDelimiter("\\s+");
		sc.skip("\\s*");

		if (!sc.hasNext(cmdRegex))
			return command;

		tmp = sc.next(cmdRegex);

		if (tmp.equalsIgnoreCase("!login")) {
			if (!sc.hasNext())
				return command;

			String username = sc.next();
			setUserName(username);

			initLoginServices(username);

			// Changed here LoginCommand for Lab3!
			// cmd = cmd + " " + udpPort;
			String clientChallenge = generateClientChallenge(Charset
					.forName("UTF-16"));
			command = command + " " + serverPort + " " + clientChallenge;

		} else if (tmp.equalsIgnoreCase("!logout")) {
			setUserName(null);
			this.hashMACService = null;

		}

		return command;
	}

	/**
	 * This method generates a 32 byte secure random number and encodes it with
	 * Base64 encoding
	 * 
	 * @param charset
	 *            the charset which will be used for translating the encoded
	 *            byte array to a string
	 * @return the generated 32 byte secure random number Base64 encoded
	 */
	private String generateClientChallenge(Charset charset) {
		SecureRandom secureRandom = new SecureRandom();
		final byte[] number = new byte[32];
		secureRandom.nextBytes(number);
		byte[] encodedRandom = Base64.encode(number);
		return new String(encodedRandom, charset);
	}

	private void initLoginServices(String username) throws IOException {
		try {
			this.hashMACService = HashMACServiceFactory.getService(
					clientsKeysDirectory, username);
		} catch (IOException e) {
			throw new IOException("Could not log in because keys for user "
					+ username + " not found in directory "
					+ clientsKeysDirectory, e);
		}
	}

	private boolean tryConnect() {
		try {
			connect();
		} catch (IOException e) {
			return false;
		}

		return true;
	}

	@Override
	public void connect() throws IOException {
		if (ns == null || !ns.isConnected()) {
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
