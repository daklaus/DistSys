package at.ac.tuwien.dslab3.service.auctionServer;

import at.ac.tuwien.dslab3.service.net.NetworkServiceFactory;
import at.ac.tuwien.dslab3.service.net.TCPServerNetworkService;

import java.io.IOException;
import java.net.SocketException;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class ServerThread extends Thread {
	private final PrivateKey privateKeyServer;
	private volatile boolean stop;
	private final TCPServerNetworkService ns;
	private final AuctionService as;
	private final List<ClientHandler> clientHandlerList;
    private final String keyDirectory;
	private ExecutorService pool;

	public ServerThread(int tcpPort, String analyticsServerRef,
                        String billingServerRef, String keyDirectory, PrivateKey privateKeyServer) throws IOException {
		if (tcpPort <= 0)
			throw new IllegalArgumentException(
					"The TCP port is not set properly");
		if (analyticsServerRef == null)
			throw new IllegalArgumentException("analyticsServerRef is null");
		if (billingServerRef == null)
			throw new IllegalArgumentException("billingServerRef is null");
        if (keyDirectory == null)
			throw new IllegalArgumentException("key directory is null");

		ns = NetworkServiceFactory.newTCPServerNetworkService(tcpPort);
		as = AuctionServerServiceFactory.newAuctionService(analyticsServerRef, billingServerRef);
        this.keyDirectory = keyDirectory;
        clientHandlerList = Collections
				.synchronizedList(new LinkedList<ClientHandler>());
		this.privateKeyServer = privateKeyServer;
	}

	public boolean isConnected() {
		return ns != null;
	}

	@Override
	public void run() {
		ClientHandler c;
		stop = false;

		if (!isConnected())
			throw new IllegalStateException("Service not connected!");

		pool = Executors.newCachedThreadPool(new ClientThreadFactory(
				getUncaughtExceptionHandler()));

		try {
			try {
				while (!stop) {
					c = new ClientHandler(ns.accept(), as, this.keyDirectory, this.privateKeyServer);
					clientHandlerList.add(c);
					pool.execute(c);
				}
			} finally {
				if (pool != null)
					shutdownAndAwaitTermination(pool);
				if (ns != null)
					ns.close();
			}
		} catch (IOException e) {
			// The if-clause down here is because of what is described in
			// http://docs.oracle.com/javase/6/docs/technotes/guides/concurrency/threadPrimitiveDeprecation.html
			// under "What if a thread doesn't respond to Thread.interrupt?"

			// So the socket is closed when the ClientNetworkService is
			// closed and in that special case no error should be
			// propagated.
			if (!stop) {
				UncaughtExceptionHandler eh = this
						.getUncaughtExceptionHandler();
				if (eh != null)
					eh.uncaughtException(this, e);
			}
		}
	}

	public void close() throws IOException {
		stop = true;
		if (pool != null)
			shutdownAndAwaitTermination(pool);
		if (as != null)
			as.close();
		this.interrupt();
		if (ns != null)
			ns.close();
	}

	private void shutdownAndAwaitTermination(ExecutorService pool)
			throws IOException {
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
				// Cancel currently executing tasks
				pool.shutdownNow();

				// Close the clients connections so they can really terminate
				synchronized (clientHandlerList) {
					for (ClientHandler clientHandler : clientHandlerList) {
						clientHandler.close();
					}
				}
				// Wait a while for tasks to respond to being cancelled
				// if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
				// throw new RuntimeException("Pool did not terminate");
				// }
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

}