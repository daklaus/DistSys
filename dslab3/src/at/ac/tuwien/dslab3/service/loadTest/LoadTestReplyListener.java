package at.ac.tuwien.dslab3.service.loadTest;

import at.ac.tuwien.dslab3.service.biddingClient.ReplyListener;

import java.util.concurrent.BlockingQueue;
import java.util.regex.Pattern;

class LoadTestReplyListener implements ReplyListener {

    private final BlockingQueue<String> auctionListQueue;
    private final BlockingQueue<String> auctionBiddingQueue;
    private final BlockingQueue<String> auctionCreateQueue;

    public LoadTestReplyListener(BlockingQueue<String> auctionListQueue, BlockingQueue<String> auctionBiddingQueue, BlockingQueue<String> auctionCreateQueue) {
        this.auctionListQueue = auctionListQueue;
        this.auctionBiddingQueue = auctionBiddingQueue;
        this.auctionCreateQueue = auctionCreateQueue;
    }

    @Override
    public void displayReply(String reply) {
        reply = reply.trim();
        try {
            if (Pattern.compile("\\d+\\..*", Pattern.DOTALL).matcher(reply).matches()) {
                this.auctionListQueue.put(reply);
            } else if (reply.startsWith("You successfully") || reply.startsWith("You unsuccessfully") || reply.startsWith("Auction with id")) {
                this.auctionBiddingQueue.put(reply);
            } else if (reply.startsWith("An auction")) {
                this.auctionCreateQueue.put(reply);
            } else if (reply.startsWith("Invalid")) {
                  System.out.println(reply);
            }

        } catch (InterruptedException e) {
            //throw new RuntimeException(e);
        }
    }

}
