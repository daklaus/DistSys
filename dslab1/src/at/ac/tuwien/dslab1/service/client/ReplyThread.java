package at.ac.tuwien.dslab1.service.client;

import java.io.IOException;
import java.net.SocketException;

import at.ac.tuwien.dslab1.service.TCPClientNetworkService;

class ReplyThread extends Thread {
	private volatile Boolean stop;
	private TCPClientNetworkService ns;
	private ReplyListener listener;

	public ReplyThread(TCPClientNetworkService ns, ReplyListener listener)
			throws IOException {
		if (ns == null)
			throw new IllegalArgumentException(
					"The TCPClientNetworkService is null");
		if (listener == null)
			throw new IllegalArgumentException("The ReplyListener is null");

		this.ns = ns;
		this.listener = listener;
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

				listener.displayReply(reply);
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
		stop = true;
		this.interrupt();
		ns.close();
	}

}