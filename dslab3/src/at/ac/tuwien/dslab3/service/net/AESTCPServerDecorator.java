package at.ac.tuwien.dslab3.service.net;

import javax.crypto.SecretKey;
import java.io.IOException;

class AESTCPServerDecorator implements TCPServerNetworkService {

    private final TCPServerNetworkService tcpServerNetworkService;
    private final SecretKey secretKey;

    public AESTCPServerDecorator(TCPServerNetworkService tcpServerNetworkService, SecretKey secretKey) {
        this.tcpServerNetworkService = tcpServerNetworkService;
        this.secretKey = secretKey;
    }

    @Override
    public TCPClientNetworkService accept() throws IOException {
        return new AESTCPClientDecorator(this.tcpServerNetworkService.accept(), this.secretKey);
    }

    @Override
    public void close() throws IOException {
        this.tcpServerNetworkService.close();
    }
}
