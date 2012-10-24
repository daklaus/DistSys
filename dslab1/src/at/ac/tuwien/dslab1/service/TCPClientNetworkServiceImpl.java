package at.ac.tuwien.dslab1.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class TCPClientNetworkServiceImpl implements TCPClientNetworkService {
	private Socket socket;
	private Scanner in;
	private PrintWriter out;

	/**
	 * 
	 * @param server
	 * @param serverPort
	 * @throws IOException
	 */
	public TCPClientNetworkServiceImpl(String server, Integer serverPort)
			throws IOException {
		this.socket = null;

		if (server == null || serverPort == null || serverPort <= 0)
			throw new IllegalArgumentException(
					"Either server and/or server port are not properly set");

		Socket socket = new Socket(server, serverPort);

		setSocket(socket);
	}

	/**
	 * 
	 * @param socket
	 *            a !connected! socket
	 * @throws IOException
	 */
	public TCPClientNetworkServiceImpl(Socket socket) throws IOException {
		setSocket(socket);
	}

	private void setSocket(Socket socket) throws IOException {
		if (socket == null || !socket.isConnected())
			throw new IllegalArgumentException(
					"The socket is null or not connected");

		this.socket = socket;

		this.in = new Scanner(new BufferedReader(new InputStreamReader(
				socket.getInputStream()))).useDelimiter(terminationChar + "+");
		this.out = new PrintWriter(socket.getOutputStream(), true);
	}

	@Override
	public void send(String messange) {
		if (out == null)
			throw new IllegalStateException("Output writer not initialized");

		out.print(messange + terminationChar);
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
	public void close() throws IOException {
		if (socket != null)
			socket.close();
		if (in != null)
			in.close();
		if (out != null)
			out.close();
	}

}
