package at.ac.tuwien.dslab3.service.auctionServer;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

import at.ac.tuwien.dslab3.domain.Client;
import at.ac.tuwien.dslab3.domain.User;
import at.ac.tuwien.dslab3.service.net.NetworkServiceFactory;
import at.ac.tuwien.dslab3.service.net.UDPClientNetworkService;

class NotificationThread extends Thread {
	private volatile boolean stop;
	private final UDPClientNetworkService ns;
	private final BlockingQueue<String> notifications;

	public NotificationThread(User user) throws IOException {
		if (user == null)
			throw new IllegalArgumentException("The user is null");

		Client c = user.getClient();
		if (c == null)
			throw new IllegalStateException("The user's client is null");

		ns = NetworkServiceFactory.newUDPClientNetworkService(c.getIp(),
				c.getUdpPort());
		this.notifications = user.getNotifications();
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
					command = notifications.take();

					ns.send(command);
				}
			} finally {
				if (ns != null)
					ns.close();
			}
		} catch (IOException e) {
			if (!stop) {
				UncaughtExceptionHandler eh = this
						.getUncaughtExceptionHandler();
				if (eh != null)
					eh.uncaughtException(this, e);
			}
		} catch (InterruptedException e) {
			if (!stop) {
				UncaughtExceptionHandler eh = this
						.getUncaughtExceptionHandler();
				if (eh != null)
					eh.uncaughtException(this, e);
			}
		}
	}

	public void close() throws IOException {
		stop = true;
		this.interrupt();
		if (ns != null)
			ns.close();
	}

}