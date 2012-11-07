package at.ac.tuwien.dslab1.presentation.client;

import java.lang.Thread.UncaughtExceptionHandler;

public class NotificationExHandlerImpl implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.err
				.println("\nError while waiting for notifications from the server:");
		e.printStackTrace();
		System.err.println("\nStopped waiting notifications!");

		Client.close();
		System.exit(1);
	}

}