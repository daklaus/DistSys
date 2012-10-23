package at.ac.tuwien.dslab1.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClientNetworkServiceImpl implements TCPClientNetworkService {
	private InetAddress server;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;

	/**
	 * 
	 * @param server
	 * @param serverPort
	 * @throws IOException
	 */
	public TCPClientNetworkServiceImpl(String server, Integer serverPort)
			throws IOException {
		this.server = null;
		this.socket = null;

		setServer(server);
		setServerPort(serverPort);
	}

	private void setServer(String host) throws UnknownHostException {
		if (host == null)
			return;

		this.server = InetAddress.getByName(host);
	}

	private void setServerPort(Integer othersTCPPort) throws IOException {
		if (othersTCPPort == null || this.server == null)
			return;

		this.socket = new Socket(server, othersTCPPort);
		this.in = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		this.out = new PrintWriter(socket.getOutputStream(), true);
	}

	@Override
	public void send(String messange) {
		if (out == null)
			throw new IllegalStateException("Output writer not initialized");

		out.println(messange);
	}

	@Override
	public String receive() throws IOException {
		if (in == null)
			throw new IllegalStateException("Input reader not initialized");

		return in.readLine();
	}

	@Override
	public void close() throws IOException {
		if (in != null)
			in.close();
		if (out != null)
			out.close();
		if (socket != null)
			socket.close();
	}

}
