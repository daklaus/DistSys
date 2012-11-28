package at.ac.tuwien.dslab2.service.loadTest;

import at.ac.tuwien.dslab2.service.biddingClient.BiddingClientService;

import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

public class AuctionListingHandler extends TimerTask {

    private final BiddingClientService biddingClientService;
    private final BlockingQueue<String> queue;

    public AuctionListingHandler(BiddingClientService biddingClientService, BlockingQueue<String> queue) {
        this.biddingClientService = biddingClientService;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            biddingClientService.submitCommand("!list");
            String response = queue.take();
            System.out.println(Thread.currentThread().getName() + ": !list");
            System.out.println(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
