/**
 * 
 */
package at.ac.tuwien.dslab2.service.managementClient;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import at.ac.tuwien.dslab2.domain.Bill;
import at.ac.tuwien.dslab2.domain.Event;
import at.ac.tuwien.dslab2.domain.PriceSteps;
import at.ac.tuwien.dslab2.service.PropertiesService;
import at.ac.tuwien.dslab2.service.PropertiesServiceFactory;
import at.ac.tuwien.dslab2.service.analyticsServer.AnalyticsServer;
import at.ac.tuwien.dslab2.service.billingServer.BillingServer;
import at.ac.tuwien.dslab2.service.billingServer.BillingServerSecure;
import at.ac.tuwien.dslab2.service.rmi.RMIClientService;
import at.ac.tuwien.dslab2.service.rmi.RMIServiceFactory;

/**
 * @author klaus
 * 
 */
public class ManagementClientServiceImpl implements ManagementClientService {
	private final RMIClientService rcs;
	private final BillingServer bs;
	private final AnalyticsServer as;
	private BillingServerSecure bss;
	private volatile boolean auto;
	private final SortedSet<Event> events;
	private volatile Event latestPrintedEvent;
	private SubscriptionListener listener;

	public ManagementClientServiceImpl(String analyticsServerRef,
			String billingServerRef) throws IOException {
		auto = false;
		latestPrintedEvent = null;
		events = new ConcurrentSkipListSet<Event>();

		/*
		 * Read the properties file
		 */
		Properties prop = PropertiesServiceFactory.getPropertiesService()
				.getRegistryProperties();

		// Check if keys exist
		if (!prop.containsKey(PropertiesService.REGISTRY_PROPERTIES_HOST_KEY)) {
			throw new IOException("Properties file doesn't contain the key "
					+ PropertiesService.REGISTRY_PROPERTIES_HOST_KEY);
		}
		if (!prop.containsKey(PropertiesService.REGISTRY_PROPERTIES_PORT_KEY)) {
			throw new IOException("Properties file doesn't contain the key "
					+ PropertiesService.REGISTRY_PROPERTIES_PORT_KEY);
		}

		// Parse value
		int port;
		String host;
		host = prop.getProperty(PropertiesService.REGISTRY_PROPERTIES_HOST_KEY);

		Scanner sc = new Scanner(
				prop.getProperty(PropertiesService.REGISTRY_PROPERTIES_PORT_KEY));
		if (!sc.hasNextInt()) {
			throw new IOException("Couldn't parse the properties value of "
					+ PropertiesService.REGISTRY_PROPERTIES_PORT_KEY);
		}
		port = sc.nextInt();

		/*
		 * Get the RMI interfaces
		 */
		this.rcs = RMIServiceFactory.newRMIClientService(host, port);
		this.as = (AnalyticsServer) rcs.lookup(analyticsServerRef);
		this.bs = (BillingServer) rcs.lookup(billingServerRef);
	}

	@Override
	public void login(String userName, String password) {
		// TODO Auto-generated method stub

	}

	@Override
	public PriceSteps steps() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addStep(double startPrice, double endPrice, double fixedPrice,
			double variablePricePercent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeStep(double startPrice, double endPrice) {
		// TODO Auto-generated method stub

	}

	@Override
	public Bill bill(String userName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void logout() {
		// TODO Auto-generated method stub

	}

	@Override
	public long subscribe(String regex) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void unsubscribe(long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSubscriptionListener(SubscriptionListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public SortedSet<Event> print() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void auto() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws IOException {
		if (rcs != null)
			rcs.close();
	}

}
