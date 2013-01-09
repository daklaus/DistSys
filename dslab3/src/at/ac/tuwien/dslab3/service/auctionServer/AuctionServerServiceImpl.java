package at.ac.tuwien.dslab3.service.auctionServer;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

class AuctionServerServiceImpl implements AuctionServerService {

	private ServerThread serverThread;
	private UncaughtExceptionHandler serverExHandler;

	// Private constructor prevents instantiation from other classes
	private AuctionServerServiceImpl() {
	}

	private static class AuctionServerServiceHolder {
		public static final AuctionServerService INSTANCE = new AuctionServerServiceImpl();
	}

	public static AuctionServerService getInstance() {
		return AuctionServerServiceHolder.INSTANCE;
	}

	@Override
	public void start(int tcpPort, String analyticsServerRef,
                      String billingServerRef, String keyDirectory) throws IOException {
		if (serverThread != null && serverThread.isAlive())
			return;
		if (tcpPort <= 0)
			throw new IllegalArgumentException(
					"The TCP port is not set properly");
		if (analyticsServerRef == null)
			throw new IllegalArgumentException("analyticsServerRef is null");
		if (billingServerRef == null)
			throw new IllegalArgumentException("billingServerRef is null");
        if (keyDirectory == null)
			throw new IllegalArgumentException("key directory is null");

		// Start server thread
		serverThread = new ServerThread(tcpPort, analyticsServerRef,
				billingServerRef, keyDirectory);
		serverThread.setName("AuctionServer thread");
		serverThread.setUncaughtExceptionHandler(serverExHandler);
		serverThread.start();
	}

	@Override
	public void setExceptionHandler(UncaughtExceptionHandler exHandler) {
		if (exHandler == null)
			throw new IllegalArgumentException("exHandler is null");
		this.serverExHandler = exHandler;

		if (serverThread != null)
			serverThread.setUncaughtExceptionHandler(exHandler);
	}

	@Override
	public void close() throws IOException {
		if (serverThread != null)
			serverThread.close();
	}

}