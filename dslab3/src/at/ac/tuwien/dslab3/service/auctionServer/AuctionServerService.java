/**
 * 
 */
package at.ac.tuwien.dslab3.service.auctionServer;

import org.bouncycastle.openssl.PasswordFinder;

import java.io.Closeable;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * @author klaus
 * 
 */
public interface AuctionServerService extends Closeable {

	/**
	 * Start the server
	 * 
	 *
     *
	 *
	 * @param tcpPort
	 *            the TCP port the server should listen on
	 * @param analyticsServerRef
	 *            the binding name of the analytics server in the RMI registry
	 * @param billingServerRef
	 *            the binding name of the billing server in the RMI registry
	 * @param keyDirectoy
	 * @param serverPrivateKeyFileLocation
	 * @param passwordFinder
	 * @throws IOException
	 */
	void start(int tcpPort, String analyticsServerRef, String billingServerRef, String keyDirectoy, String serverPrivateKeyFileLocation, PasswordFinder passwordFinder)
			throws IOException;

	/**
	 * Sets the exception handler for uncaught exceptions of invoked threads
	 * 
	 * @param exHandler
	 *            the handler for exceptions
	 */
	void setExceptionHandler(UncaughtExceptionHandler exHandler);

}
