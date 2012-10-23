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
	 * @param listener the listener for notifications
	 * @param exHandler the handler for exceptions
	 */
	public void setNotificationListener(NotificationListener listener, UncaughtExceptionHandler exHandler);
	
	/**
	 * 
	 */

	/**
	 * Submit a command to the auction server
	 * 
	 * @param command
	 *            the full command to submit
	 * @return the reply from the server
	 * @throws IOException
	 */
	public String submitCommand(String command) throws IOException;

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
	 */
	public void setNetworkParameter(String server, Integer serverPort,
			Integer udpPort);

	/**
	 * Free all acquired resources
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException;

	/**
	 * Start recieving notifications from the server
	 */
	public void startNotification();

	/**
	 * Sets the user name
	 * 
	 * @param userName
	 */
	public void setUserName(String userName);

	/**
	 * Gets the user name if logged in
	 * 
	 * @return the user name if logged in; null otherwise
	 */
	public String getUserName();
}
