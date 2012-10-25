package at.ac.tuwien.dslab1.service.server;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.TreeMap;

import at.ac.tuwien.dslab1.domain.Auction;
import at.ac.tuwien.dslab1.domain.User;
import at.ac.tuwien.dslab1.service.server.AuctionServiceImpl.AuctionServerServiceHolder;

public class AuctionServerServiceImpl implements AuctionServerService {

	// Private constructor prevents instantiation from other classes
	private AuctionServerServiceImpl() {
	}

	private static class AuctionServerServiceHolder {
		public static final AuctionServerService INSTANCE = new AuctionServerServiceImpl();
	}

	public static AuctionServerService getInstance() {
		return AuctionServerServiceHolder.INSTANCE;
	}

	@Override
	public void start(Integer tcpPort) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setExceptionHandler(UncaughtExceptionHandler exHandler) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

}
