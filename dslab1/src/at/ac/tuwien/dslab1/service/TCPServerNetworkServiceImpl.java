package at.ac.tuwien.dslab1.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerNetworkServiceImpl implements TCPServerNetworkService {
	private final ServerSocket socket;

	/**
	 * 
	 * @param port
	 * @throws IOException
	 */
	public TCPServerNetworkServiceImpl(int port) throws IOException {
		if (port <= 0)
			throw new IllegalArgumentException("Port must be a positive number");

		this.socket = new ServerSocket(port);
	}

	@Override
	public TCPClientNetworkService accept() throws IOException {
		if (socket == null || !socket.isBound())
			throw new IllegalStateException(
					"TCP listener socket not initialized");

		Socket clientSocket = socket.accept();
		TCPClientNetworkService ns = new TCPClientNetworkServiceImpl(
				clientSocket);

		return ns;
	}

	@Override
	public void close() throws IOException {
		if (socket != null)
			socket.close();
	}

}
