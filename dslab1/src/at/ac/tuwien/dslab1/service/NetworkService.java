/**
 * 
 */
package at.ac.tuwien.dslab1.service;

import java.io.IOException;

/**
 * @author klaus
 * 
 */
public interface NetworkService {

	/**
	 * Sends a message to the other machine via TCP.
	 * 
	 * @param messange
	 */
	public void tcpSend(String messange);

	/**
	 * Listens for a message on the TCP port from the other machine. Attention
	 * this method blocks until it receives a reply from the other machine!
	 * 
	 * @return the string which is received on the TCP port
	 * @throws IOException
	 */
	public String tcpReceive() throws IOException;

	/**
	 * Sends a message to the other machine via UDP.
	 * 
	 * @param message
	 * @throws IOException
	 */
	public void udpSend(String message) throws IOException;

	/**
	 * Listens for a message on the UDP port from the other machine. Attention
	 * this method blocks until it receives a reply from the other machine!
	 * 
	 * @return the string which is received on the UDP port
	 * @throws IOException
	 */
	public String udpReceive() throws IOException;

	/**
	 * Start listening on the UDP port if everything is configured right
	 */
	// public void udpStartListening();

	/**
	 * Registers the listener for the reply from the other machine on the UDP
	 * port.
	 * 
	 * @param listener
	 */
	// public void setUDPReplyListener(ReplyListener listener);

	/**
	 * Starts listening for clients connecting on the own TCP port
	 * 
	 * @return a socket each time a client connects
	 * @throws IOException
	 */
	// public Socket tcpAccept() throws IOException;

	/**
	 * Closes the all connections in a proper manner
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException;

}
