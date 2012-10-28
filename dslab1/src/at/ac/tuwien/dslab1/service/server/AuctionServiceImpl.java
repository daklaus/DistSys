package at.ac.tuwien.dslab1.service.server;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import at.ac.tuwien.dslab1.domain.Auction;
import at.ac.tuwien.dslab1.domain.Bid;
import at.ac.tuwien.dslab1.domain.Client;
import at.ac.tuwien.dslab1.domain.User;

public class AuctionServiceImpl implements AuctionService {
	private Map<String, User> users;
	private SortedMap<Integer, Auction> auctions;
	private volatile Integer idCounter;

	// Private constructor prevents instantiation from other classes
	private AuctionServiceImpl() {
		users = Collections.synchronizedMap(new LinkedHashMap<String, User>());
		auctions = Collections
				.synchronizedSortedMap(new TreeMap<Integer, Auction>());
		idCounter = 1;
	}

	private static class AuctionServiceHolder {
		public static final AuctionService INSTANCE = new AuctionServiceImpl();
	}

	public static AuctionService getInstance() {
		return AuctionServiceHolder.INSTANCE;
	}

	@Override
	public synchronized Auction create(User owner, String description,
			Integer duration) {
		// TODO Start timer for auction

		Auction a = new Auction(idCounter++, description, owner, duration);
		auctions.put(a.getId(), a);
		return a;
	}

	@Override
	public synchronized String list() {
		StringBuilder sb = new StringBuilder();
		for (Auction a : auctions.values()) {
			sb.append(a.toString());
			sb.append("\n");
		}

		return sb.toString();
	}

	@Override
	public synchronized Auction bid(User user, Integer auctionId, double amount) {
		// TODO Notify overbid

		Auction a = auctions.get(auctionId);

		if (a == null)
			return null;

		a.addBid(new Bid(amount, user));

		return a;
	}

	@Override
	public synchronized User login(String userName, Client client) {
		User u;
		if (users.containsKey(userName)) {
			u = users.get(userName);
		} else {
			u = new User(userName, client);
			users.put(userName, u);
		}

		return u;
	}

	@Override
	public synchronized void logout(User user) {
		user.setClient(null);
		user.setLoggedIn(false);
	}

}
