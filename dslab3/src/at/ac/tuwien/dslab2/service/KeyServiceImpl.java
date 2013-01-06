package at.ac.tuwien.dslab2.service;

import at.ac.tuwien.dslab2.domain.User;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

class KeyServiceImpl implements KeyService {

    private final File keyDirectory;

    public KeyServiceImpl(String directory) {
        this.keyDirectory = new File(directory);
    }

    @Override
    public byte[] createHashMAC(SecretKey secretKey, byte[] data) throws GeneralSecurityException {
        Mac hMac = Mac.getInstance("HmacSHA256");
        hMac.init(secretKey);
        hMac.update(data);
        return hMac.doFinal();
    }

    @Override
    public SecretKey createKeyFor(User user) throws IOException {
        File file = new File(this.keyDirectory.getPath() + "/" + user.getName() + ".key");
        byte[] bytesDecoded = Hex.decode(readBytes(file));
        return new SecretKeySpec(bytesDecoded, "HmacSHA256");
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