/**
 * 
 */
package at.ac.tuwien.dslab1.service.client;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * @author klaus
 * 
 */
public interface AuctionClientService {

	/**
	 * Sets the notification handlers of the service
	 * 
	 * @param listener
	 *            the listener for notifications
	 * @param exHandler
	 *            the handler for exceptions
	 */
	void setNotificationListener(NotificationListener listener,
			UncaughtExceptionHandler exHandler);

	/**
	 * Sets the reply handlers of the service
	 * 
	 * @param listener
	 *            the listener for notifications
	 * @param exHandler
	 *            the handler for exceptions
	 */
	void setReplyListener(ReplyListener listener,
			UncaughtExceptionHandler exHandler);

	/**
	 * Submit a command to the auction server
	 * 
	 * @param command
	 *            the full command to submit
	 * @throws IOException
	 */
	void submitCommand(String command) throws IOException;

	/**
	 * Sets the server, server port and own UDP port for the networking. This
	 * has to be done before any command can be submitted.
	 * 
	 * @param server
	 *            the host name or IP address of the auction server
	 * @param serverPort
	 *            the TCP port of the auction server
	 * @param udpPort
	 *            the UDP port on which to listen for notifications from the
	 *            server
	 * @throws IOException
	 */
	void connect(String server, int serverPort, int udpPort) throws IOException;

	/**
	 * Determines whether the service is connected or not
	 * 
	 * @return true if and only if the service is connected
	 */
	boolean isConnected();

	/**
	 * Free all acquired resources
	 * 
	 * @throws IOException
	 */
	void close() throws IOException;

	/**
	 * Sets the user name
	 * 
	 * @param userName
	 */
	void setUserName(String userName);

	/**
	 * Gets the user name if logged in
	 * 
	 * @return the user name if logged in; null otherwise
	 */
	String getUserName();
}
