package at.ac.tuwien.dslab2.service.biddingClient;

public abstract class BiddingClientServiceFactory {

	public static AuctionClientService getAuctionClientService() {
		return AuctionClientServiceImpl.getInstance();
	}
}
