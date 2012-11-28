/**
 * 
 */
package at.ac.tuwien.dslab2.service.analyticsServer;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import at.ac.tuwien.dslab2.domain.Event;
import at.ac.tuwien.dslab2.domain.Subscription;
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
	private final ConcurrentMap<Long, Subscription> subscriptions;

	public AnalyticsServerImpl(String bindingName) throws IOException {
		if (bindingName == null)
			throw new IllegalArgumentException("bindingName is null");

		this.subscriptionIdCounter = new AtomicLong(1);
		this.subscriptions = new ConcurrentSkipListMap<Long, Subscription>();

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
		if (regex == null)
			throw new IllegalArgumentException("regex is null");

		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

		Subscription s = new Subscription(
				subscriptionIdCounter.getAndIncrement(), p, cb);
		subscriptions.put(s.getId(), s);

		return s.getId();
	}

	@Override
	public void processEvent(Event event) throws RemoteException {
		for (Subscription s : this.subscriptions.values()) {
			if (s.getCb() == null)
				continue;

			// TODO: Match regex
			if (false)
				continue;

			try {
				s.getCb().processEvent(event);
			} catch (RemoteException e) {
				// If there is a problem with the connection to the
				// subscribed client, just ignore it
			}
		}
		// TODO Auto-generated method stub

	}

	@Override
	public void unsubscribe(long id) throws RemoteException {
		subscriptions.remove(id);
	}

	@Override
	public void close() throws IOException {
		if (rss != null) {
			rss.close();
		}
	}

}
