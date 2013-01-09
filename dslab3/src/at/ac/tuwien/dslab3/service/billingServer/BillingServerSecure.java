/**
 * 
 */
package at.ac.tuwien.dslab3.service.billingServer;

import java.io.Closeable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import at.ac.tuwien.dslab3.domain.Bill;
import at.ac.tuwien.dslab3.domain.PriceSteps;

/**
 * @author klaus
 * 
 */
public interface BillingServerSecure extends Closeable, Remote {

	/**
	 * This method returns the current configuration of price steps.
	 * 
	 * @return the <code>PriceSteps</code> class which represent the current
	 *         price steps configuration
	 * @throws RemoteException
	 */
	PriceSteps getPriceSteps() throws RemoteException;

	/**
	 * This method allows to create a price step for a given price interval. To
	 * represent an infinite value for the endPrice parameter you can use the
	 * value 0.
	 * 
	 * @param startPrice
	 *            The start of the interval (exclusive)
	 * @param endPrice
	 *            The end of the interval (inclusive)
	 * @param fixedPrice
	 *            The fixed fee for this price interval
	 * @param variablePricePercent
	 *            The relative fee (in percent) of this price interval
	 * @throws RemoteException
	 *             if any of the specified values are negative or if the
	 *             provided price interval collides (overlaps) with an existing
	 *             price step
	 */
	void createPriceStep(double startPrice, double endPrice, double fixedPrice,
			double variablePricePercent) throws RemoteException;

	/**
	 * This method allows to delete a price step from the pricing curve.
	 * 
	 * @param startPrice
	 *            The start of the interval
	 * @param endPrice
	 *            The end of the interval
	 * @throws RemoteException
	 *             if the specified interval does not match an existing price
	 *             step interval
	 */
	void deletePriceStep(double startPrice, double endPrice)
			throws RemoteException;

	/**
	 * This method is called by the auction server as soon as an auction has
	 * ended. The billing server stores the auction result and later uses this
	 * information to calculate the bill for a user.
	 * 
	 * @param user
	 *            The user who created the auction
	 * @param auctionID
	 *            The ID of the auction
	 * @param price
	 *            The highest bid at the end of the auction.
	 */
	void billAuction(String user, long auctionID, double price)
			throws RemoteException;

	/**
	 * This method calculates and returns the bill for a given user, based on
	 * the price steps stored within the billing server.
	 * 
	 * @param user
	 *            The user for whom the bill should be returned
	 * @return the bill which represents the total history of all auctions
	 *         created by the user.
	 */
	Bill getBill(String user) throws RemoteException;
}
