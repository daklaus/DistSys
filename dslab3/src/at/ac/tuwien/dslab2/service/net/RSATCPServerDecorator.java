package at.ac.tuwien.dslab2.service.net;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

class RSATCPServerDecorator implements TCPServerNetworkService {

    private final TCPServerNetworkService tcpServerNetworkService;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public RSATCPServerDecorator(TCPServerNetworkService tcpServerNetworkService, PublicKey publicKey, PrivateKey privateKey) {
        this.tcpServerNetworkService = tcpServerNetworkService;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    @Override
    public TCPClientNetworkService accept() throws IOException {
        return new RSATCPClientDecorator(this.tcpServerNetworkService.accept(), this.publicKey, this.privateKey);
    }

    @Override
    public void close() throws IOException {
        this.tcpServerNetworkService.close();
    }
}
