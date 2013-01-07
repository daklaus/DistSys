package at.ac.tuwien.dslab2.service.net;

import java.io.IOException;
import java.nio.charset.Charset;

class Base64TCPServerDecorator implements TCPServerNetworkService {

    private final TCPServerNetworkService tcpServerNetworkService;
    private final Charset charset;

    public Base64TCPServerDecorator(TCPServerNetworkService tcpServerNetworkService, Charset charset) {
        this.tcpServerNetworkService = tcpServerNetworkService;
        this.charset = charset;
    }

    @Override
    public TCPClientNetworkService accept() throws IOException {
        return new Base64TCPClientDecorator(this.tcpServerNetworkService.accept(), this.charset);
    }

    @Override
    public void close() throws IOException {
        this.tcpServerNetworkService.close();
    }
}
