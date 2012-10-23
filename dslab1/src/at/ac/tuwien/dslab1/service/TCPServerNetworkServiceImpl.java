package at.ac.tuwien.dslab1.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerNetworkServiceImpl implements TCPServerNetworkService {
	private ServerSocket socket;

	/**
	 * 
	 * @param port
	 * @throws IOException
	 */
	public TCPServerNetworkServiceImpl(Integer port) throws IOException {
		this.socket = null;

		setPort(port);
	}

	private void setPort(Integer port) throws IOException {
		if (port == null)
			return;

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
