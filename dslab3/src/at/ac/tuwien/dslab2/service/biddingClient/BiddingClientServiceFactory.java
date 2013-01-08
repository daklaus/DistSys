package at.ac.tuwien.dslab2.service.biddingClient;

public abstract class BiddingClientServiceFactory {

	/**
	 * 
	 * @param server
	 *            the host name or IP address of the auction server
	 * @param serverPort
	 *            the TCP port of the auction server
	 * @param udpPort
	 *            the UDP port on which to listen for notifications from the
	 *            server
	 * @return
	 */
	public static BiddingClientService newBiddingClientService(String server,
			int serverPort, int udpPort) {
		return new BiddingClientServiceImpl(server, serverPort, udpPort);
	}
}
