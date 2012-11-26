package at.ac.tuwien.dslab2.service.analyticsServer;

import java.io.Closeable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

import at.ac.tuwien.dslab2.domain.Event;
import at.ac.tuwien.dslab2.presentation.managementClient.MgmtClientCallback;

public interface AnalyticsServer extends Closeable, Remote {

	/**
	 * This is invoked by management client(s) to register for notifications.
	 * 
	 * @param regex
	 *            The filter (specified as a regular expression string) that
	 *            determines which types of events the client is interested in
	 * @param cb
	 *            The callback object reference which is used to send
	 *            notifications to the clients.
	 * @return a unique subscription identifier.
	 * @throws RemoteException
	 */
	UUID subscribe(String regex, MgmtClientCallback cb) throws RemoteException;

	/**
	 * This method is invoked by the bidding server each time a new event
	 * happens (e.g., user logged in, auction started, ...). The analytics
	 * server forwards these events to subscribed clients, and possibly
	 * generates new events which are also forwarded to clients with a matching
	 * subscriptions. If the server finds out that a subscription cannot be
	 * processed because a client is unavailable (e.g., connection exception),
	 * then the subscriptions is automatically removed from the analytics
	 * server.
	 * 
	 * @param event
	 *            The event that happened and should be processed
	 * @throws RemoteException
	 */
	void processEvent(Event event) throws RemoteException;

	/**
	 * This method is invoked by the management client(s) to terminate an
	 * existing event subscription. The method receives the subscription
	 * identifier which has been previously received from the subscribe method.
	 * 
	 * @param id
	 *            An <code>UUID</code> that identifies the subscription
	 * @throws RemoteException
	 */
	void unsubscribe(UUID id) throws RemoteException;
}
