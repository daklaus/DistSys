package at.ac.tuwien.dslab2.service.auctionServer;

import java.io.IOException;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import at.ac.tuwien.dslab2.domain.Auction;
import at.ac.tuwien.dslab2.domain.Bid;
import at.ac.tuwien.dslab2.domain.Client;
import at.ac.tuwien.dslab2.domain.User;

public class AuctionServiceImpl implements AuctionService {
	private final ConcurrentMap<String, User> users;
	private final SortedMap<Long, Auction> auctions;
	private final Timer timer;
	private volatile long idCounter;

	// Private constructor prevents instantiation from other classes
	private AuctionServiceImpl() {
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
	}

	private static class AuctionServiceHolder {
		public static final AuctionService INSTANCE = new AuctionServiceImpl();
	}

	public static AuctionService getInstance() {
		return AuctionServiceHolder.INSTANCE;
	}

	@Override
	public Auction create(User owner, String description, int duration) {

		Auction a = new Auction(idCounter++, description, owner, duration);
		auctions.put(a.getId(), a);

		// Start timer for auction
		synchronized (timer) {
			timer.schedule(new AuctionEndTask(a), a.getEndDate());
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

		// Notify overbid
		if (prevHighestBidder != null && !prevHighestBidder.equals(user)
				&& !prevHighestBidder.equals(a.getHighestBidder())) {
			prevHighestBidder.addNotification("!new-bid " + a.getDescription());
		}

		return a;
	}

	@Override
	public User login(String userName, Client client) {
		return users.putIfAbsent(userName, new User(userName, client));
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
	public void close() throws IOException {
		this.timer.cancel();
	}

	private class AuctionEndTask extends TimerTask {
		Auction a;

		public AuctionEndTask(Auction a) {
			super();
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
		}
	}

}
