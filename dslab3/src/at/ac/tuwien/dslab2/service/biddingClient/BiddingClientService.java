/**
 * 
 */
package at.ac.tuwien.dslab2.service.biddingClient;

import at.ac.tuwien.dslab2.service.security.HashMACService;

import java.io.Closeable;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * @author klaus
 * 
 */
public interface BiddingClientService extends Closeable {

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

    /**
     * Sets the hashMACService
     *
     * @param hashMACService
     */
    void setHashMACService(HashMACService hashMACService);


    /**
     * Gets the HashMACService if logged in
     *
     * @return the HashMACService if logged in; null otherwise
     */
    HashMACService getHashMACService();
}
