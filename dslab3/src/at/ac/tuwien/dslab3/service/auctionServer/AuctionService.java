/**
 * 
 */
package at.ac.tuwien.dslab3.service.auctionServer;

import java.io.Closeable;

import at.ac.tuwien.dslab3.domain.Auction;
import at.ac.tuwien.dslab3.domain.Client;
import at.ac.tuwien.dslab3.domain.User;
import at.ac.tuwien.dslab3.service.analyticsServer.AnalyticsServer;

/**
 * @author klaus
 * 
 */
public interface AuctionService extends Closeable {

	/**
	 * Creates an auction
	 * 
	 * @param owner
	 *            user which creates the auction
	 * @param description
	 *            short description of the auction
	 * @param duration
	 *            how long the auction runs from now (in seconds)
	 * @return the auction which has been created if successfull; null otherwise
	 */
	Auction create(User owner, String description, int duration);

	/**
	 * Lists all auctions in one string. Each auction in one line in the form:<br>
	 * 'Apple I' wozniak 10.10.2012 21:00 CET 10000.00 gates<br>
	 * 'description' owner end-time highest-bid highest-bidder
	 * 
	 * @return a string listing all auctions currently running
	 */
	String list();

	/**
	 * Bids a specified amount on an auction
	 * 
	 * @param user
	 *            the user who bids on the auction
	 * @param auctionId
	 *            the id of the auction
	 * @param amount
	 *            the amount to bid
	 * @return the auction of this bid if the auction exists; null otherwise
	 */
	Auction bid(User user, long auctionId, double amount);

	/**
	 * Logs the user in and binds it to the client
	 * 
	 * @param userName
	 *            the users name
	 * @param client
	 *            the client from which the user wants to log in
	 * @return a User object if the login was successful; null otherwise
	 */
	User login(String userName, Client client);

	/**
	 * Logs the user out. This deletes the user.
	 * 
	 * @param user
	 *            the user who should be logged out
	 */
	void logout(User user);

	/**
	 * Get the RMI stub for communicating with the analytics server
	 * 
	 * @return the RMI stub of the <code>AnalyticsServer</code> interface
	 */
	AnalyticsServer getAnalysticsServerRef();

	/**
	 * Get the currently connected clients
	 * 
	 * @return the currently connected clients
	 */
	String getClientList();
}
