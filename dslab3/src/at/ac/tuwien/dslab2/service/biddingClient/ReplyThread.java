package at.ac.tuwien.dslab2.service.biddingClient;

import java.io.IOException;
import java.net.SocketException;
import java.util.regex.Pattern;

import at.ac.tuwien.dslab2.service.KeyService;
import at.ac.tuwien.dslab2.service.net.TCPClientNetworkService;

class ReplyThread extends Thread {
    private final BiddingClientService bcs;
    private final KeyService ks;
    private volatile boolean stop;
	private final TCPClientNetworkService ns;
	private final ReplyListener listener;

    public ReplyThread(TCPClientNetworkService ns, ReplyListener listener, BiddingClientService bcs, KeyService ks)
			throws IOException {
		if (ns == null)
			throw new IllegalArgumentException(
					"The TCPClientNetworkService is null");

		this.ns = ns;
        this.bcs = bcs;
        this.ks = ks;
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
			try {
				while (!stop) {
					reply = ns.receive();

					if (listener != null){
                        String userName = bcs.getUserName();
                        //if it's a list command
                        if (Pattern.compile("\\d+\\..*", Pattern.DOTALL).matcher(reply).matches()
                            && userName != null) {
                            if (!hasCorrectMAC(reply, userName)) {
                                listener.displayReply(
                                        "The provided Message Authentication Code is incorrect!\n"
                                      + "The command will be retransmitted!");
                                bcs.submitCommand("!list");
                                reply = ns.receive();
                                if (!hasCorrectMAC(reply, userName)) {
                                    listener.displayReply(
                                        "The provided Message Authentication Code is incorrect again!"
                                      + "No further retransmission");
                                    continue;
                                }
                            }
                        }
                        listener.displayReply(reply);
                    }
				}
			} finally {
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
			if (!(stop && e.getClass() == SocketException.class)) {
				UncaughtExceptionHandler eh = this
						.getUncaughtExceptionHandler();
				if (eh != null)
					eh.uncaughtException(this, e);
			}
		}
	}

    private boolean hasCorrectMAC(String message, String userName) {
        String[] chunks = message.split("\u001f");
        if (chunks.length != 2) {
            return false;
        }
        byte[] hMAC = chunks[1].getBytes();


        return true;
    }

	public void close() throws IOException {
		stop = true;
		this.interrupt();
		if (ns != null)
			ns.close();
	}

}