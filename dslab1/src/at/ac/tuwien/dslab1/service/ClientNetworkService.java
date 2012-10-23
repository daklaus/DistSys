/**
 * 
 */
package at.ac.tuwien.dslab1.service;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author klaus
 * 
 */
interface ClientNetworkService extends Closeable {

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

}
