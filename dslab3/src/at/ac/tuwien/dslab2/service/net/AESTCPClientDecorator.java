package at.ac.tuwien.dslab2.service.net;

import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;

class AESTCPClientDecorator implements TCPClientNetworkService {

    private final TCPClientNetworkService tcpClientNetworkService;
    private final SecretKey secretKey;

    public AESTCPClientDecorator(TCPClientNetworkService tcpClientNetworkService, SecretKey secretKey) {
        this.tcpClientNetworkService = tcpClientNetworkService;
        this.secretKey = secretKey;
    }

    @Override
    public void send(String message) throws IOException {
        try {
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
            byte[] encryptedMessage = cipher.doFinal(message.getBytes(Charset.forName("UTF-16")));
            byte[] encodedMessage = Base64.encode(encryptedMessage);
            this.tcpClientNetworkService.send(new String(encodedMessage, Charset.forName("UTF-16")));
        } catch (GeneralSecurityException e) {
            throw new IOException("There's a problem with RSA-Encryption", e);
        }
    }

    @Override
    public String receive() throws IOException {
        try {
            String receivedMessage = this.tcpClientNetworkService.receive();
            byte[] decodedMessage = Base64.decode(receivedMessage);
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
            byte[] decryptedMessage = cipher.doFinal(decodedMessage);
            return new String(decryptedMessage, Charset.forName("UTF-16"));
        } catch (GeneralSecurityException e) {
            throw new IOException("There's a problem with RSA-Encryption", e);
        }
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
