package at.ac.tuwien.dslab1.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class TCPServerNetworkServiceImpl implements TCPServerNetworkService {
	private InetAddress host;
	private Socket tcpSocket;
	// private ServerSocket tcpListenSocket;
	private DatagramSocket udpSocket;
	private DatagramSocket udpListenSocket;
	private BufferedReader tcpIn;
	private PrintWriter tcpOut;
	private Integer othersUDPPort;

	/**
	 * 
	 * @param host
	 * @param othersTCPPort
	 * @param ownTCPPort
	 * @param othersUDPPort
	 * @param ownUDPPort
	 * @throws IOException
	 */
	public TCPServerNetworkServiceImpl(String host, Integer othersTCPPort,
	/* Integer ownTCPPort, */Integer othersUDPPort, Integer ownUDPPort)
			throws IOException {
		this.host = null;
		this.tcpSocket = null;
		// this.tcpListenSocket = null;
		this.udpSocket = null;
		this.udpListenSocket = null;

		setHost(host);
		setOthersTCPPort(othersTCPPort);
		// setOwnTCPPort(ownTCPPort);
		setOthersUDPPort(othersUDPPort);
		setOwnUDPPort(ownUDPPort);
	}

	/**
	 * 
	 * @param host
	 * @param othersTCPPort
	 * @param ownUDPPort
	 * @throws IOException
	 */
	public TCPServerNetworkServiceImpl(String host, Integer othersTCPPort,
			Integer ownUDPPort) throws IOException {
		this(host, othersTCPPort, /* null, */null, ownUDPPort);
	}

	private void setHost(String host) throws UnknownHostException {
		if (host == null)
			return;

		this.host = InetAddress.getByName(host);
	}

	private void setOthersTCPPort(Integer othersTCPPort) throws IOException {
		if (othersTCPPort == null || this.host == null)
			return;

		this.tcpSocket = new Socket(host, othersTCPPort);
		this.tcpIn = new BufferedReader(new InputStreamReader(
				tcpSocket.getInputStream()));
		this.tcpOut = new PrintWriter(tcpSocket.getOutputStream(), true);
	}

	// private void setOwnTCPPort(Integer ownTCPPort) throws IOException {
	// if (ownTCPPort == null)
	// return;
	//
	// this.tcpListenSocket = new ServerSocket(ownTCPPort);
	// }

	private void setOthersUDPPort(Integer othersUDPPort) throws SocketException {
		if (othersUDPPort == null)
			return;

		this.othersUDPPort = othersUDPPort;
		this.udpSocket = new DatagramSocket();
	}

	private void setOwnUDPPort(Integer ownUDPPort) throws SocketException {
		if (ownUDPPort == null)
			return;

		this.udpListenSocket = new DatagramSocket(ownUDPPort);
	}

	public void tcpSend(String messange) {
		if (tcpOut == null)
			throw new IllegalStateException("Output writer not initialized");

		tcpOut.println(messange);
	}

	public String tcpReceive() throws IOException {
		if (tcpIn == null)
			throw new IllegalStateException("Input reader not initialized");

		return tcpIn.readLine();
	}

	public void udpSend(String message) throws IOException {
		if (udpSocket == null || othersUDPPort == null || this.host == null)
			throw new IllegalStateException("UDP socket not initialized");

		byte[] buf = message.getBytes();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, host,
				othersUDPPort);
		udpSocket.send(packet);
	}

	public String udpReceive() throws IOException {
		if (udpListenSocket == null)
			throw new IllegalStateException(
					"UDP listening socket not initialized");

		byte[] buf = new byte[udpListenSocket.getReceiveBufferSize()];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		udpListenSocket.receive(packet);

		return new String(packet.getData(), 0, packet.getLength());
	}

	// @Override
	// public void setUDPReplyListener(ReplyListener listener) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public Socket tcpAccept() throws IOException {
	// if (tcpListenSocket == null || !tcpListenSocket.isBound())
	// throw new IllegalStateException("TCP listener socket not initialized");
	//
	// return tcpListenSocket.accept();
	// }
	//
	// @Override
	// public void udpStartListening() {
	// // TODO Auto-generated method stub
	//
	// }

	@Override
	public void close() throws IOException {
		if (tcpIn != null)
			tcpIn.close();
		if (tcpOut != null)
			tcpOut.close();
		if (tcpSocket != null)
			tcpSocket.close();
		if (udpSocket != null)
			udpSocket.close();
		if (udpListenSocket != null)
			udpListenSocket.close();
	}

	@Override
	public TCPClientNetworkService accept() {
		// TODO Auto-generated method stub
		return null;
	}

}
