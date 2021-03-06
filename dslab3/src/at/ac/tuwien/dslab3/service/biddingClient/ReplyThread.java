package at.ac.tuwien.dslab3.service.biddingClient;

import at.ac.tuwien.dslab3.service.net.TCPClientNetworkService;
import at.ac.tuwien.dslab3.service.security.HashMACService;
import org.bouncycastle.util.encoders.Base64;

import java.io.IOException;
import java.net.SocketException;
import java.util.regex.Pattern;

class ReplyThread extends Thread {
	private final BiddingClientService bcs;
	private volatile boolean stop;
	private final TCPClientNetworkService ns;
	private final ReplyListener listener;

	public ReplyThread(TCPClientNetworkService ns, ReplyListener listener,
			BiddingClientService bcs) throws IOException {
		if (ns == null)
			throw new IllegalArgumentException(
					"The TCPClientNetworkService is null");

		this.ns = ns;
		this.bcs = bcs;
		this.listener = listener;
	}

	public boolean isConnected() {
		return ns != null;
	}

	@Override
	public void run() {
		String reply = null;
		stop = false;

		if (!isConnected())
			throw new IllegalStateException("Service not connected!");

		try {
			synchronized (this) {
				this.wait();
			}
			while (!stop) {
				reply = ns.receive().trim();

				if (listener != null) {
					String userName = bcs.getUserName();
					HashMACService hashMACService = bcs.getHashMACService();
					// if it's a list command
					if (userName != null && hashMACService != null
							&& isListResponse(reply)) {
						if (!hasCorrectMAC(reply, userName, hashMACService)) {
							listener.displayReply("The provided Message Authentication Code is incorrect!\n"
									+ "The command will be retransmitted!");
							bcs.submitCommand("!list");
							reply = ns.receive();
							if (!hasCorrectMAC(reply, userName, hashMACService)) {
								listener.displayReply("The provided Message Authentication Code is incorrect again!\n"
										+ "No further retransmission");
								continue;
							}
						}
						String[] chunks = reply.split("\u001f");
						assert (chunks.length == 2);
						reply = chunks[0];
					}
					listener.displayReply(reply);
				}
				synchronized (this) {
					this.wait();
				}
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
		} catch (InterruptedException e) {
			if (!stop) {
				UncaughtExceptionHandler eh = this
						.getUncaughtExceptionHandler();
				if (eh != null)
					eh.uncaughtException(this, e);
			}
		}
	}

	private boolean isListResponse(String reply) {
		return Pattern.compile("\\d+\\..*", Pattern.DOTALL).matcher(reply)
				.matches()
				|| reply.startsWith("There are currently no auctions running!");
	}

	private boolean hasCorrectMAC(String message, String userName,
			HashMACService ks) {
		if (ks == null) {
			throw new IllegalArgumentException("HashMACService is null!");
		}

		try {
			String[] chunks = message.split("\u001f");
			if (chunks.length != 2) {
				return false;
			}
			byte[] data = chunks[0].getBytes();
			byte[] actualMAC = Base64.decode(chunks[1]);
			byte[] expectedMAC = ks.createHashMAC(data);

			return ks.verifyHashMAC(expectedMAC, actualMAC);
		} catch (Exception e) {
			if (listener != null)
				listener.displayReply(e.getMessage());
		}
		return false;
	}

	/**
	 * Close the thread if it is ready but don't interrupt it if it is blocked.
	 */
	public void closeSoft() {
		stop = true;
		if (this.getState() == Thread.State.WAITING) {
			synchronized (this) {
				this.notifyAll();
			}
		}
	}

	public void close() throws IOException {
		stop = true;
		this.interrupt();
	}

}