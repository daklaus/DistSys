package at.ac.tuwien.dslab3.service.loadTest;

import at.ac.tuwien.dslab3.presentation.PasswordFinderImpl;
import at.ac.tuwien.dslab3.presentation.auctionServer.ServerExceptionHandlerImpl;
import at.ac.tuwien.dslab3.service.PropertiesService;
import at.ac.tuwien.dslab3.service.PropertiesServiceFactory;
import at.ac.tuwien.dslab3.service.analyticsServer.AnalyticsServer;
import at.ac.tuwien.dslab3.service.analyticsServer.AnalyticsServerFactory;
import at.ac.tuwien.dslab3.service.auctionServer.AuctionServerService;
import at.ac.tuwien.dslab3.service.auctionServer.AuctionServerServiceFactory;
import at.ac.tuwien.dslab3.service.biddingClient.BiddingClientService;
import at.ac.tuwien.dslab3.service.biddingClient.BiddingClientServiceFactory;
import at.ac.tuwien.dslab3.service.billingServer.BillingServer;
import at.ac.tuwien.dslab3.service.billingServer.BillingServerFactory;
import at.ac.tuwien.dslab3.service.managementClient.*;
import org.bouncycastle.openssl.PasswordFinder;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

class LoadTestServiceImpl implements LoadTestService {

	private final int auctionServerTcpPort;
	private final String billingServerBindingName;
	private final String analyticsServerBindingName;
	private final String auctionServerHostName;
	private int clientCount;
	private int auctionDuration;
	private double auctionsPerMin;
	private double bidsPerMin;
	private double updateInterval;
	private ManagementClientService managementClientService;
	private AuctionServerService auctionServerService;
	private BillingServer billingServer;
	private List<BiddingClientService> biddingClientServices;
	private AnalyticsServer analyticsServer;
	private final BlockingQueue<String> auctionListQueue;
	private final BlockingQueue<String> auctionCreateQueue;
	private final BlockingQueue<String> auctionBiddingQueue;
	private final LinkedList<TimerTask> openTimerTasks;
	private final SubscriptionListener subscriptionListener;
	private final TimerNotifications timerNotifications;
	private final Random random;
	private final LinkedList<Timer> timers;

	public LoadTestServiceImpl(int auctionServerTcpPort,
			String billingServerBindingName, String analyticsServerBindingName,
			String auctionServerHostName,
			SubscriptionListener subscriptionListener,
			TimerNotifications timerNotifications) throws IOException {
		this.auctionServerTcpPort = auctionServerTcpPort;
		this.billingServerBindingName = billingServerBindingName;
		this.analyticsServerBindingName = analyticsServerBindingName;
		this.auctionServerHostName = auctionServerHostName;
		this.subscriptionListener = subscriptionListener;
		this.timerNotifications = timerNotifications;
		this.timers = new LinkedList<Timer>();
		this.auctionListQueue = new SynchronousQueue<String>();
		this.auctionCreateQueue = new SynchronousQueue<String>();
		this.auctionBiddingQueue = new SynchronousQueue<String>();
		this.biddingClientServices = new LinkedList<BiddingClientService>();
		this.openTimerTasks = new LinkedList<TimerTask>();

		this.random = new Random();
		initialize();
	}

	private void initialize() throws IOException {
		Properties loadTestProperties = PropertiesServiceFactory
				.getPropertiesService().getLoadTestProperties();

		Scanner scanner = new Scanner(
				loadTestProperties
						.getProperty(PropertiesService.LOADTEST_PROPERTIES_AUCTIONDURATION_KEY));
		if (!scanner.hasNextInt()) {
			throw new IOException(
					"Couldn't parse the registry Properties value of "
							+ PropertiesService.LOADTEST_PROPERTIES_AUCTIONDURATION_KEY);
		}
		auctionDuration = scanner.nextInt();
		scanner = new Scanner(
				loadTestProperties
						.getProperty(PropertiesService.LOADTEST_PROPERTIES_AUCTIONSPERMIN_KEY));
		if (!scanner.hasNextDouble()) {
			throw new IOException(
					"Couldn't parse the registry Properties value of "
							+ PropertiesService.LOADTEST_PROPERTIES_AUCTIONSPERMIN_KEY);
		}
		auctionsPerMin = scanner.nextDouble();
		scanner = new Scanner(
				loadTestProperties
						.getProperty(PropertiesService.LOADTEST_PROPERTIES_BIDSPERMIN_KEY));
		if (!scanner.hasNextDouble()) {
			throw new IOException(
					"Couldn't parse the registry Properties value of "
							+ PropertiesService.LOADTEST_PROPERTIES_BIDSPERMIN_KEY);
		}
		bidsPerMin = scanner.nextDouble();
		scanner = new Scanner(
				loadTestProperties
						.getProperty(PropertiesService.LOADTEST_PROPERTIES_UPDATEINTERVALSEC_KEY));
		if (!scanner.hasNextDouble()) {
			throw new IOException(
					"Couldn't parse the registry Properties value of "
							+ PropertiesService.LOADTEST_PROPERTIES_UPDATEINTERVALSEC_KEY);
		}
		updateInterval = scanner.nextDouble();
		scanner = new Scanner(
				loadTestProperties
						.getProperty(PropertiesService.LOADTEST_PROPERTIES_CLIENTS_KEY));
		if (!scanner.hasNextInt()) {
			throw new IOException(
					"Couldn't parse the registry Properties value of "
							+ PropertiesService.LOADTEST_PROPERTIES_CLIENTS_KEY);
		}
		this.clientCount = scanner.nextInt();
		scanner.close();
	}

	@Override
	public void start() {
		try {
			startBillingServer();
			startAnalyticsServer();
			startAuctionServer();
			startManagementClient();
			startBiddingClients();
			startTimerTasks();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void startAuctionServer() throws IOException {
		this.auctionServerService = AuctionServerServiceFactory
				.getAuctionServerService();
		this.auctionServerService
				.setExceptionHandler(new ServerExceptionHandlerImpl());
		//TODO:
		this.auctionServerService.start(auctionServerTcpPort,
				analyticsServerBindingName, billingServerBindingName, "keys/", "keys/auction-server.pem", new PasswordFinderImpl());

	}

	private void startBillingServer() throws IOException {
		billingServer = BillingServerFactory
				.newBillingServer(billingServerBindingName);
	}

	private void startBiddingClients() throws IOException {
		for (int i = 0; i < this.clientCount; i++) {
            // TODO: Remove static definition of server's public key file
            // TODO: Define appropriate PasswordFinder!
            // location and client keys directory
            BiddingClientService biddingClientService = BiddingClientServiceFactory
                    .newBiddingClientService(auctionServerHostName,
                            auctionServerTcpPort, 1,
                            "keys/auction-server.pub.pem", "keys/", new PasswordFinder() {
                        @Override
                        public char[] getPassword() {
                            return "23456".toCharArray();
                        }
                    });
            biddingClientService.setReplyListener(new LoadTestReplyListener(
                    auctionListQueue, auctionBiddingQueue, auctionCreateQueue),
                    null);
            biddingClientService.connect();
            biddingClientService.submitCommand("!login user" + i + " 1");
            this.biddingClientServices.add(biddingClientService);
        }
    }

	private void startTimerTasks() throws IOException {
		for (BiddingClientService biddingClientService : this.biddingClientServices) {
			AuctionBiddingHandler auctionBiddingHandler = new AuctionBiddingHandler(
					biddingClientService, auctionListQueue,
					auctionBiddingQueue, this.timerNotifications, this.random);
			AuctionCreationHandler auctionCreationHandler = new AuctionCreationHandler(
					biddingClientService, auctionCreateQueue, auctionDuration,
					this.timerNotifications);
			AuctionListingHandler auctionListingHandler = new AuctionListingHandler(
					biddingClientService, auctionListQueue,
					this.timerNotifications);

			this.openTimerTasks.add(auctionBiddingHandler);
			this.openTimerTasks.add(auctionCreationHandler);
			this.openTimerTasks.add(auctionListingHandler);

			Timer biddingTimer = new Timer("BiddingTimer");
			this.timers.add(biddingTimer);
			Timer auctionCreationTimer = new Timer("AuctionCreationTimer");
			this.timers.add(auctionCreationTimer);
			Timer auctionListingTimer = new Timer("AuctionListingTimer");
			this.timers.add(auctionListingTimer);

			long delay = (long) ((60.0 / this.bidsPerMin) * 1000);
			biddingTimer.schedule(auctionBiddingHandler, delay, delay);
			delay = (long) ((60.0 / this.auctionsPerMin) * 1000);
			auctionCreationTimer.schedule(auctionCreationHandler, delay, delay);
			delay = (long) (this.updateInterval * 1000.0);
			auctionListingTimer.schedule(auctionListingHandler, delay, delay);

		}
	}

	private void startManagementClient() throws IOException {
		managementClientService = ManagementClientServiceFactory
				.newManagementClientService(analyticsServerBindingName,
						billingServerBindingName);
		managementClientService
				.setSubscriptionListener(this.subscriptionListener);
		managementClientService.auto();
		managementClientService.subscribe(".*");
		try {
			managementClientService.login("stefan", "stefan");

			/*
			 * 0 100 3.0 7.0% 100 200 5.0 6.5% 200 500 7.0 6.0% 500 1000 10.0
			 * 5.5% 1000 INFINITY 15.0 5.0%
			 */
			managementClientService.addStep(0, 100, 3.0, 7.0);
			managementClientService.addStep(100, 200, 5.0, 6.5);
			managementClientService.addStep(200, 500, 7.0, 6.0);
			managementClientService.addStep(500, 1000, 10.0, 5.5);
			managementClientService.addStep(1000, Double.POSITIVE_INFINITY,
					15.0, 5.0);
			managementClientService.logout();

		} catch (AlreadyLoggedInException e) {
			throw new RuntimeException(e);
		} catch (LoggedOutException e) {
			throw new RuntimeException(e);
		}
	}

	private void startAnalyticsServer() throws IOException {
		this.analyticsServer = AnalyticsServerFactory
				.newAnalyticsServer(analyticsServerBindingName);
	}

	@Override
	public void close() throws IOException {
		for (TimerTask task : this.openTimerTasks) {
			task.cancel();
		}
		for (Timer timer : this.timers) {
			timer.cancel();
		}
		if (this.managementClientService != null) {
			this.managementClientService.close();
		}
		for (Closeable closeable : this.biddingClientServices) {
			closeable.close();
		}
		if (this.auctionServerService != null) {
			this.auctionServerService.close();
		}
		this.billingServer.close();
		this.analyticsServer.close();

	}
}
