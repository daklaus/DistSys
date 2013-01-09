/**
 * 
 */
package at.ac.tuwien.dslab3.service.net;

import java.io.IOException;

/**
 * @author klaus
 * 
 */
public interface UDPServerNetworkService extends ServerNetworkService {

	/**
	 * Sends a message to the other machine.
	 * 
	 * @param messange
	 * @throws IOException
	 */
	void send(String messange) throws IOException;

	/**
	 * Listens for a message from other machines. Attention this method blocks
	 * until it receives a reply from the other machine!
	 * 
	 * @return the string which is received
	 * @throws IOException
	 */
	String receive() throws IOException;

}
