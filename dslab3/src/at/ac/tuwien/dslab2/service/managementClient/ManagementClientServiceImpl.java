/**
 * 
 */
package at.ac.tuwien.dslab2.service.managementClient;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
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
	private final NavigableSet<Event> events;
	private final Object latestEventLockObj;
	private volatile Event latestPrintedEvent;
	private volatile SubscriptionListener listener;
	private final MgmtClientCallback callback;
	private final List<Long> subscriptionIds;

	public ManagementClientServiceImpl(String analyticsServerRef,
			String billingServerRef) throws IOException {
		if (analyticsServerRef == null)
			throw new IllegalArgumentException("analyticsServerRef is null");
		if (billingServerRef == null)
			throw new IllegalArgumentException("billingServerRef is null");

		auto = false;
		latestEventLockObj = new Object();
		latestPrintedEvent = null;
		events = new ConcurrentSkipListSet<Event>();
		subscriptionIds = new ArrayList<Long>();
		callback = new MgmtClientCallbackImpl();
		UnicastRemoteObject.exportObject(callback, 0);

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
			throws LoggedOutException, RemoteException {
		if (this.bss == null)
			throw new LoggedOutException();

		this.bss.deletePriceStep(startPrice, endPrice);
	}

	@Override
	public Bill bill(String userName) throws LoggedOutException,
			RemoteException {
		if (this.bss == null)
			throw new LoggedOutException();

		return this.bss.getBill(userName);
	}

	@Override
	public void logout() throws LoggedOutException {
		if (this.bss == null)
			throw new LoggedOutException();

		this.bss = null;
	}

	@Override
	public long subscribe(String regex) throws RemoteException {
		if (regex == null)
			throw new IllegalArgumentException("regex is null");

		long id = this.as.subscribe(regex, callback);
		synchronized (this.subscriptionIds) {
			this.subscriptionIds.add(id);
		}
		return id;
	}

	@Override
	public void unsubscribe(long id) throws RemoteException {
		synchronized (this.subscriptionIds) {
			if (!this.subscriptionIds.contains(id))
				throw new IllegalStateException(
						"You didn't subscribe for a subscription with ID " + id);

			this.as.unsubscribe(id);

			this.subscriptionIds.remove(id);
		}
	}

	@Override
	public void setSubscriptionListener(SubscriptionListener listener) {
		this.listener = listener;
	}

	@Override
	public Set<Event> print() {
		SortedSet<Event> returnSet;
		synchronized (this.latestEventLockObj) {
			if (latestPrintedEvent != null) {
				returnSet = new TreeSet<Event>(events.tailSet(
						latestPrintedEvent, false));
			} else {
				returnSet = new TreeSet<Event>(events);
			}
			if (returnSet.isEmpty())
				return null;

			latestPrintedEvent = returnSet.last();
		}
		removeOldEvents();
		return returnSet;
	}

	@Override
	public void auto() {
		this.auto = true;

		try {
			synchronized (this.latestEventLockObj) {
				latestPrintedEvent = events.last();
			}
			removeOldEvents();
		} catch (NoSuchElementException e) {
			// Ignore if the event list is empty
		}
	}

	private void removeOldEvents() {
		long time;
		synchronized (latestEventLockObj) {
			if (this.latestPrintedEvent == null)
				return;
			time = this.latestPrintedEvent.getTimestamp() - 60000; // 60s
																	// before
																	// latest
																	// printed
																	// event
		}
		for (Iterator<Event> iterator = this.events.iterator(); iterator
				.hasNext();) {
			Event event = iterator.next();
			if (event.getTimestamp() < time)
				iterator.remove();
		}
	}

	@Override
	public void hide() {
		this.auto = false;
	}

	@Override
	public void close() throws IOException {
		synchronized (this.subscriptionIds) {
			for (Iterator<Long> iterator = this.subscriptionIds.iterator(); iterator
					.hasNext();) {
				Long id = iterator.next();
				try {
					this.as.unsubscribe(id);
				} catch (IOException e) {
					// If the server isn't available, we don't care
				}
				iterator.remove();
			}
		}

		UnicastRemoteObject.unexportObject(callback, false);
		if (rcs != null)
			rcs.close();
	}

	private final class MgmtClientCallbackImpl implements MgmtClientCallback {

		@Override
		public void processEvent(Event event) throws RemoteException {
			boolean newEvent = events.add(event);

			if (auto && listener != null && newEvent) {
				listener.autoPrintEvent(print());
			}
		}

	}

}
