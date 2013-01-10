/**
 * 
 */
package at.ac.tuwien.dslab3.service.biddingClient;

import java.util.concurrent.BlockingQueue;

/**
 * @author klaus
 * 
 */
public class FilterReplyListener implements ReplyListener {
	private final ReplyListener forwardReplyListener;
	private final BlockingQueue<String> forwardQueue;

	private boolean forwardToListener;
	private boolean forwardToQueue;

	public FilterReplyListener(ReplyListener forwardReplyListener,
			BlockingQueue<String> forwardQueue) {
		this.forwardReplyListener = forwardReplyListener;
		this.forwardQueue = forwardQueue;

		this.forwardToListener = true;
		this.forwardToQueue = true;
	}

	@Override
	public void displayReply(String reply) {
		if (forwardToListener) {
			if (this.forwardReplyListener != null)
				this.forwardReplyListener.displayReply(reply);
		}
		if (forwardToQueue) {
			if (this.forwardQueue != null)
				this.forwardQueue.offer(reply);
		}
	}

	public boolean isForwardToListener() {
		return this.forwardToListener;
	}

	public void setForwardToListener(boolean forwardToListener) {
		this.forwardToListener = forwardToListener;
	}

	public boolean isForwardToQueue() {
		return this.forwardToQueue;
	}

	public void setForwardToQueue(boolean forwardToQueue) {
		this.forwardToQueue = forwardToQueue;
	}

}
