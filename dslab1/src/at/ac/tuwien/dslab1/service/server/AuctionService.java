/**
 * 
 */
package at.ac.tuwien.dslab1.service.server;

import at.ac.tuwien.dslab1.domain.Auction;
import at.ac.tuwien.dslab1.domain.Client;
import at.ac.tuwien.dslab1.domain.User;

/**
 * @author klaus
 * 
 */
public interface AuctionService {

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
	public Auction create(User owner, String description, Integer duration);

	/**
	 * Lists all auctions in one string. Each auction in one line in the form:<br>
	 * 'Apple I' wozniak 10.10.2012 21:00 CET 10000.00 gates<br>
	 * 'description' owner end-time highest-bid highest-bidder
	 * 
	 * @return a string listing all auctions currently running
	 */
	public String list();

	/**
	 * Bids a specified amount on an auction
	 * 
	 * @param user
	 *            the user who bids on the auction
	 * @param auctionId
	 *            the id of the auction
	 * @param amount
	 *            the amount to bid
	 * @return the created Bid object if the bid was successful; null otherwise
	 */
	public void bid(User user, Integer auctionId, double amount);

	/**
	 * Logs the user in and binds it to the client
	 * 
	 * @param userName
	 *            the users name
	 * @param client
	 *            the client from which the user wants to log in
	 * @return a User object if the login was successful; null otherwise
	 */
	public User login(String userName, Client client);

	/**
	 * Logs the user out. This deletes the user.
	 * 
	 * @param user
	 *            the user who should be logged out
	 */
	public void logout(User user);
}
