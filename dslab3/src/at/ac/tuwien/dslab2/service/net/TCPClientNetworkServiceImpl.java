package at.ac.tuwien.dslab2.service.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.NoSuchElementException;
import java.util.Scanner;

class TCPClientNetworkServiceImpl implements TCPClientNetworkService {
	private final Socket socket;
	private final Scanner in;
	private final PrintWriter out;

	/**
	 * 
	 * @param server
	 * @param serverPort
	 * @throws IOException
	 */
	public TCPClientNetworkServiceImpl(String server, int serverPort)
			throws IOException {
		this(createSocket(server, serverPort));
	}

	private static Socket createSocket(String server, int serverPort)
			throws IOException {
		if (server == null || serverPort <= 0)
			throw new IllegalArgumentException(
					"Either server and/or server port are not properly set");

		return new Socket(server, serverPort);
	}

	/**
	 * 
	 * @param socket
	 *            a !connected! socket
	 * @throws IOException
	 */
	public TCPClientNetworkServiceImpl(Socket socket) throws IOException {
		if (socket == null || !socket.isConnected())
			throw new IllegalArgumentException(
					"The socket is null or not connected");

		this.socket = socket;

		this.in = new Scanner(new BufferedReader(new InputStreamReader(
				socket.getInputStream()))).useDelimiter(terminationChar + "+");
		this.out = new PrintWriter(socket.getOutputStream(), true);
	}

	@Override
	public void send(String message) {
		if (out == null)
			throw new IllegalStateException("Output writer not initialized");

		out.print(message + terminationChar);
		out.flush();
	}

	@Override
	public String receive() throws IOException {
		if (in == null)
			throw new IllegalStateException("Input reader not initialized");

		try {
			return in.next();
		} catch (NoSuchElementException e) {
			throw new SocketException("Socket closed");
		}
	}

	@Override
	public InetAddress getLocalAddress() {
		return socket.getLocalAddress();
	}

	@Override
	public InetAddress getAddress() {
		return socket.getInetAddress();
	}

	@Override
	public int getLocalPort() {
		return socket.getLocalPort();
	}

	@Override
	public int getPort() {
		return socket.getPort();
	}

	@Override
	public void close() throws IOException {
		if (socket != null)
			socket.close();
		if (in != null)
			in.close();
		if (out != null)
			out.close();
	}

}
