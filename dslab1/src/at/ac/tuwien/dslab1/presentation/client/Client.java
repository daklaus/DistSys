/**
 * 
 */
package at.ac.tuwien.dslab1.presentation.client;

import java.io.IOException;
import java.util.Scanner;

import at.ac.tuwien.dslab1.service.client.AuctionClientService;
import at.ac.tuwien.dslab1.service.client.ServiceFactory;

/**
 * @author klaus
 * 
 */
public class Client {
	private static AuctionClientService acs;
	private static Integer udpPort;

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
		udpPort = sc.nextInt();

		acs = ServiceFactory.getAuctionClientService();
		acs.setNotificationListener(new NotificationListenerImpl(),
				new NotificationExHandlerImpl());
		acs.setReplyListener(new ReplyListenerImpl(), new ReplyExHandlerImpl());
		try {
			acs.connect(host, tcpPort, udpPort);
		} catch (IOException e) {
			System.err.println("Error while connecting:");
			e.printStackTrace();

			close();
			System.exit(0);
		}

	}

	private static void usage() {
		System.err.println("usage: java Client server tcpPort udpPort\n");
		System.err.println("\thost: host name or IP of the auction server\n"
				+ "\ttcpPort: TCP connection port on which the auction "
				+ "server is listening for incoming connections\n"
				+ "\tudpPort: this port will be used for handling UDP "
				+ "notifications from the auction server");

		close();
		System.exit(0);
	}

	private enum ParseResult {
		End, Login
	}

	private static void readInput() {
		Scanner sc = new Scanner(System.in);
		String cmd;
		Boolean end;

		System.out.print(getPrompt());
		end = false;
		while (!end && sc.hasNextLine()) {

			cmd = sc.nextLine();
			ParseResult r = parseCommand(cmd);
			if (r != null) {
				switch (r) {
				case End:
					end = true;
					break;
				case Login:
					cmd = cmd + " " + udpPort;
					break;
				}
			}

			if (!end) {
				try {
					if (!cmd.trim().isEmpty()) {
						acs.submitCommand(cmd);
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

	synchronized static void close() {
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
	private static ParseResult parseCommand(String command) {
		if (command == null)
			throw new IllegalArgumentException("The command is null");
		if (acs == null)
			throw new IllegalStateException("The AuctionClientService is null");

		// Commands:
		// !login <username>
		// !logout
		// !list
		// !create <duration> <description>
		// !bid <auction-id> <amount>
		// !end

		String cmdRegex = "![a-zA-Z-]+";
		String tmp;

		Scanner sc = new Scanner(command);
		sc.useDelimiter("\\s+");
		sc.skip("\\s*");

		if (!sc.hasNext(cmdRegex))
			return null;

		tmp = sc.next(cmdRegex);
		if (tmp.equalsIgnoreCase("!login")) {
			if (!sc.hasNext())
				return null;

			acs.setUserName(sc.next());

			return ParseResult.Login;
		} else if (tmp.equalsIgnoreCase("!logout")) {

			acs.setUserName(null);
		} else if (tmp.equalsIgnoreCase("!end")) {
			return ParseResult.End;
		}

		return null;
	}

}
