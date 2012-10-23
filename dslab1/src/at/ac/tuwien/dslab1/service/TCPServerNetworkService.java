/**
 * 
 */
package at.ac.tuwien.dslab1.service;

/**
 * @author klaus
 * 
 */
public interface TCPServerNetworkService extends ServerNetworkService {

	/**
	 * Accept connections. Attention this method blocks until a client connects!
	 */
	TCPClientNetworkService accept();

}
