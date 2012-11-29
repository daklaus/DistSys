/**
 * 
 */
package at.ac.tuwien.dslab2.service.analyticsServer;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import at.ac.tuwien.dslab2.domain.AuctionEvent;
import at.ac.tuwien.dslab2.domain.BidEvent;
import at.ac.tuwien.dslab2.domain.Event;
import at.ac.tuwien.dslab2.domain.EventType;
import at.ac.tuwien.dslab2.domain.StatisticsEvent;
import at.ac.tuwien.dslab2.domain.Subscription;
import at.ac.tuwien.dslab2.domain.UserEvent;
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

	/*
	 * Statistics fields
	 */
	private long systemStarted;

	// Users:
	private final Object userLockObj;
	private final ConcurrentMap<String, Long> userLoginTimes;
	private double userSessionTimeMin;
	private double userSessionTimeMax;
	private double userSessionTimeAvg;
	private long userSessionEndedCounter;

	// Bids:
	private final Object bidLockObj;
	private double bidPriceMax;
	private long bidCounter;

	// Auctions:
	private final Object auctionLockObj;
	private final ConcurrentMap<Long, Long> auctionStartTimes;
	private long finishedAuctionCounter;
	private double finishedAuctionTimeAvg;
	private final Set<Long> successfulAuctions;
	private long successfulFinishedAuctionCounter;

	public AnalyticsServerImpl(String bindingName) throws IOException {
		if (bindingName == null)
			throw new IllegalArgumentException("bindingName is null");

		this.subscriptionIdCounter = new AtomicLong(1);
		this.subscriptions = new ConcurrentSkipListMap<Long, Subscription>();

		// Statistics:
		this.systemStarted = 0;
		// User:
		this.userLockObj = new Object();
		this.userLoginTimes = new ConcurrentSkipListMap<String, Long>();
		this.userSessionTimeMin = Long.MAX_VALUE;
		this.userSessionTimeMax = 0;
		this.userSessionTimeAvg = 0;
		this.userSessionEndedCounter = 0;
		// Bids:
		this.bidLockObj = new Object();
		this.bidPriceMax = 0;
		this.bidCounter = 0;
		// Auctions:
		this.auctionLockObj = new Object();
		this.auctionStartTimes = new ConcurrentSkipListMap<Long, Long>();
		this.finishedAuctionCounter = 0;
		this.finishedAuctionTimeAvg = 0;
		this.successfulAuctions = new ConcurrentSkipListSet<Long>();
		this.successfulFinishedAuctionCounter = 0;

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
		if (event == null)
			throw new IllegalArgumentException("event is null");

		Set<Event> events = new LinkedHashSet<Event>();
		events.add(event);

		/*
		 * Calculate statistics
		 */
		if (event.getClass() == UserEvent.class) {
			UserEvent ue = (UserEvent) event;

			switch (ue.getType()) {
			case USER_LOGIN:
				this.userLoginTimes.put(ue.getUserName(), ue.getTimestamp());
				break;
			case USER_DISCONNECT:
			case USER_LOGOUT:
				synchronized (userLockObj) {
					Long loginTime = this.userLoginTimes.remove(ue
							.getUserName());
					if (loginTime == null)
						throw new IllegalStateException(
								"User logged out event before user logged in event (user "
										+ ue.getUserName() + ")");
					double uTime = (ue.getTimestamp() - loginTime) / 1000.0;

					if (uTime < this.userSessionTimeMin) {
						this.userSessionTimeMin = uTime;
						events.add(new StatisticsEvent(
								EventType.USER_SESSIONTIME_MIN,
								this.userSessionTimeMin));
					}
					if (uTime > this.userSessionTimeMax) {
						this.userSessionTimeMax = uTime;
						events.add(new StatisticsEvent(
								EventType.USER_SESSIONTIME_MAX, uTime));
					}

					this.userSessionTimeAvg = (this.userSessionTimeAvg
							* this.userSessionEndedCounter + uTime)
							/ (this.userSessionEndedCounter + 1);
					this.userSessionEndedCounter++;
					events.add(new StatisticsEvent(
							EventType.USER_SESSIONTIME_AVG,
							this.userSessionTimeAvg));
				}
				break;
			}

		} else if (event.getClass() == BidEvent.class) {
			BidEvent be = (BidEvent) event;

			switch (event.getType()) {
			case BID_PLACED:
				this.successfulAuctions.add(be.getAuctionId());

				synchronized (bidLockObj) {
					if (be.getPrice() > this.bidPriceMax) {
						this.bidPriceMax = be.getPrice();
						events.add(new StatisticsEvent(EventType.BID_PRICE_MAX,
								this.bidPriceMax));
					}
					this.bidCounter++;
					double bidCountPerMin = this.bidCounter
							/ ((System.currentTimeMillis() - this.systemStarted) / 60000.0);
					events.add(new StatisticsEvent(
							EventType.BID_COUNT_PER_MINUTE, bidCountPerMin));
				}
				break;
			}
		} else if (event.getClass() == AuctionEvent.class) {
			AuctionEvent ae = (AuctionEvent) event;

			switch (ae.getType()) {
			case AUCTION_STARTED:
				if (this.systemStarted <= 0)
					this.systemStarted = System.currentTimeMillis();
				this.auctionStartTimes
						.put(ae.getAuctionId(), ae.getTimestamp());
				break;
			case AUCTION_ENDED:
				synchronized (auctionLockObj) {

					Long startTime = this.auctionStartTimes.remove(ae
							.getAuctionId());
					if (startTime == null)
						throw new IllegalStateException(
								"Auction ended event before auction started event (auction "
										+ ae.getAuctionId() + ")");
					double aTime = (ae.getTimestamp() - startTime) / 1000.0;

					this.finishedAuctionTimeAvg = (this.finishedAuctionTimeAvg
							* this.finishedAuctionCounter + aTime)
							/ (this.finishedAuctionCounter + 1);
					this.finishedAuctionCounter++;
					events.add(new StatisticsEvent(EventType.AUCTION_TIME_AVG,
							this.finishedAuctionTimeAvg));

					boolean successful = this.successfulAuctions.remove(ae
							.getAuctionId());
					if (successful)
						this.successfulFinishedAuctionCounter++;

					double auctionSuccessRatio = this.successfulFinishedAuctionCounter
							/ this.finishedAuctionCounter;
					events.add(new StatisticsEvent(
							EventType.AUCTION_SUCCESS_RATIO,
							auctionSuccessRatio));
				}
			}
		}

		/*
		 * Send events
		 */
		for (Event e : events) {
			sendEvent(e);
		}
	}

	private void sendEvent(Event event) {
		for (Subscription s : this.subscriptions.values()) {
			MgmtClientCallback cb = s.getCb();

			// Only proceed if the subscription has a callback set
			if (cb == null)
				continue;

			// Only proceed if event matches the subscription regex
			if (!s.getRegex().matcher(event.getType().toString()).matches())
				continue;

			try {
				cb.processEvent(event);
			} catch (RemoteException e) {
				// If there is a problem with the connection to the
				// subscribed client, just ignore it
			}
		}
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
