package at.ac.tuwien.dslab2.service.biddingClient;

import at.ac.tuwien.dslab2.service.security.HashMACService;

public abstract class BiddingClientServiceFactory {

	public static BiddingClientService newBiddingClientService(HashMACService ks) {
		return new BiddingClientServiceImpl(ks);
	}
}
