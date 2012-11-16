/**
 * 
 */
package at.ac.tuwien.dslab2.presentation.auctionServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import at.ac.tuwien.dslab2.service.auctionServer.AuctionServerService;
import at.ac.tuwien.dslab2.service.auctionServer.ServiceFactory;

/**
 * @author klaus
 * 
 */
public class Server {
	private static AuctionServerService ass;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			initialize(args);
			readInput();
		} finally {
			close();
		}
	}

	private static void initialize(String[] args) {
		Scanner sc;

		if (args.length != 1)
			usage();

		sc = new Scanner(args[0]);
		if (!sc.hasNextInt())
			usage();
		int tcpPort = sc.nextInt();

		ass = ServiceFactory.getAuctionServerService();
		ass.setExceptionHandler(new ServerExceptionHandlerImpl());

		try {
			ass.start(tcpPort);
		} catch (IOException e) {
			System.err.println("Error while connecting:");
			e.printStackTrace();

			close();
			System.exit(1);
		}

	}

	private static void usage() {
		System.err.println("usage: java Server tcpPort\n");
		System.err.println("\thost: host name or IP of the auction server\n"
				+ "\ttcpPort: TCP connection port on which the "
				+ "auction server will receive incoming messages "
				+ "(commands) from clients.");

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
			} catch (IOException e) {
				System.err.println("Something went wrong while closing:");
				e.printStackTrace();
			}
		}
	}
}
