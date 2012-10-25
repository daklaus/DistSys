/**
 * 
 */
package at.ac.tuwien.dslab1.service.client;

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
