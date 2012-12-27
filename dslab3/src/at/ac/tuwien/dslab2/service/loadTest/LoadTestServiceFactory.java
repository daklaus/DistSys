package at.ac.tuwien.dslab2.service.loadTest;

import at.ac.tuwien.dslab2.service.managementClient.SubscriptionListener;

import java.io.IOException;

public abstract class LoadTestServiceFactory {
    public static LoadTestService newLoadTest(int auctionServerTcpPort, String billingServerBindingName, String analyticsServerBindingName, String auctionServerHostName, SubscriptionListener subscriptionListener, TimerNotifications timerNotifications) throws IOException {
        return new LoadTestServiceImpl(auctionServerTcpPort, billingServerBindingName, analyticsServerBindingName, auctionServerHostName, subscriptionListener, timerNotifications);
    }
}
