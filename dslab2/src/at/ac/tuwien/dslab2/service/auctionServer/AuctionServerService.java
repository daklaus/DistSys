/**
 * 
 */
package at.ac.tuwien.dslab2.service.auctionServer;

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
	 * @param tcpPort
	 *            the TCP port the server should listen on
	 * @param billingServerRef
	 *            the binding name of the billing server in the RMI registry
	 * @param analyticsServerRef
	 *            the binding name of the analytics server in the RMI registry
	 * @throws IOException
	 */
	void start(int tcpPort, String billingServerRef, String analyticsServerRef)
			throws IOException;

	/**
	 * Sets the exception handler for uncaught exceptions of invoked threads
	 * 
	 * @param exHandler
	 *            the handler for exceptions
	 */
	void setExceptionHandler(UncaughtExceptionHandler exHandler);

}
