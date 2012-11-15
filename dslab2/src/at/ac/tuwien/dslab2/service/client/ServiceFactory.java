package at.ac.tuwien.dslab2.service.client;

public abstract class ServiceFactory {

	public static AuctionClientService getAuctionClientService() {
		return AuctionClientServiceImpl.getInstance();
	}
}
