package at.ac.tuwien.dslab3.service.auctionServer;

import at.ac.tuwien.dslab3.domain.*;
import at.ac.tuwien.dslab3.service.analyticsServer.AnalyticsServer;
import at.ac.tuwien.dslab3.service.net.TCPClientNetworkService;
import at.ac.tuwien.dslab3.service.security.HashMACService;
import at.ac.tuwien.dslab3.service.security.HashMACServiceFactory;
import org.bouncycastle.util.encoders.Base64;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.util.Scanner;

class ClientHandler implements Runnable {
	private volatile boolean stop;
	private final TCPClientNetworkService ns;
	private final AuctionService as;
	private final AnalyticsServer ans;
	private User user;
	private NotificationThread notificationThread;
	private final File keyDirectory;

	public ClientHandler(TCPClientNetworkService ns, AuctionService as,
			String keyDirectory) throws IOException {
		if (ns == null)
			throw new IllegalArgumentException(
					"The TCPClientNetworkService is null");
		if (as == null)
			throw new IllegalArgumentException("The AuctionService is null");

		this.ns = ns;
		this.as = as;
        this.keyDirectory = new File(keyDirectory);
        this.ans = as.getAnalysticsServerRef();
		user = null;
	}

	public boolean isConnected() {
		return ns != null;
	}

	@Override
	public void run() {
		String command = null;
		String reply = null;
		stop = false;

		if (!isConnected())
			throw new IllegalStateException("Service not connected!");

		try {
			try {
				while (!stop) {
					// Receive command from the client
					command = ns.receive();

					reply = executeCommand(command);

					if (reply != null) {
						// Reply to the client
						ns.send(reply);
					} else {
						stop = true;
					}
				}
			} finally {
				close();
			}
		} catch (Exception e) {
			// The if-clause down here is because of what is described in
			// http://docs.oracle.com/javase/6/docs/technotes/guides/concurrency/threadPrimitiveDeprecation.html
			// under "What if a thread doesn't respond to Thread.interrupt?"

			// So the socket is closed when the ClientNetworkService is
			// closed and in that special case no error should be
			// propagated.
			if (!(stop && e.getClass() == SocketException.class)) {
				UncaughtExceptionHandler eh = Thread.currentThread()
						.getUncaughtExceptionHandler();
				if (eh != null)
					eh.uncaughtException(Thread.currentThread(), e);
			}
		}
	}

	/**
	 * Parse command, execute the methods of the AuctionService and return the
	 * reply
	 * 
	 * @param command
	 * @return the reply to send to the client or null if the connection should
	 *         be closed
	 * @throws IOException
	 */
	private String executeCommand(String command) throws IOException,
			GeneralSecurityException {
		if (as == null)
			throw new IllegalStateException("The AuctionService is null");
		if (ns == null)
			throw new IllegalStateException(
					"The TCPClientNetworkService is null");

		// Commands:
		// !login <username>
		// !logout
		// !list
		// !create <duration> <description>
		// !bid <auction-id> <amount>
		// !getClientList
		// !end

		final String commands = "!login <username>\n" + "!logout\n" + "!list\n"
				+ "!create <duration> <description>\n"
				+ "!bid <auction-id> <amount>\n" + "!end";
		final String invalidCommand = "Invalid command '" + command
				+ "'\n\nCommands are:\n" + commands;
		final String cmdRegex = "![a-zA-Z-]+";
		String tmp;

		Scanner sc = new Scanner(command);
		sc.useDelimiter("\\s+");
		sc.skip("\\s*");

		if (!sc.hasNext(cmdRegex))
			return invalidCommand;

		tmp = sc.next();
		if (tmp.equalsIgnoreCase("!login")) {
			if (user != null)
				return "You first have to log out!";

			if (!sc.hasNext())
				return invalidCommand;
			String userName = sc.next();
			if (!sc.hasNextInt())
				return invalidCommand;
			int udpPort = sc.nextInt();

			Client c = new Client(ns.getAddress(), ns.getPort(), udpPort);

			user = as.login(userName, c);

			synchronized (user) {
				if (user.isLoggedIn()) {
					user = null;
					return "You are already logged in on another client, you first have to log out!";
				}
				user.setLoggedIn(true);
			}
			user.setClient(c);

			// Start notification thread
			// startNotification(user); <- disabled for dslab2

			// Report event
			if (ans != null) {
				try {
					ans.processEvent(new UserEvent(EventType.USER_LOGIN, user
							.getName()));
				} catch (Exception e) {
					// Don't propagate the exception, because we don't care for
					// RMI exceptions
					// Maybe add logging later
				}
			}

			return "Successfully logged in as " + userName + "!";

		} else if (tmp.equalsIgnoreCase("!logout")) {
			if (user == null)
				return "You have to log in first!";

			as.logout(user);
			String userName = user.getName();
			user = null;

			// Stop notification thread
			if (notificationThread != null)
				notificationThread.close();

			// Report event
			if (ans != null) {
				try {
					ans.processEvent(new UserEvent(EventType.USER_LOGOUT,
							userName));
				} catch (Exception e) {
					// Don't propagate the exception, because we don't care for
					// RMI exceptions
					// Maybe add logging later
				}
			}

			return "Successfully logged out as " + userName + "!";

		} else if (tmp.equalsIgnoreCase("!list")) {
			try {
				if (user != null && user.isLoggedIn()) {
					StringBuilder builder = new StringBuilder();
					String auctions = as.list();
					builder.append(auctions);

					// Unit Separator(see:
					// http://en.wikipedia.org/wiki/Unit_separator#Field_separators)
					builder.append("\u001f");

					String userName = user.getName();
					HashMACService hashMACService = HashMACServiceFactory
							.getService(this.keyDirectory, userName);
					byte[] hashMAC = hashMACService.createHashMAC(auctions
                            .getBytes());
					byte[] encodedMAC = Base64.encode(hashMAC);
					builder.append(new String(encodedMAC));

					return builder.toString();
				}
				return as.list();
			} catch (IOException e) {
				return e.getMessage();
			}
		} else if (tmp.equalsIgnoreCase("!create")) {
			if (user == null)
				return "You have to log in first!";

			if (!sc.hasNextInt())
				return invalidCommand;
			int duration = sc.nextInt();
			if (!sc.hasNext())
				return invalidCommand;
			String description = sc.skip("\\s*").nextLine().trim();

			Auction a = as.create(user, description, duration);

			return "An auction '"
					+ a.getDescription()
					+ "' with id "
					+ a.getId()
					+ " has been created and will end on "
					+ (a.getEndDateFormatted() != null ? a
							.getEndDateFormatted() : "") + ".";

		} else if (tmp.equalsIgnoreCase("!bid")) {
			if (user == null)
				return "You have to log in first!";

			if (!sc.hasNextLong())
				return invalidCommand;
			long auctionId = sc.nextLong();
			if (!sc.hasNextDouble())
				return invalidCommand;
			double amount = sc.nextDouble();

			Auction a = as.bid(user, auctionId, amount);

			if (a == null)
				return "Auction with id " + auctionId + " does not exists!";

			Bid b = a.getHighestBid();
			if (b.getAmount() == amount && b.getUser().equals(user))
				return "You successfully bid with "
						+ String.format("%.2f", amount) + " on '"
						+ a.getDescription() + "'.";

			return "You unsuccessfully bid with "
					+ String.format("%.2f", amount) + " on '"
					+ a.getDescription() + "'. Current highest bid is "
					+ String.format("%.2f", b.getAmount()) + ".";

		} else if (tmp.equalsIgnoreCase("!getClientList")) {
			if (user == null)
				return "You have to log in first!";

			return as.getClientList();

		} else if (tmp.equalsIgnoreCase("!end")) {

			return null;
		}

		return invalidCommand;
	}

	public void close() throws IOException {
		stop = true;
		if (user != null) {
			as.logout(user);

			// Report event
			if (ans != null) {
				try {
					ans.processEvent(new UserEvent(EventType.USER_DISCONNECT,
							user.getName()));
				} catch (Exception e) {
					// Don't propagate the exception, because we don't care for
					// RMI exceptions
					// Maybe add logging later
				}
			}
		}

		// Close notification thread
		if (notificationThread != null)
			notificationThread.close();

		if (ns != null)
			ns.close();
	}

	/**
	 * Start receiving notifications from the server
	 * 
	 * @throws IOException
	 */
	private void startNotification(User user) throws IOException {
		if (notificationThread != null && notificationThread.isAlive())
			return;
		if (user == null)
			throw new IllegalArgumentException("The user is null");

		// Start notification thread
		notificationThread = new NotificationThread(user);
		notificationThread.setName("Notification thread for user "
				+ user.getName());
		notificationThread.setUncaughtExceptionHandler(Thread.currentThread()
				.getUncaughtExceptionHandler());
		notificationThread.start();
	}

}