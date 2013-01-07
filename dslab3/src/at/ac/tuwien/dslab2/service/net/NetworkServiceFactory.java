package at.ac.tuwien.dslab2.service.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;

public abstract class NetworkServiceFactory {

    public static TCPClientNetworkService newBase64TCPClientNetworkService(
            TCPClientNetworkService tcpClientNetworkService, Charset charset) {
        return new Base64TCPClientDecorator(tcpClientNetworkService, charset);
    }

    public static TCPServerNetworkService newBase64TCPServerNetworkService(
            TCPServerNetworkService tcpServerNetworkService, Charset charset) {
        return new Base64TCPServerDecorator(tcpServerNetworkService, charset);
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
