/**
 * 
 */
package at.ac.tuwien.dslab2.service.net;

import java.net.InetAddress;

/**
 * @author klaus
 * 
 */
public interface TCPClientNetworkService extends ClientNetworkService {
	public static final char terminationChar = '\0';

	/**
	 * Get the local IP address to which the network service is bound
	 * 
	 * @return the local IP address
	 */
	InetAddress getLocalAddress();

	/**
	 * Get the server IP address to which the network service is bound
	 * 
	 * @return the server IP address
	 */
	InetAddress getAddress();

	/**
	 * Get the local port to which the network service is bound
	 * 
	 * @return the local port
	 */
	int getLocalPort();

	/**
	 * Get the server port to which the network service is bound
	 * 
	 * @return the server port
	 */
	int getPort();
}
