/**
 * 
 */
package at.ac.tuwien.dslab2.presentation.auctionServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import at.ac.tuwien.dslab2.service.auctionServer.AuctionServerService;
import at.ac.tuwien.dslab2.service.auctionServer.AuctionServerServiceFactory;

/**
 * @author klaus
 * 
 */
public class AuctionServer {
	private static AuctionServerService ass;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			try {
				initialize(args);
				readInput();
			} finally {
				close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void initialize(String[] args) {
		Scanner sc;

		if (args.length != 3)
			usage();

		sc = new Scanner(args[0]);
		if (!sc.hasNextInt())
			usage();
		int tcpPort = sc.nextInt();
		String analyticsServerRef = args[1];
		String billingServerRef = args[2];

		ass = AuctionServerServiceFactory.getAuctionServerService();
		ass.setExceptionHandler(new ServerExceptionHandlerImpl());

		try {
			ass.start(tcpPort, analyticsServerRef, billingServerRef);
		} catch (IOException e) {
			error("Error while connecting:", e);
		}

	}

	private static void usage() {
		System.err
				.println("usage: java AuctionServer tcpPort analyticsServerName "
						+ "billingServerName\n\n"
						+ "\thost: host name or IP of the auction server\n"
						+ "\ttcpPort: TCP connection port on which the "
						+ "auction server will receive incoming messages "
						+ "(commands) from clients.\n"
						+ "\tanalyticsServerName: the binding name of the "
						+ "analytics server in the RMI registry"
						+ "\tbillingServerName: the binding name of the "
						+ "billing server in the RMI registry\n");

		close();
		System.exit(0);
	}

	private static void readInput() {
		System.out.println("--- press Enter to exit ---");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		try {
			br.readLine();
		} catch (IOException e) {
			System.err.println("Something went wrong while reading input:");
			e.printStackTrace();
		}
	}

	synchronized static void close() {
		if (ass != null) {
			try {
				ass.close();
			} catch (Exception e) {
				System.err.println("Something went wrong while closing:");
				e.printStackTrace();
			}
		}
	}

	private static void error(String msg, Throwable e) {
		System.err.println(msg);
		if (e != null)
			e.printStackTrace();
		close();
		System.exit(1);
	}
}
