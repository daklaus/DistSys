package at.ac.tuwien.dslab2.service.loadTest;

import at.ac.tuwien.dslab2.service.biddingClient.BiddingClientService;

import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

class AuctionCreationHandler extends TimerTask {

    private final BlockingQueue<String> queue;
    private final BiddingClientService biddingClientService;
    private final int duration;
    private Thread timerThread;
    private final TimerNotifications timerNotifications;

    public AuctionCreationHandler(BiddingClientService biddingClientService, BlockingQueue<String> queue, int duration, TimerNotifications timerNotifications) {
        this.queue = queue;
        this.biddingClientService = biddingClientService;
        this.duration = duration;
        this.timerNotifications = timerNotifications;
    }
    @Override
    public void run() {
        try {
            this.timerThread = Thread.currentThread();
            biddingClientService.submitCommand("!create " + duration + " description");
            String response = queue.take();
            timerNotifications.newCreation(response);
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
