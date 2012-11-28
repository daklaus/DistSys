/**
 * 
 */
package at.ac.tuwien.dslab2.presentation.managementClient;

import java.util.Set;

import at.ac.tuwien.dslab2.domain.Event;
import at.ac.tuwien.dslab2.service.managementClient.SubscriptionListener;

/**
 * @author klaus
 * 
 */
public class SubscriptionListenerImpl implements SubscriptionListener {

	@Override
	public void autoPrintEvent(Set<Event> events) {
		System.out.println();
		System.out.println(ManagementClient.printEvents(events));
		System.out.print(ManagementClient.getPrompt());
	}

}
