/**
 * 
 */
package at.ac.tuwien.dslab2.presentation.biddingClient;

import at.ac.tuwien.dslab2.presentation.PasswordFinderImpl;
import at.ac.tuwien.dslab2.service.biddingClient.BiddingClientService;
import at.ac.tuwien.dslab2.service.biddingClient.BiddingClientServiceFactory;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author klaus
 * 
 */
public class BiddingClient {
	private static BiddingClientService bcs;

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
		int udpPort;
		int tcpPort;
		String serverPublicKeyFileLocation;
		String clientsKeysDirectory;
		Scanner sc;

		if (args.length != 5)
			usage();

		String host = args[0];

		sc = new Scanner(args[1]);
		if (!sc.hasNextInt())
			usage();
		tcpPort = sc.nextInt();

		sc = new Scanner(args[2]);
		if (!sc.hasNextInt())
			usage();
		udpPort = sc.nextInt();

		serverPublicKeyFileLocation = args[3];
		clientsKeysDirectory = args[4];

		bcs = BiddingClientServiceFactory.newBiddingClientService(host,
				tcpPort, udpPort, serverPublicKeyFileLocation,
				clientsKeysDirectory, new
                PasswordFinderImpl());
		bcs.setNotificationListener(new NotificationListenerImpl(),
				new NotificationExHandlerImpl());
		bcs.setReplyListener(new ReplyListenerImpl(), new ReplyExHandlerImpl());
		try {
			bcs.connect();
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

	private static void readInput() {
		Scanner sc = new Scanner(System.in);
		String cmd;
		boolean end;

		System.out.print(getPrompt());
		System.out.flush();
		end = false;
		while (!end && sc.hasNextLine()) {

			cmd = sc.nextLine().trim();

			end = cmd.matches("^!end.*");

			if (!end) {
				try {
					if (!cmd.isEmpty()) {
						bcs.submitCommand(cmd);
					}

					System.out.print(getPrompt());
					System.out.flush();
				} catch (Exception e) {
					System.err.println("Error while submitting command: "
							+ e.getMessage());
				}
			}
		}
	}

	synchronized static void close() {
		if (bcs != null) {
			try {
				bcs.close();
			} catch (Exception e) {
				System.err.println("Something went wrong while closing:");
				e.printStackTrace();
			}
		}
	}

	static String getPrompt() {
		if (bcs == null || bcs.getUserName() == null)
			return "> ";
		return bcs.getUserName() + "> ";
	}

	static String getUserName() {
		if (bcs == null)
			return null;
		return bcs.getUserName();
	}

}
