package at.ac.tuwien.dslab2.service.loadTest;

import java.io.IOException;

public abstract class LoadTestServiceFactory {
    public static LoadTestService newLoadTest(int auctionServerTcpPort, String billingServerBindingName, String analyticsServerBindingName, String auctionServerHostName) throws IOException {
        return new LoadTestServiceImpl(auctionServerTcpPort, billingServerBindingName, analyticsServerBindingName, auctionServerHostName);
    }
}
