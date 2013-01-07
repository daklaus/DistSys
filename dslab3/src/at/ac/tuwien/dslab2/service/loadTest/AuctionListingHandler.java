package at.ac.tuwien.dslab2.service.loadTest;

import at.ac.tuwien.dslab2.service.biddingClient.BiddingClientService;

import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

class AuctionListingHandler extends TimerTask {

    private final BiddingClientService biddingClientService;
    private final BlockingQueue<String> queue;
    private Thread timerThread;
    private final TimerNotifications timerNotifications;

    public AuctionListingHandler(BiddingClientService biddingClientService, BlockingQueue<String> queue, TimerNotifications timerNotifications) {
        this.biddingClientService = biddingClientService;
        this.queue = queue;
        this.timerNotifications = timerNotifications;
    }

    @Override
    public void run() {
        try {
            this.timerThread = Thread.currentThread();
            biddingClientService.submitCommand("!list");
            String response = queue.take();
            timerNotifications.newListing(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {

        }
    }

    @Override
    public boolean cancel() {
        if (this.timerThread != null && this.timerThread.isAlive()) {
            this.timerThread.interrupt();
        }
        return super.cancel();
    }
}
