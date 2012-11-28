package at.ac.tuwien.dslab2.service.loadTest;

import at.ac.tuwien.dslab2.service.biddingClient.BiddingClientService;

import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

public class AuctionCreationHandler extends TimerTask {

    private final BlockingQueue<String> queue;
    private final BiddingClientService biddingClientService;
    private final int duration;

    public AuctionCreationHandler(BiddingClientService biddingClientService, BlockingQueue<String> queue, int duration) {
        this.queue = queue;
        this.biddingClientService = biddingClientService;
        this.duration = duration;
    }
    @Override
    public void run() {
        try {
            biddingClientService.submitCommand("!create " + duration + " description");
            String response = queue.take();
            System.out.println(Thread.currentThread().getName() + ": !create");
            System.out.println(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
