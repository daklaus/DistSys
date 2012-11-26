/**
 * 
 */
package at.ac.tuwien.dslab2.service.analyticsServer;

import java.io.IOException;
import java.rmi.RemoteException;

import at.ac.tuwien.dslab2.domain.Event;
import at.ac.tuwien.dslab2.service.managementClient.MgmtClientCallback;

/**
 * @author klaus
 * 
 */
class AnalyticsServerImpl implements AnalyticsServer {

	@Override
	public long subscribe(String regex, MgmtClientCallback cb)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void processEvent(Event event) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unsubscribe(long id) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

}
