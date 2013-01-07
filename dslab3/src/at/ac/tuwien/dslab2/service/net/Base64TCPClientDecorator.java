package at.ac.tuwien.dslab2.service.net;

import org.bouncycastle.util.encoders.Base64;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.Charset;

class Base64TCPClientDecorator implements TCPClientNetworkService {

    private final TCPClientNetworkService tcpClientNetworkService;
    private final Charset charset;

    public Base64TCPClientDecorator(TCPClientNetworkService tcpClientNetworkService, Charset charset) {
        this.tcpClientNetworkService = tcpClientNetworkService;
        this.charset = charset;
    }

    @Override
    public void send(String messange) throws IOException {
        byte[] encodedMessageBytes = Base64.encode(messange.getBytes(this.charset));
        String encodedMessage = new String(encodedMessageBytes, this.charset);
        this.tcpClientNetworkService.send(encodedMessage);
    }

    @Override
    public String receive() throws IOException {
        String message = this.tcpClientNetworkService.receive();
        byte[] decodedMessageBytes = Base64.decode(message);
        return new String(decodedMessageBytes, this.charset);
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
    public void close() throws IOException {
        this.tcpClientNetworkService.close();
    }
}
