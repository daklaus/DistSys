/**
 * 
 */
package at.ac.tuwien.dslab3.service.net;

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
