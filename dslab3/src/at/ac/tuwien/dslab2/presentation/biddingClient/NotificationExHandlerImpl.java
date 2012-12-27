package at.ac.tuwien.dslab2.presentation.biddingClient;

import java.lang.Thread.UncaughtExceptionHandler;

public class NotificationExHandlerImpl implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.err
				.println("\nError while waiting for notifications from the server:");
		e.printStackTrace();
		System.err.println("\nStopped waiting notifications!");

		BiddingClient.close();
		System.exit(1);
	}

}
