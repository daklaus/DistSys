package at.ac.tuwien.dslab1.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServerNetworkServiceImpl implements UDPServerNetworkService {
	private DatagramSocket socket;

	/**
	 * 
	 * @param port
	 * @throws IOException
	 */
	public UDPServerNetworkServiceImpl(Integer port) throws IOException {
		this.socket = null;

		setPort(port);
	}

	private void setPort(Integer port) throws SocketException {
		if (port == null)
			return;

		this.socket = new DatagramSocket(port);
	}

	@Override
	public void send(String message) throws IOException {
		if (socket == null)
			throw new IllegalStateException("UDP socket not initialized");
		if (socket.getInetAddress() == null || socket.getPort() <= -1)
			throw new IllegalStateException(
					"No client connected yet. A client must send anything "
							+ "to the server before the server can send "
							+ "something to the client");

		byte[] buf = message.getBytes();
		DatagramPacket packet = new DatagramPacket(buf, buf.length,
				socket.getInetAddress(), socket.getPort());
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
