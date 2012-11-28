package at.ac.tuwien.dslab2.service.loadTest;

import at.ac.tuwien.dslab2.service.biddingClient.ReplyListener;

import java.util.concurrent.BlockingQueue;

public class LoadTestReplyListener implements ReplyListener {

    private final BlockingQueue<String> queue;

    public LoadTestReplyListener(BlockingQueue<String> queue) {
        this.queue = queue;
    }

        @Override
        public void displayReply(String reply) {
            try {
                this.queue.put(reply);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

}
