/**
 * 
 */
package at.ac.tuwien.dslab3.service.biddingClient;

/**
 * @author klaus
 * 
 */
public interface ReplyListener {

	/**
	 * Display the reply from the server.
	 * 
	 * @param reply
	 *            the reply from the server
	 */
	void displayReply(String reply);
}
