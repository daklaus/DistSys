package at.ac.tuwien.dslab3.service.biddingClient;

import org.bouncycastle.openssl.PasswordFinder;

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
			int serverPort, int udpPort, String serverPublicKeyFileLocation,
			String clientsKeysDirectory, PasswordFinder passwordFinder) {
		return new BiddingClientServiceImpl(server, serverPort, udpPort,
				serverPublicKeyFileLocation, clientsKeysDirectory, passwordFinder);
	}
}
