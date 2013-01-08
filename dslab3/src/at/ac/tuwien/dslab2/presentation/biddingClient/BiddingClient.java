/**
 * 
 */
package at.ac.tuwien.dslab2.presentation.biddingClient;

import at.ac.tuwien.dslab2.service.biddingClient.BiddingClientService;
import at.ac.tuwien.dslab2.service.biddingClient.BiddingClientServiceFactory;
import at.ac.tuwien.dslab2.service.security.HashMACService;
import at.ac.tuwien.dslab2.service.security.HashMACServiceFactory;
import org.bouncycastle.util.encoders.Base64;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Scanner;

/**
 * @author klaus
 * 
 */
public class BiddingClient {
	private static BiddingClientService acs;
	private static int udpPort;
	private static int tcpPort;
	private static String serverPublicKeyFileLocation;
	private static String clientsKeysDirectory;

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
		tcpPort = sc.nextInt();

		sc = new Scanner(args[2]);
		if (!sc.hasNextInt())
			usage();
		udpPort = sc.nextInt();

		serverPublicKeyFileLocation = args[3];
		clientsKeysDirectory = args[4];

		acs = BiddingClientServiceFactory.newBiddingClientService(host,
				tcpPort, udpPort);
		acs.setNotificationListener(new NotificationListenerImpl(),
				new NotificationExHandlerImpl());
		acs.setReplyListener(new ReplyListenerImpl(), new ReplyExHandlerImpl());
		try {
			acs.connect();
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
		End, Fail, Login
	}

	private static void readInput() {
		Scanner sc = new Scanner(System.in);
		String cmd;
		boolean end;

		System.out.print(getPrompt());
		System.out.flush();
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
					// Changed here LoginCommand for Lab3!
					// cmd = cmd + " " + udpPort;
					String clientChallenge = generateClientChallenge(Charset
							.forName("UTF-16"));
					cmd = cmd + " " + tcpPort + " " + clientChallenge;
					break;
				}
			}

			if (!end && r != ParseResult.Fail) {
				try {
					if (!cmd.trim().isEmpty()) {
						acs.submitCommand(cmd);
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

	/**
	 * This method generates a 32 byte secure random number and encodes it with
	 * Base64 encoding
	 * 
	 * @param charset
	 *            the charset which will be used for translating the encoded
	 *            byte array to a string
	 * @return the generated 32 byte secure random number Base64 encoded
	 */
	static String generateClientChallenge(Charset charset) {
		SecureRandom secureRandom = new SecureRandom();
		final byte[] number = new byte[32];
		secureRandom.nextBytes(number);
		byte[] encodedRandom = Base64.encode(number);
		return new String(encodedRandom, charset);
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
			if (!sc.hasNext()) {
				return null;
			}
			String username = sc.next();
			acs.setUserName(username);

			return initLoginServices(username);
		} else if (tmp.equalsIgnoreCase("!logout")) {
			acs.setUserName(null);
			acs.setHashMACService(null);
		} else if (tmp.equalsIgnoreCase("!end")) {
			return ParseResult.End;
		}

		return null;
	}

	private static ParseResult initLoginServices(String username) {
		try {
			HashMACService hashMACService = HashMACServiceFactory.getService(
					clientsKeysDirectory, username);
			acs.setHashMACService(hashMACService);
		} catch (IOException e) {
			System.out.println("Could not log in because keys for user "
					+ username + " not found in directory "
					+ clientsKeysDirectory);
			System.out.flush();
			return ParseResult.Fail;
		}
		return ParseResult.Login;
	}

}
