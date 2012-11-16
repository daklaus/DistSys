/**
 * 
 */
package at.ac.tuwien.dslab2.service.auctionServer;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * @author klaus
 * 
 */
public interface AuctionServerService {

	/**
	 * Start the server
	 * 
	 * @param tcpPort
	 *            the TCP port the server should listen on
	 * @throws IOException
	 */
	void start(int tcpPort) throws IOException;

	/**
	 * Sets the exception handler for uncaught exceptions of invoked threads
	 * 
	 * @param exHandler
	 *            the handler for exceptions
	 */
	void setExceptionHandler(UncaughtExceptionHandler exHandler);

	/**
	 * Free all acquired resources
	 * 
	 * @throws IOException
	 */
	void close() throws IOException;
}
