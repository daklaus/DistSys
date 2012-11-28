package at.ac.tuwien.dslab2.service.loadTest;

import at.ac.tuwien.dslab2.service.biddingClient.BiddingClientService;

import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

public class AuctionCreationHandler extends TimerTask {

    private final BlockingQueue<String> queue;
    private final BiddingClientService biddingClientService;
    private final int duration;
    private Thread timerThread;

    public AuctionCreationHandler(BiddingClientService biddingClientService, BlockingQueue<String> queue, int duration) {
        this.queue = queue;
        this.biddingClientService = biddingClientService;
        this.duration = duration;
    }
    @Override
    public void run() {
        try {
            this.timerThread = Thread.currentThread();
            biddingClientService.submitCommand("!create " + duration + " description");
            String response = queue.take();
            //System.out.println(this.timerThread.getName() + ": !create");
            //System.out.println(response);
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
