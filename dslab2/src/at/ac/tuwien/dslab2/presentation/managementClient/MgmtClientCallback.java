/**
 * 
 */
package at.ac.tuwien.dslab2.presentation.managementClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

import at.ac.tuwien.dslab2.domain.Event;

/**
 * @author klaus
 * 
 */
public interface MgmtClientCallback extends Remote {

	/**
	 * This method is invoked by the analytics server each time a new event
	 * happens (e.g., user logged in, auction started, ...). The analytics
	 * server forwards these events to subscribed client through this callback
	 * method if the regular expression specified in the subscription matches
	 * the event.
	 * 
	 * @param event
	 *            The event that happened and should be processed
	 * @throws RemoteException
	 */
	void processEvent(Event event) throws RemoteException;
}
