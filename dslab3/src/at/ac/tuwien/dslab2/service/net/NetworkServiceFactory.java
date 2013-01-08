package at.ac.tuwien.dslab2.service.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

public abstract class NetworkServiceFactory {

    public static TCPClientNetworkService newRSATCPClientNetworkService(
            TCPClientNetworkService tcpClientNetworkService, PublicKey publicKey, PrivateKey privateKey) {
        return new RSATCPClientDecorator(tcpClientNetworkService, publicKey, privateKey);
    }

    public static TCPServerNetworkService newRSATCPServerNetworkService(
            TCPServerNetworkService tcpServerNetworkService, PublicKey publicKey, PrivateKey privateKey) {
        return new RSATCPServerDecorator(tcpServerNetworkService, publicKey, privateKey);
    }

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
