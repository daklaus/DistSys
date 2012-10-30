package at.ac.tuwien.dslab1.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPClientNetworkServiceImpl implements UDPClientNetworkService {
	private InetAddress server;
	private DatagramSocket socket;
	private Integer serverPort;

	/**
	 * 
	 * @param server
	 * @param serverPort
	 * @throws IOException
	 */
	public UDPClientNetworkServiceImpl(InetAddress server, Integer serverPort)
			throws IOException {
		this.server = null;
		this.socket = null;

		setServer(server);
		setServerPort(serverPort);
	}

	/**
	 * 
	 * @param server
	 * @param serverPort
	 * @throws IOException
	 */
	public UDPClientNetworkServiceImpl(String server, Integer serverPort)
			throws IOException {
		this(InetAddress.getByName(server), serverPort);
	}

	private void setServer(InetAddress server) {
		if (server == null)
			throw new IllegalArgumentException("server is null");

		this.server = server;
	}

	private void setServerPort(Integer serverPort) throws SocketException {
		if (serverPort == null)
			throw new IllegalArgumentException("server is null");

		this.serverPort = serverPort;
		this.socket = new DatagramSocket();
	}

	@Override
	public void send(String message) throws IOException {
		if (socket == null || serverPort == null || this.server == null)
			throw new IllegalStateException("UDP socket not initialized");

		byte[] buf = message.getBytes();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, server,
				serverPort);
		socket.send(packet);
	}

	@Override
	public String receive() throws IOException {
		if (socket == null)
			throw new IllegalStateException("UDP socket not initialized");

		byte[] buf = new byte[socket.getReceiveBufferSize()];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);

		return new String(packet.getData(), 0, packet.getLength());
	}

	@Override
	public void close() throws IOException {
		if (socket != null)
			socket.close();
	}

}
