package at.ac.tuwien.dslab3.service.net;

import javax.crypto.SecretKey;
import java.io.IOException;

class AESTCPServerDecorator implements TCPServerNetworkService {

    private final TCPServerNetworkService tcpServerNetworkService;
    private final SecretKey secretKey;
    private final byte[] iv;

    public AESTCPServerDecorator(TCPServerNetworkService tcpServerNetworkService, SecretKey secretKey, byte[] iv) {
        this.tcpServerNetworkService = tcpServerNetworkService;
        this.secretKey = secretKey;
        this.iv = iv;
    }

    @Override
    public TCPClientNetworkService accept() throws IOException {
        return new AESTCPClientDecorator(this.tcpServerNetworkService.accept(), secretKey, iv);
    }

    @Override
    public void close() throws IOException {
        this.tcpServerNetworkService.close();
    }
}
