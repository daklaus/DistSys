/**
 * 
 */
package at.ac.tuwien.dslab1.service;

/**
 * @author klaus
 * 
 */
public interface ReplyListener {

	/**
	 * Receives a reply
	 * 
	 * @param reply
	 *            the reply which to process
	 */
	public void receiveReply(String reply);
}
