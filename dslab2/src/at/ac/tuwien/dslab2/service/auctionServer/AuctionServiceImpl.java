package at.ac.tuwien.dslab2.service.auctionServer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import at.ac.tuwien.dslab2.domain.Auction;
import at.ac.tuwien.dslab2.domain.AuctionEvent;
import at.ac.tuwien.dslab2.domain.Bid;
import at.ac.tuwien.dslab2.domain.BidEvent;
import at.ac.tuwien.dslab2.domain.Client;
import at.ac.tuwien.dslab2.domain.EventType;
import at.ac.tuwien.dslab2.domain.User;
import at.ac.tuwien.dslab2.service.PropertiesService;
import at.ac.tuwien.dslab2.service.PropertiesServiceFactory;
import at.ac.tuwien.dslab2.service.analyticsServer.AnalyticsServer;
import at.ac.tuwien.dslab2.service.billingServer.BillingServer;
import at.ac.tuwien.dslab2.service.billingServer.BillingServerSecure;
import at.ac.tuwien.dslab2.service.rmi.RMIClientService;
import at.ac.tuwien.dslab2.service.rmi.RMIServiceFactory;

public class AuctionServiceImpl implements AuctionService {
	private final ConcurrentMap<String, User> users;
	private final SortedMap<Long, Auction> auctions;
	private final Timer timer;
	private volatile long idCounter;
	private final RMIClientService rcs;
	private final BillingServerSecure bss;
	private final AnalyticsServer as;

	public AuctionServiceImpl(String analyticsServerRef, String billingServerRef)
			throws IOException {
		// users = Collections.synchronizedMap(new LinkedHashMap<String,
		// User>());
		// Better scalability
		this.users = new ConcurrentHashMap<String, User>();
		// auctions = Collections.synchronizedSortedMap(new TreeMap<Long,
		// Auction>());
		// Better scalability
		this.auctions = new ConcurrentSkipListMap<Long, Auction>();
		this.timer = new Timer("Timer thread");
		this.idCounter = 1;

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
		RMIClientService rcs = null;
		BillingServer bs = null;
		BillingServerSecure bss = null;
		AnalyticsServer as = null;
		try {
			rcs = RMIServiceFactory.newRMIClientService(host, port);
			bs = (BillingServer) rcs.lookup(billingServerRef);
			as = (AnalyticsServer) rcs.lookup(analyticsServerRef);

			// Log into the billing server
			if (bs != null)
				bss = bs.login(PropertiesService.BILLINGSERVER_USERNAME,
						PropertiesService.BILLINGSERVER_PASSWORD);
		} catch (Exception e) {
			// Don't propagate the exception, because we don't care for RMI
			// exceptions
			// Maybe add logging later
		}
		this.rcs = rcs;
		this.bss = bss;
		this.as = as;
	}

	@Override
	public Auction create(User owner, String description, int duration) {

		Auction a = new Auction(idCounter++, description, owner, duration);
		auctions.put(a.getId(), a);

		// Start timer for auction
		synchronized (timer) {
			timer.schedule(new AuctionEndTask(a), a.getEndDate());
		}

		// Report event
		if (as != null) {
			try {
				as.processEvent(new AuctionEvent(EventType.AUCTION_STARTED, a
						.getId()));
			} catch (Exception e) {
				// Don't propagate the exception, because we don't care for
				// RMI exceptions
				// Maybe add logging later
			}
		}

		return a;
	}

	@Override
	public String list() {
		if (auctions.isEmpty())
			return "There are currently no auctions running!";

		StringBuilder sb = new StringBuilder();
		// No more use for sync with ConcurrentSkipListMap
		// synchronized (auctions) {
		for (Auction a : auctions.values()) {
			sb.append(a.toString());
			sb.append("\n");
		}
		// }

		return sb.toString();
	}

	@Override
	public Auction bid(User user, long auctionId, double amount) {

		Auction a = auctions.get(auctionId);

		if (a == null)
			return null;

		User prevHighestBidder = a.getHighestBidder();

		a.addBid(new Bid(amount, user));

		// Report event
		if (as != null) {
			try {
				as.processEvent(new BidEvent(EventType.BID_PLACED, user
						.getName(), auctionId, amount));
			} catch (Exception e) {
				// Don't propagate the exception, because we don't care for
				// RMI exceptions
				// Maybe add logging later
			}
		}

		// Notify overbid
		if (prevHighestBidder != null && !prevHighestBidder.equals(user)
				&& !prevHighestBidder.equals(a.getHighestBidder())) {
			prevHighestBidder.addNotification("!new-bid " + a.getDescription());

			// Report event
			if (as != null) {
				try {
					as.processEvent(new BidEvent(EventType.BID_OVERBID,
							prevHighestBidder.getName(), auctionId, amount));
				} catch (Exception e) {
					// Don't propagate the exception, because we don't care for
					// RMI exceptions
					// Maybe add logging later
				}
			}
		}

		return a;
	}

	@Override
	public User login(String userName, Client client) {
		User u = new User(userName, client);

		User existingUser = users.putIfAbsent(userName, u);
		if (existingUser != null)
			u = existingUser;

		return u;
	}

	@Override
	public void logout(User user) {
		if (user == null)
			throw new IllegalArgumentException("user is null");

		synchronized (user) {
			user.setClient(null);
			user.setLoggedIn(false);
		}
	}

	@Override
	public AnalyticsServer getAnalysticsServerRef() {
		return as;
	}

	@Override
	public void close() throws IOException {
		this.timer.cancel();
		if (rcs != null)
			rcs.close();
	}

	private class AuctionEndTask extends TimerTask {
		private final Auction a;

		public AuctionEndTask(Auction a) {
			super();
			if (a == null)
				throw new IllegalArgumentException("Auction is null");

			this.a = a;
		}

		@Override
		public void run() {
			final User owner = a.getOwner();
			final User winner = a.getHighestBidder();
			final Bid bid = a.getHighestBid();

			final String notification = "!auction-ended "
					+ (winner != null ? winner.toString() : "none")
					+ " "
					+ (bid != null ? String.format("%.2f", bid.getAmount())
							: "0.00") + " " + a.getDescription();

			// Notify owner
			if (owner != null)
				owner.addNotification(notification);

			// Notify winner
			if (winner != null && !winner.equals(owner))
				winner.addNotification(notification);

			auctions.remove(a.getId());

			// Report event
			if (as != null) {
				try {
					as.processEvent(new AuctionEvent(EventType.AUCTION_ENDED, a
							.getId()));
					if (bid != null)
						as.processEvent(new BidEvent(EventType.BID_WON, bid
								.getUser().getName(), a.getId(), bid
								.getAmount()));
				} catch (Exception e) {
					// Don't propagate the exception, because we don't care for
					// RMI
					// exceptions
					// Maybe add logging later
				}
			}

			// Report event
			if (bss != null) {
				try {
					bss.billAuction(owner.getName(), a.getId(), bid == null ? 0
							: bid.getAmount());
				} catch (Exception e) {
					// Don't propagate the exception, because we don't care for
					// RMI
					// exceptions
					// Maybe add logging later
				}
			}
		}
	}

}
