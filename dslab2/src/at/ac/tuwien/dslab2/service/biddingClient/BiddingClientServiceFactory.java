package at.ac.tuwien.dslab2.service.biddingClient;

public abstract class BiddingClientServiceFactory {

	public static BiddingClientService newAuctionClientService() {
		return new BiddingClientServiceImpl();
	}
}
