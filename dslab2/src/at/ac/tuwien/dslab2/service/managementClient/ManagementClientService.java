/**
 * 
 */
package at.ac.tuwien.dslab2.service.managementClient;

import java.io.Closeable;
import java.util.SortedSet;

import at.ac.tuwien.dslab2.domain.Bill;
import at.ac.tuwien.dslab2.domain.Event;
import at.ac.tuwien.dslab2.domain.PriceSteps;

/**
 * @author klaus
 * 
 */
public interface ManagementClientService extends Closeable {

	/**
	 * Login at the billing server.
	 * 
	 * @param userName
	 * @param password
	 * @throws AlreadyLoggedInException
	 */
	void login(String userName, String password)
			throws AlreadyLoggedInException;

	/**
	 * Get all existing price steps.
	 * 
	 * @return the price steps
	 */
	PriceSteps steps();

	/**
	 * Add a new price step.
	 * 
	 * @param startPrice
	 *            The minimum of the interval (exclusive)
	 * @param endPrice
	 *            The maximum of the interval (inclusive)
	 * @param fixedPrice
	 *            The fixed fee for this price step
	 * @param variablePricePercent
	 *            The variable fee in percent for this price step
	 */
	void addStep(double startPrice, double endPrice, double fixedPrice,
			double variablePricePercent);

	/**
	 * Remove an existing price step.
	 * 
	 * @param startPrice
	 *            The minimum of the interval
	 * @param endPrice
	 *            The maximum of the interval
	 */
	void removeStep(double startPrice, double endPrice);

	/**
	 * This method gets the bill for a certain user name. This is the list of
	 * finished auctions (plus auction fees) that have been created by the
	 * specified user.
	 * 
	 * @param userName
	 *            The user name of the user whom bill will be returned
	 * @return
	 */
	Bill bill(String userName);

	/**
	 * Set the client into "logged out" state. After this command, users have to
	 * use the login method again in order to interact with the billing server.
	 */
	void logout();

	/**
	 * Subscribe for events with a specified subscription filter (regular
	 * expression). A user can add multiple subscriptions.
	 * 
	 * @param regex
	 *            The subscription filter
	 * @return the unique identifier of the subscription returned from the
	 *         analytics server
	 */
	long subscribe(String regex);

	/**
	 * Terminate an existing subscription with a specific identifier (
	 * <code>id</code>).
	 * 
	 * @param id
	 *            The unique identifier of the subscription previously returned
	 *            from the subscribe method
	 */
	void unsubscribe(long id);

	/**
	 * Sets the listener for the automatic printing of events when they arrive.
	 * 
	 * @param listener
	 */
	void setSubscriptionListener(SubscriptionListener listener);

	/**
	 * Returns all events that are currently in the buffer and have not been
	 * printed before.
	 * 
	 * @return a set of all events described above
	 */
	SortedSet<Event> print();

	/**
	 * Turns the client in automatic subscription printing mode. Before this to
	 * actually work you once need to set the <code>SubscriptionListener</code>
	 * with the <code>setSubscriptionListener</code> method.
	 */
	void auto();

	/**
	 * Turns off automatic subscription printing mode.
	 */
	void hide();
}
