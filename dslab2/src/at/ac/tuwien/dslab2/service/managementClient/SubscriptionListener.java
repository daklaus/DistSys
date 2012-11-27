/**
 * 
 */
package at.ac.tuwien.dslab2.service.managementClient;

import java.util.SortedSet;

import at.ac.tuwien.dslab2.domain.Event;

/**
 * @author klaus
 * 
 */
public interface SubscriptionListener {

	/**
	 * This method is invoked if the client is in auto print mode and an event
	 * arrives
	 * 
	 * @param event
	 *            The event that happened and should be processed
	 */
	void autoPrintEvent(SortedSet<Event> events);
}
