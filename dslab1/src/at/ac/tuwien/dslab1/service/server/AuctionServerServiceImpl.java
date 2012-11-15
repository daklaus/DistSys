package at.ac.tuwien.dslab1.service.server;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

public class AuctionServerServiceImpl implements AuctionServerService {

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
	public void start(int tcpPort) throws IOException {
		if (serverThread != null && serverThread.isAlive())
			return;
		if (tcpPort <= 0)
			throw new IllegalArgumentException(
					"The TCP port is not set properly");

		// Start server thread
		serverThread = new ServerThread(tcpPort);
		serverThread.setName("Server thread");
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
