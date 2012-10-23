/**
 * 
 */
package at.ac.tuwien.dslab1.service.client;

/**
 * @author klaus
 * 
 */
public interface NotificationListener {

	/**
	 * Notify the user about an overbid on an auction.
	 * 
	 * @param description
	 *            the description of the auction
	 */
	public void newBid(String description);

	/**
	 * Notify the user about the end of an auction.
	 * 
	 * @param winner
	 *            the winner of the auction
	 * @param amount
	 *            the highest bid at the end of the auction
	 * @param description
	 *            the auction's description
	 */
	public void auctionEnded(String winner, double amount, String description);
}
