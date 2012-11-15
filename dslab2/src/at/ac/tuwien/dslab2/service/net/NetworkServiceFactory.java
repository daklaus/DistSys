package at.ac.tuwien.dslab2.service.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public abstract class NetworkServiceFactory {

	public static TCPClientNetworkService newTCPClientNetworkService(
			Socket socket) throws IOException {
		return new TCPClientNetworkServiceImpl(socket);
	}

	public static TCPClientNetworkService newTCPClientNetworkService(
			String server, int port) throws IOException {
		return new TCPClientNetworkServiceImpl(server, port);
	}

	public static TCPServerNetworkService newTCPServerNetworkService(
			int port) throws IOException {
		return new TCPServerNetworkServiceImpl(port);
	}

	public static UDPClientNetworkService newUDPClientNetworkService(
			String server, int port) throws IOException {
		return new UDPClientNetworkServiceImpl(server, port);
	}

	public static UDPClientNetworkService newUDPClientNetworkService(
			InetAddress server, int port) throws IOException {
		return new UDPClientNetworkServiceImpl(server, port);
	}

	public static UDPServerNetworkService newUDPServerNetworkService(
			int port) throws IOException {
		return new UDPServerNetworkServiceImpl(port);
	}
}
