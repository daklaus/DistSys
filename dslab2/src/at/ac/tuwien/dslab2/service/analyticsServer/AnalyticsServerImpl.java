/**
 * 
 */
package at.ac.tuwien.dslab2.service.analyticsServer;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.UUID;

import at.ac.tuwien.dslab2.domain.Event;
import at.ac.tuwien.dslab2.service.managementClient.MgmtClientCallback;

/**
 * @author klaus
 * 
 */
class AnalyticsServerImpl implements AnalyticsServer {

	@Override
	public UUID subscribe(String regex, MgmtClientCallback cb)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processEvent(Event event) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unsubscribe(UUID id) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

}
