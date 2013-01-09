package at.ac.tuwien.dslab3.service.net;

import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;

class RSATCPClientDecorator extends AbstractTCPClientDecorator {

    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    public RSATCPClientDecorator(TCPClientNetworkService tcpClientNetworkService, PublicKey publicKey, PrivateKey privateKey) {
        super(tcpClientNetworkService);
        this.publicKey = publicKey;
        this.privateKey = privateKey;

    }

    @Override
    public void send(String message) throws IOException {
        try {
            Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA256AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
            byte[] encryptedMessage = cipher.doFinal(message.getBytes(Charset.forName("UTF-16")));
            byte[] encodedMessage = Base64.encode(encryptedMessage);
            super.send(new String(encodedMessage, Charset.forName("UTF-16")));
        } catch (GeneralSecurityException e) {
            throw new IOException("There's a problem with RSA-Encryption", e);
        }
    }

    @Override
    public String receive() throws IOException {
        try {
            String receivedMessage = super.receive();
            byte[] decodedMessage = Base64.decode(receivedMessage);
            Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA256AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, this.privateKey);
            byte[] decryptedMessage = cipher.doFinal(decodedMessage);
            return new String(decryptedMessage, Charset.forName("UTF-16"));
        } catch (GeneralSecurityException e) {
            throw new IOException("There's a problem with RSA-Encryption", e);
        }
    }
}
