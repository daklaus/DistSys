package at.ac.tuwien.dslab1.service.server;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

import at.ac.tuwien.dslab1.domain.Client;
import at.ac.tuwien.dslab1.domain.User;
import at.ac.tuwien.dslab1.service.NetworkServiceFactory;
import at.ac.tuwien.dslab1.service.UDPClientNetworkService;
import at.ac.tuwien.dslab1.service.UDPClientNetworkServiceImpl;

class NotificationThread extends Thread {
	private volatile Boolean stop;
	private UDPClientNetworkService ns;
	private BlockingQueue<String> notifications;

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
			if (!(stop && e.getClass() == SocketException.class)) {
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