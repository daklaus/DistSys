package at.ac.tuwien.dslab3.service.security;

import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

class HashMACServiceImpl implements HashMACService {

    private final SecretKeySpec secretKey;

    public HashMACServiceImpl(File keyDirectory, String username) throws IOException {
        File file = new File("dslab3/" + keyDirectory.getPath() + "/" + username + ".key");
        byte[] bytesDecoded = Hex.decode(readBytes(file));
        this.secretKey = new SecretKeySpec(bytesDecoded, "HmacSHA256");
    }

    @Override
    public byte[] createHashMAC(byte[] data) throws GeneralSecurityException {
        Mac hMac = Mac.getInstance("HmacSHA256");
        hMac.init(secretKey);
        hMac.update(data);
        return hMac.doFinal();
    }

    private byte[] readBytes(File file) throws IOException {
        long fileLength = file.length();
        if (fileLength > Integer.MAX_VALUE) {
            throw new IOException("The File " + file + " is too big");
        }
        byte[] buffer = new byte[(int) fileLength];
        InputStream ios = null;
        try {
            ios = new FileInputStream(file);
            if (ios.read(buffer) == -1) {
                throw new IOException("EOF reached while trying to read the whole file");
            }
        } finally {
            try {
                if (ios != null)
                    ios.close();
            } catch (IOException ignored) {
            }
        }

        return buffer;
    }

    @Override
    public boolean verifyHashMAC(byte[] expectedHash, byte[] actualHash) {
        return MessageDigest.isEqual(expectedHash, actualHash);
    }
}