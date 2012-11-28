package at.ac.tuwien.dslab2.service.loadTest;

import at.ac.tuwien.dslab2.service.biddingClient.BiddingClientService;

import java.io.IOException;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Pattern;

public class AuctionBiddingHandler extends TimerTask {

    private final BiddingClientService biddingClientService;
    private final long currentTime;
    private final BlockingQueue<String> queue;

    public AuctionBiddingHandler(BiddingClientService biddingClientService, BlockingQueue<String> queue) throws IOException {
        this.biddingClientService = biddingClientService;
        this.queue = queue;
        this.currentTime = System.nanoTime();

    }

    @Override
    public void run() {
        try {
            biddingClientService.submitCommand("!list");
            String reply = queue.take();
            Scanner scanner = new Scanner(reply.trim());
            scanner.useDelimiter(Pattern.compile("\\.\\s+.*\\n?\\s*"));
            if (!scanner.hasNext()) return;

            while (scanner.hasNext()) {
                int auctionId = scanner.nextInt();
                double price = System.nanoTime() - currentTime;
                biddingClientService.submitCommand("!bid " + auctionId + " " + price);
                queue.take();
            }
            scanner.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
