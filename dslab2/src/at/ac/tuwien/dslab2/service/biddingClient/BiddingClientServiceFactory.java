package at.ac.tuwien.dslab2.service.biddingClient;

public abstract class BiddingClientServiceFactory {

	public static BiddingClientService getAuctionClientService() {
		return BiddingClientServiceImpl.getInstance();
	}
}
