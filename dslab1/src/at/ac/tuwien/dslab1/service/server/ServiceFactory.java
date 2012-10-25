package at.ac.tuwien.dslab1.service.server;

public abstract class ServiceFactory {

	public static AuctionServerService getAuctionServerService() {
		return AuctionServerServiceImpl.getInstance();
	}

	static AuctionService getAuctionService() {
		return AuctionServiceImpl.getInstance();
	}
}
