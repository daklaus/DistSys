package at.ac.tuwien.dslab3.service.auctionServer;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;

class ClientThreadFactory implements ThreadFactory {
	private int tCount;
	private final UncaughtExceptionHandler eh;

	public ClientThreadFactory(UncaughtExceptionHandler eh) {
		if (eh == null)
			throw new IllegalArgumentException("eh must not be null");

		this.eh = eh;
		tCount = 1;
	}

	@Override
	public Thread newThread(Runnable run) {
		// if (!ClientHandler.class.isInstance(run))
		// throw new IllegalArgumentException(
		// "The runnable is not an instance of ClientHandler");

		// ClientThread ct = new ClientThread((ClientHandler) run);
		Thread ct = new Thread(run);
		ct.setName("BiddingClient thread Nr." + tCount++);
		ct.setUncaughtExceptionHandler(eh);
		return ct;
	}
}
