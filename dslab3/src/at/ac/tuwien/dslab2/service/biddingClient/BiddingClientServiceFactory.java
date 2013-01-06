package at.ac.tuwien.dslab2.service.biddingClient;

import at.ac.tuwien.dslab2.service.security.KeyService;

public abstract class BiddingClientServiceFactory {

	public static BiddingClientService newBiddingClientService(KeyService ks) {
		return new BiddingClientServiceImpl(ks);
	}
}
