/**
 * 
 */
package at.ac.tuwien.dslab1.presentation.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import at.ac.tuwien.dslab1.service.client.AuctionClientService;
import at.ac.tuwien.dslab1.service.client.ServiceFactory;

/**
 * @author klaus
 * 
 */
public class Client {
	private static AuctionClientService acs;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		initialize(args);
		readInput();
		close();
	}

	private static void initialize(String[] args) {
		Scanner sc;

		if (args.length != 3)
			usage();

		String host = args[0];

		sc = new Scanner(args[1]);
		if (!sc.hasNextInt())
			usage();
		Integer tcpPort = sc.nextInt();

		sc = new Scanner(args[2]);
		if (!sc.hasNextInt())
			usage();
		Integer udpPort = sc.nextInt();

		acs = ServiceFactory.getAuctionClientService();
		acs.setNotificationListener(new NotificationListenerImpl(),
				new NotificationExHandlerImpl());
		acs.setNetworkParameter(host, tcpPort, udpPort);
		acs.startNotification();
	}

	private static void usage() {
		System.err.println("usage: java Client tcpPort server udpPort\n");
		System.err.println("\thost: host name or IP of the auction server\n"
				+ "\ttcpPort: TCP connection port on which the auction "
				+ "server is listening for incoming connections\n"
				+ "\tudpPort: this port will be used for handling UDP "
				+ "notifications from the auction server).");

		close();
		System.exit(0);
	}

	private static void readInput() {
		Scanner sc = new Scanner(System.in);
		PrintWriter pw = new PrintWriter(System.out, true);
		String out = null;
		String cmd;
		Boolean end;

		System.out.print(getPrompt());
		end = false;
		while (!end && sc.hasNextLine()) {

			cmd = sc.nextLine();
			end = parseCommand(cmd);
			if (!end) {
				try {
					if (!cmd.trim().isEmpty()) {
						out = acs.submitCommand(cmd);

						pw.println(out);
					}

					System.out.print(getPrompt());
				} catch (IOException e) {
					System.err.println("Error while submitting command:");
					e.printStackTrace();
					end = true;
				}
			}
		}
	}

	private static void close() {
		if (acs != null) {
			try {
				acs.close();
			} catch (IOException e) {
				System.err.println("Something went wrong while closing:");
				e.printStackTrace();
			}
		}
	}

	static String getPrompt() {
		if (acs == null || acs.getUserName() == null)
			return "> ";
		return acs.getUserName() + "> ";
	}

	/**
	 * Parses the command to see if the client should do something.
	 * 
	 * @param command
	 * @return true if the client should end; false otherwise
	 */
	private static Boolean parseCommand(String command) {
		// Commands:
		// !login <username>
		// !logout
		// !list
		// !create <duration> <description>
		// !bid <auction-id> <amount>
		// !end

		String cmdRegex = "![a-zA-Z-]+";
		Boolean ret = false;
		String tmp;

		Scanner sc = new Scanner(command);
		sc.useDelimiter("\\s+");
		sc.skip("\\s*");

		if (!sc.hasNext(cmdRegex))
			return ret;

		tmp = sc.next(cmdRegex);
		if (tmp.equalsIgnoreCase("!login")) {
			if (!sc.hasNext())
				return ret;
			if (acs != null)
				acs.setUserName(sc.next());
		} else if (tmp.equalsIgnoreCase("!logout")) {
			if (acs != null)
				acs.setUserName(null);
		} else if (tmp.equalsIgnoreCase("!end")) {
			ret = true;
		}

		return ret;
	}

}
