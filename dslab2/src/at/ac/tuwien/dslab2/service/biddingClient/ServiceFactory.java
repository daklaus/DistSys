package at.ac.tuwien.dslab2.service.biddingClient;

public abstract class ServiceFactory {

	public static AuctionClientService getAuctionClientService() {
		return AuctionClientServiceImpl.getInstance();
	}
}
