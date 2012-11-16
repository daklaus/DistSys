package at.ac.tuwien.dslab2.presentation.server;

import java.lang.Thread.UncaughtExceptionHandler;

public class ServerExceptionHandlerImpl implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.err.println("\nError from thread '" + t.getName() + "':");
		e.printStackTrace();
		System.err.println("\nGoing to shut down the server!");

		Server.close();
		System.exit(1);
	}

}