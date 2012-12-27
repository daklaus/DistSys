package at.ac.tuwien.dslab2.service.biddingClient;

public abstract class BiddingClientServiceFactory {

	public static BiddingClientService newBiddingClientService() {
		return new BiddingClientServiceImpl();
	}
}
