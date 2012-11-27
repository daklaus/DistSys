/**
 * 
 */
package at.ac.tuwien.dslab2.service.managementClient;

import java.io.IOException;
import java.rmi.RemoteException;
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
class ManagementClientServiceImpl implements ManagementClientService {
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
		this.as = (AnalyticsServer) this.rcs.lookup(analyticsServerRef);
		this.bs = (BillingServer) this.rcs.lookup(billingServerRef);
	}

	@Override
	public void login(String userName, String password)
			throws AlreadyLoggedInException, RemoteException {
		if (userName == null)
			throw new IllegalArgumentException("user name is null");
		if (password == null)
			throw new IllegalAccessError("password is null");

		if (this.bss != null)
			throw new AlreadyLoggedInException();

		this.bss = this.bs.login(userName, password);
	}

	@Override
	public PriceSteps steps() throws LoggedOutException, RemoteException {
		if (this.bss == null)
			throw new LoggedOutException();

		return this.bss.getPriceSteps();
	}

	@Override
	public void addStep(double startPrice, double endPrice, double fixedPrice,
			double variablePricePercent) throws LoggedOutException,
			RemoteException {
		if (this.bss == null)
			throw new LoggedOutException();

		this.bss.createPriceStep(startPrice, endPrice, fixedPrice,
				variablePricePercent);
	}

	@Override
	public void removeStep(double startPrice, double endPrice)
			throws LoggedOutException {
		if (this.bss == null)
			throw new LoggedOutException();
		// TODO Auto-generated method stub

	}

	@Override
	public Bill bill(String userName) throws LoggedOutException {
		if (this.bss == null)
			throw new LoggedOutException();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void logout() throws AlreadyLoggedOutException {
		if (this.bss != null)
			throw new AlreadyLoggedOutException();
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
