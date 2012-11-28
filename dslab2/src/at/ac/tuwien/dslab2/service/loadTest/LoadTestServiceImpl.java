package at.ac.tuwien.dslab2.service.loadTest;

import at.ac.tuwien.dslab2.domain.Event;
import at.ac.tuwien.dslab2.presentation.auctionServer.ServerExceptionHandlerImpl;
import at.ac.tuwien.dslab2.service.PropertiesService;
import at.ac.tuwien.dslab2.service.PropertiesServiceFactory;
import at.ac.tuwien.dslab2.service.analyticsServer.AnalyticsServer;
import at.ac.tuwien.dslab2.service.analyticsServer.AnalyticsServerFactory;
import at.ac.tuwien.dslab2.service.auctionServer.AuctionServerService;
import at.ac.tuwien.dslab2.service.auctionServer.AuctionServerServiceFactory;
import at.ac.tuwien.dslab2.service.biddingClient.BiddingClientService;
import at.ac.tuwien.dslab2.service.biddingClient.BiddingClientServiceFactory;
import at.ac.tuwien.dslab2.service.billingServer.BillingServer;
import at.ac.tuwien.dslab2.service.billingServer.BillingServerFactory;
import at.ac.tuwien.dslab2.service.managementClient.*;
import at.ac.tuwien.dslab2.service.rmi.RMIServerService;
import at.ac.tuwien.dslab2.service.rmi.RMIServiceFactory;

import java.io.Closeable;
import java.io.IOException;
import java.rmi.RemoteException;
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
    private int auctionsPerMin;
    private int bidsPerMin;
    private int updateInterval;
    private ManagementClientService managementClientService;
    private AuctionServerService auctionServerService;
    private BillingServer billingServer;
    private final Timer biddingTimer;
    private final Timer auctionCreationTimer;
    private final BlockingQueue<String> queue;
    private List<BiddingClientService> biddingClientServices;
    private final Timer auctionListingTimer;
    private AnalyticsServer analyticsServer;
    private RMIServerService analyticsServerRemoteRef;

    public LoadTestServiceImpl(int auctionServerTcpPort, String billingServerBindingName, String analyticsServerBindingName, String auctionServerHostName) throws IOException {
        this.auctionServerTcpPort = auctionServerTcpPort;
        this.billingServerBindingName = billingServerBindingName;
        this.analyticsServerBindingName = analyticsServerBindingName;
        this.auctionServerHostName = auctionServerHostName;
        this.biddingTimer = new Timer("BiddingTimer");
        this.auctionCreationTimer = new Timer("AuctionCreationTimer");
        this.auctionListingTimer = new Timer("AuctionListingTimer");
        this.queue = new SynchronousQueue<String>();
        this.biddingClientServices = new LinkedList<BiddingClientService>();

        initialize();
    }

    private void initialize() throws IOException {
        Properties loadTestProperties = PropertiesServiceFactory.getPropertiesService().getLoadTestProperties();


        Scanner scanner = new Scanner(loadTestProperties.getProperty(PropertiesService.LOADTEST_PROPERTIES_AUCTIONDURATION_KEY));
        if (!scanner.hasNextInt()) {
            throw new IOException("Couldn't parse the registry Properties value of "
                    + PropertiesService.LOADTEST_PROPERTIES_AUCTIONDURATION_KEY);
        }
        auctionDuration = scanner.nextInt();
        scanner = new Scanner(loadTestProperties.getProperty(PropertiesService.LOADTEST_PROPERTIES_AUCTIONSPERMIN_KEY));
        if (!scanner.hasNextInt()) {
            throw new IOException("Couldn't parse the registry Properties value of "
                    + PropertiesService.LOADTEST_PROPERTIES_AUCTIONSPERMIN_KEY);
        }
        auctionsPerMin = scanner.nextInt();
        scanner = new Scanner(loadTestProperties.getProperty(PropertiesService.LOADTEST_PROPERTIES_BIDSPERMIN_KEY));
        if (!scanner.hasNextInt()) {
            throw new IOException("Couldn't parse the registry Properties value of "
                    + PropertiesService.LOADTEST_PROPERTIES_BIDSPERMIN_KEY);
        }
        bidsPerMin = scanner.nextInt();
        scanner = new Scanner(loadTestProperties.getProperty(PropertiesService.LOADTEST_PROPERTIES_UPDATEINTERVALSEC_KEY));
        if (!scanner.hasNextInt()) {
            throw new IOException("Couldn't parse the registry Properties value of "
                    + PropertiesService.LOADTEST_PROPERTIES_UPDATEINTERVALSEC_KEY);
        }
        updateInterval = scanner.nextInt();
        scanner = new Scanner(loadTestProperties.getProperty(PropertiesService.LOADTEST_PROPERTIES_CLIENTS_KEY));
        if (!scanner.hasNextInt()) {
            throw new IOException("Couldn't parse the registry Properties value of "
                    + PropertiesService.LOADTEST_PROPERTIES_CLIENTS_KEY);
        }
        this.clientCount = scanner.nextInt();
        scanner.close();
    }

    private RMIServerService createRegistry() throws RemoteException {
        /*
        * Read the properties file
        */
        Properties prop = null;
        try {
            prop = PropertiesServiceFactory.getPropertiesService()
                    .getRegistryProperties();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Parse value
        Scanner scanner = new Scanner(
                prop.getProperty(PropertiesService.REGISTRY_PROPERTIES_PORT_KEY));
        if (!scanner.hasNextInt()) {
            throw new RuntimeException("Couldn't parse the properties value of "
                    + PropertiesService.REGISTRY_PROPERTIES_PORT_KEY);
        }
        int port = scanner.nextInt();

        return RMIServiceFactory.newRMIServerService(port);
    }

    @Override
    public void start() {
        try {
            startBillingServer();
            startAnalyticsServer();
            startAuctionServer();
            startManagementClient();
            startBiddingClients();
            startHandlers();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void startAuctionServer() throws IOException {
        this.auctionServerService = AuctionServerServiceFactory.getAuctionServerService();
        this.auctionServerService.setExceptionHandler(new ServerExceptionHandlerImpl());
        this.auctionServerService.start(auctionServerTcpPort, billingServerBindingName, analyticsServerBindingName);

    }

    private void startBillingServer() throws IOException {
        billingServer = BillingServerFactory.newBillingServer(billingServerBindingName);
    }

    private void startBiddingClients() throws IOException {
        for (int i = 0; i < this.clientCount; i++) {
            BiddingClientService biddingClientService = BiddingClientServiceFactory.newBiddingClientService();
            biddingClientService.setReplyListener(new LoadTestReplyListener(queue), null);
            biddingClientService.connect(auctionServerHostName, auctionServerTcpPort, 1);
            biddingClientService.submitCommand("!login user" + i + " 1");
            this.biddingClientServices.add(biddingClientService);
        }
    }

    private void startHandlers() throws IOException {
        for (BiddingClientService biddingClientService : this.biddingClientServices) {
            AuctionBiddingHandler auctionBiddingHandler = new AuctionBiddingHandler(biddingClientService, queue);
            AuctionCreationHandler auctionCreationHandler = new AuctionCreationHandler(biddingClientService, queue, auctionDuration);
            AuctionListingHandler auctionListingHandler = new AuctionListingHandler(biddingClientService, queue);

            long delay = (60 / this.bidsPerMin) * 1000;
            this.biddingTimer.schedule(auctionBiddingHandler, delay, delay);
            delay = (60 / this.auctionsPerMin) * 1000;
            this.auctionCreationTimer.schedule(auctionCreationHandler, delay, delay);
            delay = this.updateInterval * 1000;
            this.auctionListingTimer.schedule(auctionListingHandler, delay, delay);
        }
    }

    private void startManagementClient() throws IOException {
        managementClientService = ManagementClientServiceFactory.newManagementClientService(analyticsServerBindingName, billingServerBindingName);
        managementClientService.setSubscriptionListener(new SubscriptionListener() {
            @Override
            public void autoPrintEvent(Set<Event> events) {
                System.out.println(events.toString());
            }
        });
        managementClientService.auto();
        managementClientService.subscribe(".*");

        try {
            managementClientService.login("stefan", "stefan");

            /*
            0	100	3.0	7.0%
           100	200	5.0	6.5%
           200	500	7.0	6.0%
           500	1000	10.0	5.5%
           1000	INFINITY	15.0	5.0%
            */
            managementClientService.addStep(0, 100, 3.0, 7.0);
            managementClientService.addStep(100, 200, 5.0, 6.5);
            managementClientService.addStep(200, 500, 7.0, 6.0);
            managementClientService.addStep(500, 1000, 10.0, 5.5);
            managementClientService.addStep(1000, Double.POSITIVE_INFINITY, 15.0, 5.0);
            managementClientService.logout();

        } catch (AlreadyLoggedInException e) {
            throw new RuntimeException(e);
        } catch (LoggedOutException e) {
            throw new RuntimeException(e);
        }
    }

    private void startAnalyticsServer() throws IOException {
        this.analyticsServer = AnalyticsServerFactory.newAnalyticsServer(analyticsServerBindingName);
        this.analyticsServerRemoteRef = createRegistry();
        this.analyticsServerRemoteRef.bind(analyticsServerBindingName, analyticsServer);
    }

    @Override
    public void close() throws IOException {
        this.auctionCreationTimer.cancel();
        this.biddingTimer.cancel();
        this.auctionListingTimer.cancel();
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
        this.analyticsServerRemoteRef.close();
        this.analyticsServer.close();

    }
}
