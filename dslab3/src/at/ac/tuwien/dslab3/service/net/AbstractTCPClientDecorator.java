package at.ac.tuwien.dslab3.service.net;

import java.io.IOException;
import java.net.InetAddress;

abstract class AbstractTCPClientDecorator implements TCPClientNetworkService {

	private final TCPClientNetworkService tcpClientNetworkService;

	AbstractTCPClientDecorator(TCPClientNetworkService tcpClientNetworkService) {
		this.tcpClientNetworkService = tcpClientNetworkService;
	}

	@Override
	public void send(String message) throws IOException {
		this.tcpClientNetworkService.send(message);
	}

	@Override
	public String receive() throws IOException {
		return this.tcpClientNetworkService.receive();
	}

	@Override
	public InetAddress getLocalAddress() {
		return this.tcpClientNetworkService.getLocalAddress();
	}

	@Override
	public InetAddress getAddress() {
		return this.tcpClientNetworkService.getAddress();
	}

	@Override
	public int getLocalPort() {
		return this.tcpClientNetworkService.getLocalPort();
	}

	@Override
	public int getPort() {
		return this.tcpClientNetworkService.getPort();
	}

	@Override
	public boolean isConnected() {
		return this.tcpClientNetworkService.isConnected();
	}

	@Override
	public void close() throws IOException {
		this.tcpClientNetworkService.close();
	}
}
