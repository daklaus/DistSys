package at.ac.tuwien.dslab1.service.client;

import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;

import at.ac.tuwien.dslab1.service.NetworkServiceFactory;
import at.ac.tuwien.dslab1.service.UDPServerNetworkService;

class NotificationThread extends Thread {
	private volatile boolean stop;
	private final UDPServerNetworkService ns;
	private final NotificationListener listener;

	public NotificationThread(int udpPort, NotificationListener listener)
			throws IOException {
		if (udpPort <= 0)
			throw new IllegalArgumentException(
					"The UDP port is not set properly");
		if (listener == null)
			throw new IllegalArgumentException(
					"The NotificationListener is null");

		ns = NetworkServiceFactory.newUDPServerNetworkService(udpPort);
		this.listener = listener;
	}

	public boolean isConnected() {
		return ns != null;
	}

	@Override
	public void run() {
		String command = null;
		stop = false;

		if (!isConnected())
			throw new IllegalStateException("Service not connected!");

		try {
			try {
				while (!stop) {
					command = ns.receive();

					parseCommand(command);
				}
			} finally {
				if (ns != null)
					ns.close();
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
	private void parseCommand(String command) {

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
		stop = true;
		this.interrupt();
		if (ns != null)
			ns.close();
	}

}