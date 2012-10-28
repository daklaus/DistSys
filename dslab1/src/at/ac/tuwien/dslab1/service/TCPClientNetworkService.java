/**
 * 
 */
package at.ac.tuwien.dslab1.service;

import java.net.InetAddress;

/**
 * @author klaus
 * 
 */
public interface TCPClientNetworkService extends ClientNetworkService {
	public static final char terminationChar = '\0';

	InetAddress getLocalAddress();

	InetAddress getAddress();

	Integer getLocalPort();

	Integer getPort();
}
