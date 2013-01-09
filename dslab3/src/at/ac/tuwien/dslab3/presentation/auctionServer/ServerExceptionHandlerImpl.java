package at.ac.tuwien.dslab3.presentation.auctionServer;

import java.lang.Thread.UncaughtExceptionHandler;

public class ServerExceptionHandlerImpl implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.err.println("\nError from thread '" + t.getName() + "':");
		e.printStackTrace();
		System.err.println("\nGoing to shut down the server!");

		AuctionServer.close();
		System.exit(1);
	}

}
