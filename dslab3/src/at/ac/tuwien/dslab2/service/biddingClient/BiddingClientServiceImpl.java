/**
 * 
 */
package at.ac.tuwien.dslab2.service.biddingClient;

import at.ac.tuwien.dslab2.service.net.NetworkServiceFactory;
import at.ac.tuwien.dslab2.service.net.TCPClientNetworkService;
import at.ac.tuwien.dslab2.service.security.HashMACService;
import at.ac.tuwien.dslab2.service.security.HashMACServiceFactory;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;
import org.bouncycastle.util.encoders.Base64;

import java.io.*;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
	private final File clientsKeysDirectory;
	private final PasswordFinder passwordFinder;
	private TCPClientNetworkService ns;
	private NotificationListener notificationListener;
	private NotificationThread notificationThread;
	private UncaughtExceptionHandler notificationExHandler;
	private FilterReplyListener replyListener;
	private final BlockingQueue<String> replyQueue;
	private ReplyThread replyThread;
	private UncaughtExceptionHandler replyExHandler;
	private String userName;
	private HashMACService hashMACService;
	private TCPClientNetworkService RSAns;
	private TCPClientNetworkService AESns;

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
			String serverPublicKeyFileLocation, String clientsKeysDirectory,
			PasswordFinder passwordFinder) {
		if (server == null || server.isEmpty() || serverPort <= 0
				|| udpPort <= 0)
			throw new IllegalArgumentException(
					"The server or the server port are not set properly");

		this.server = server;
		this.serverPort = serverPort;
		this.udpPort = udpPort;
		this.serverPublicKeyFileLocation = serverPublicKeyFileLocation;
		this.clientsKeysDirectory = new File(clientsKeysDirectory);
		this.passwordFinder = passwordFinder;

		// LinkedBlockingQueue for one reply at a time
		this.replyQueue = new LinkedBlockingQueue<String>(1);
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
		this.replyListener = new FilterReplyListener(listener, replyQueue);
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

		command = parseCommand(command);

		if (command != null) {
			// Send the command to the server
			ns.send(command);
			postSendAction(command);
		}
	}

	private void postSendAction(String command) throws IOException {
		if (command.matches("^!login.*")) {
			try {
				// Wait for reply of login command (and ignore it)
				this.replyQueue.take();

				postLoginAction();

				// After successful login

				// Get client list after login
				getClientList();
			} catch (InterruptedException e) {
				throw new IOException("Interrupted login procedure", e);
			}
		} else if (command.matches("^!getClientList.*")) {

			try {
				parseClientList(this.replyQueue.take());
			} catch (InterruptedException ignored) {
			}
			// Turn off pasting in the synchronization queue again
			this.replyListener.setForwardToQueue(false);
		}
	}

	private void getClientList() throws IOException, InterruptedException {
		// Turn off output to the presentation layer
		this.replyListener.setForwardToListener(false);
		// Get clients list
		ns.send("!getClientList");
		// Parse and store the client list
		parseClientList(this.replyQueue.take());

		// Turn on displaying to the presentation layer again
		this.replyListener.setForwardToListener(true);
		// Turn off pasting in the synchronization queue again
		this.replyListener.setForwardToQueue(false);
	}

	private void parseClientList(String clientList) {
		// TODO Auto-generated method stub

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

			setUserName(sc.next());
			command += " " + udpPort;

			command = preLoginAction(command);

			// Clean queue if it has another reply of a previous command
			this.replyQueue.clear();
			// Begin synchronization with queue
			this.replyListener.setForwardToQueue(true);

		} else if (tmp.equalsIgnoreCase("!getClientList")) {
			// Clean queue if it has another reply of a previous command
			this.replyQueue.clear();
			// Begin synchronization with queue
			this.replyListener.setForwardToQueue(true);

		} else if (tmp.equalsIgnoreCase("!logout")) {
			setUserName(null);
			this.hashMACService = null;

		}

		return command;
	}

	private String preLoginAction(String command) throws IOException {
		// @stefan: Hier gehören die sachen rein, die vor dem login command
		// passieren (den command verändern, das NS austauschen durch die RSA
		// gschicht, etc)

		String clientChallenge = generateClientChallenge(Charset
				.forName("UTF-16"));
		command += " " + clientChallenge;

		initLoginServices(passwordFinder);

		return command;
	}

	private void postLoginAction() throws IOException {
		// @stefan: Hier kannst deine sachen machen die nach dem login command
		// und vor meinen sachen passieren sollen
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

	private void initLoginServices(PasswordFinder passwordFinder)
			throws IOException {
		try {
			this.hashMACService = HashMACServiceFactory.getService(
					clientsKeysDirectory, userName);
			PrivateKey privateKey = readPrivateKey(
					clientsKeysDirectory.getPath() + "/" + userName + ".pem",
					passwordFinder);
			PublicKey publicKey = readPublicKey(serverPublicKeyFileLocation);
			this.RSAns = NetworkServiceFactory.newRSATCPClientNetworkService(
					this.ns, publicKey, privateKey);
		} catch (IOException e) {
			throw new IOException("Could not log in because keys for user '"
					+ userName + " not found in directory "
					+ clientsKeysDirectory, e);
		}
	}

	private void changeNS(TCPClientNetworkService newNS) {
		// Exchange the NS
		this.ns = newNS;

		// Stop the reply thread
		if (replyThread != null) {
			try {
				replyThread.close();
			} catch (IOException ignored) {
			}
		}

		// Restart the reply thread
		try {
			startReplying();
		} catch (IOException ignored) {
		}
	}

	private PublicKey readPublicKey(String path) throws IOException {
		PEMReader in = new PEMReader(new FileReader(path));
		Object o = in.readObject();
		if (o instanceof PublicKey) {
			return (PublicKey) o;
		}
		throw new IOException(
				"Read Object isn not of type 'PublicKey'.\nType is:"
						+ o.getClass().getSimpleName());
	}

	private PrivateKey readPrivateKey(String path, PasswordFinder passwordFinder)
			throws IOException {
		PEMReader in = new PEMReader(new FileReader(path), passwordFinder);
		Object o = in.readObject();
		if (o instanceof KeyPair) {
			return ((KeyPair) o).getPrivate();
		}
		throw new IOException(
				"Read Object isn not of type 'KeyPair'.\nType is:"
						+ o.getClass().getSimpleName());
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
