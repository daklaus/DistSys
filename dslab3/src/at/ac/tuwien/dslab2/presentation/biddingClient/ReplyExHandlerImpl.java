package at.ac.tuwien.dslab2.presentation.biddingClient;

import java.lang.Thread.UncaughtExceptionHandler;

class ReplyExHandlerImpl implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.err
				.println("\nError while waiting for replies from the server:");
		e.printStackTrace();
		System.err.println("\nStopped waiting for replies!");

		// BiddingClient.close();
		// System.exit(1);
	}

}
