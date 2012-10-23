/**
 * 
 */
package at.ac.tuwien.dslab1.service;

import java.io.IOException;

/**
 * @author klaus
 * 
 */
public interface TCPServerNetworkService extends ServerNetworkService {

	/**
	 * Accept connections. Attention this method blocks until a client connects!
	 * @throws IOException 
	 */
	TCPClientNetworkService accept() throws IOException;

}
