/**
 * 
 */
package at.ac.tuwien.dslab2.presentation.managementClient;

import at.ac.tuwien.dslab2.domain.Event;
import at.ac.tuwien.dslab2.service.managementClient.SubscriptionListener;

import java.util.Set;

/**
 * @author klaus
 * 
 */
class SubscriptionListenerImpl implements SubscriptionListener {

	@Override
	public void autoPrintEvent(Set<Event> events) {
		System.out.println();
		System.out.print(ManagementClient.printEvents(events));
		System.out.flush();
	}

}
