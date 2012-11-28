/**
 * 
 */
package at.ac.tuwien.dslab2.service.analyticsServer;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

import at.ac.tuwien.dslab2.domain.Event;
import at.ac.tuwien.dslab2.service.PropertiesService;
import at.ac.tuwien.dslab2.service.PropertiesServiceFactory;
import at.ac.tuwien.dslab2.service.managementClient.MgmtClientCallback;
import at.ac.tuwien.dslab2.service.rmi.RMIServerService;
import at.ac.tuwien.dslab2.service.rmi.RMIServiceFactory;

/**
 * @author klaus
 * 
 */
class AnalyticsServerImpl implements AnalyticsServer {
	private final RMIServerService rss;
	private final AtomicLong subscriptionIdCounter;

	public AnalyticsServerImpl(String bindingName) throws IOException {
		this.subscriptionIdCounter = new AtomicLong(1);

		/*
		 * Read the registry properties file
		 */
		Properties prop = null;
		prop = PropertiesServiceFactory.getPropertiesService()
				.getRegistryProperties();

		// Parse value
		Scanner sc = new Scanner(
				prop.getProperty(PropertiesService.REGISTRY_PROPERTIES_PORT_KEY));
		if (!sc.hasNextInt()) {
			throw new IOException("Couldn't parse the properties value of "
					+ PropertiesService.REGISTRY_PROPERTIES_PORT_KEY);
		}
		int port = sc.nextInt();

		/*
		 * Bind the RMI interface
		 */
		this.rss = RMIServiceFactory.newRMIServerService(port);
		this.rss.bind(bindingName, this);
	}

	@Override
	public long subscribe(String regex, MgmtClientCallback cb)
			throws RemoteException {
		// TODO Auto-generated method stub
		return subscriptionIdCounter.getAndIncrement();
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
		if (rss != null) {
			rss.close();
		}
	}

}
