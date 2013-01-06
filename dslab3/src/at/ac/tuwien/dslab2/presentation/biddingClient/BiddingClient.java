/**
 * 
 */
package at.ac.tuwien.dslab2.presentation.biddingClient;

import java.io.IOException;
import java.util.Scanner;

import at.ac.tuwien.dslab2.service.biddingClient.BiddingClientService;
import at.ac.tuwien.dslab2.service.biddingClient.BiddingClientServiceFactory;

/**
 * @author klaus
 * 
 */
public class BiddingClient {
	private static BiddingClientService acs;
	private static int udpPort;

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

		if (args.length != 5)
			usage();

		String host = args[0];

		sc = new Scanner(args[1]);
		if (!sc.hasNextInt())
			usage();
		int tcpPort = sc.nextInt();

		sc = new Scanner(args[2]);
		if (!sc.hasNextInt())
			usage();
		udpPort = sc.nextInt();

        String serverPublicKeyFile = args[3];
        String clientsKeysDirectory = args[4];

		acs = BiddingClientServiceFactory.newBiddingClientService();
		acs.setNotificationListener(new NotificationListenerImpl(),
				new NotificationExHandlerImpl());
		acs.setReplyListener(new ReplyListenerImpl(), new ReplyExHandlerImpl());
		try {
			acs.connect(host, tcpPort, udpPort);
		} catch (IOException e) {
			System.err.println("Error while connecting:");
			e.printStackTrace();

			close();
			System.exit(1);
		}

	}

	private static void usage() {
		System.err
				.println("usage: java BiddingClient server tcpPort udpPort\n");
		System.err.println("\thost: host name or IP of the auction server\n"
				+ "\ttcpPort: TCP connection port on which the auction "
				+ "server is listening for incoming connections\n"
				+ "\tudpPort: this port will be used for handling UDP "
				+ "notifications from the auction server"
                + "\tlocation of public key file for the auction server\n"
                + "\tdirectory of private/public keys");

		close();
		System.exit(0);
	}

	private enum ParseResult {
		End, Login
	}

	private static void readInput() {
		Scanner sc = new Scanner(System.in);
		String cmd;
		boolean end;

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
				} catch (Exception e) {
					System.err.println("Error while submitting command:");
					e.printStackTrace();
				}
			}
		}
	}

	synchronized static void close() {
		if (acs != null) {
			try {
				acs.close();
			} catch (Exception e) {
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

	static String getUserName() {
		if (acs == null)
			return null;
		return acs.getUserName();
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
			throw new IllegalStateException("The BiddingClientService is null");

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
