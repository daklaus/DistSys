package at.ac.tuwien.dslab1.service.server;

import java.io.IOException;

class ClientThread extends Thread {

	private ClientHandler clientHandler;

	public ClientThread(ClientHandler clientHandler) {
		if (clientHandler == null)
			throw new IllegalArgumentException("The client handler is null");

		this.clientHandler = clientHandler;
	}

	@Override
	public void run() {
		clientHandler.run();
	}

	@Override
	public void interrupt() {
		try {
			clientHandler.close();
		} catch (IOException e) {
			UncaughtExceptionHandler eh = Thread.currentThread()
					.getUncaughtExceptionHandler();
			if (eh != null)
				eh.uncaughtException(this, e);
		}
		super.interrupt();
	}

}