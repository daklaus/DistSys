package at.ac.tuwien.dslab2.service.auctionServer;

import java.io.IOException;

public abstract class AuctionServerServiceFactory {

	public static AuctionServerService getAuctionServerService() {
		return AuctionServerServiceImpl.getInstance();
	}

	static AuctionService newAuctionService(String analyticsServerRef,
			String billingServerRef) throws IOException {
		return new AuctionServiceImpl(analyticsServerRef, billingServerRef);
	}
}
