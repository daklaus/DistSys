/**
 * 
 */
package at.ac.tuwien.dslab1.service.server;

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
	void start(Integer tcpPort) throws IOException;

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
