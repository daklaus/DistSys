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
	private Integer port;

	/**
	 * 
	 * @param server
	 * @param port
	 * @throws IOException
	 */
	public UDPClientNetworkServiceImpl(InetAddress server, Integer port)
			throws IOException {
		this.server = null;
		this.socket = null;

		setServer(server);
		setServerPort(port);
	}

	/**
	 * 
	 * @param server
	 * @param port
	 * @throws IOException
	 */
	public UDPClientNetworkServiceImpl(String server, Integer port)
			throws IOException {
		this(InetAddress.getByName(server), port);
	}

	private void setServer(InetAddress server) {
		if (server == null)
			throw new IllegalArgumentException("server is null");

		this.server = server;
	}

	private void setServerPort(Integer serverPort) throws SocketException {
		if (serverPort == null)
			throw new IllegalArgumentException("server is null");

		this.port = serverPort;
		this.socket = new DatagramSocket();
	}

	@Override
	public void send(String message) throws IOException {
		if (socket == null || port == null || this.server == null)
			throw new IllegalStateException("UDP socket not initialized");

		byte[] buf = message.getBytes();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, server,
				port);
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
