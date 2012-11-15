package at.ac.tuwien.dslab2.service.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClientNetworkServiceImpl implements UDPClientNetworkService {
	private final InetAddress server;
	private final DatagramSocket socket;
	private final int port;

	/**
	 * 
	 * @param server
	 * @param port
	 * @throws IOException
	 */
	public UDPClientNetworkServiceImpl(InetAddress server, int port)
			throws IOException {
		if (server == null)
			throw new IllegalArgumentException("server is null");

		this.server = server;
		this.port = port;
		this.socket = new DatagramSocket();
	}

	/**
	 * 
	 * @param server
	 * @param port
	 * @throws IOException
	 */
	public UDPClientNetworkServiceImpl(String server, int port)
			throws IOException {
		this(InetAddress.getByName(server), port);
	}

	@Override
	public void send(String message) throws IOException {
		if (socket == null || this.server == null)
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
