/**
 * 
 */
package at.ac.tuwien.dslab3.service.net;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.InterruptibleChannel;

/**
 * @author klaus
 * 
 */
interface ClientNetworkService extends InterruptibleChannel {

	/**
	 * Sends a message to the other machine.
	 * 
	 * @param messange
	 * @throws IOException
	 */
	void send(String messange) throws IOException;

	/**
	 * Listens for a message from the other machine. Attention this method
	 * blocks until it receives a reply from the other machine!
	 * 
	 * @return the string which is received
	 * @throws IOException
	 */
	String receive() throws IOException;

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
