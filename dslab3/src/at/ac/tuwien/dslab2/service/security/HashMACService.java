package at.ac.tuwien.dslab2.service.security;

import at.ac.tuwien.dslab2.domain.User;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface HashMACService {

    /**
     * This method creates a key for the specified username.
     * It uses one key in the keys directory
     * specified by the username (<keysDir>/<username>.key)
     * @param username
     * @return the secret key
     * @throws IOException
     */
    public SecretKey createKeyFor(String username) throws IOException;

    /**
     * Creates a new HashMAC for the provided data using specified Key.
     * @param key
     * @param data
     * @return the creates HashMAC
     * @throws GeneralSecurityException
     */
    public byte[] createHashMAC(SecretKey key, byte[] data) throws GeneralSecurityException;

    /**
     * Verifies if the actual HashMAC matches the expected HashMAC
     * @param expectedHash
     * @param actualHash
     * @return true if it matches false otherwise
     */
    public boolean verifyHashMAC(byte[] expectedHash, byte[] actualHash);
}
