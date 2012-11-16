package at.ac.tuwien.dslab2.service.auctionServer;

public abstract class ServiceFactory {

	public static AuctionServerService getAuctionServerService() {
		return AuctionServerServiceImpl.getInstance();
	}

	static AuctionService getAuctionService() {
		return AuctionServiceImpl.getInstance();
	}
}
