package at.ac.tuwien.dslab2.service.auctionServer;

import java.io.IOException;

public abstract class ServiceFactory {

	public static AuctionServerService getAuctionServerService() {
		return AuctionServerServiceImpl.getInstance();
	}

	static AuctionService newAuctionService(String billingServerRef,
			String analyticsServerRef) throws IOException {
		return new AuctionServiceImpl(billingServerRef, analyticsServerRef);
	}
}
