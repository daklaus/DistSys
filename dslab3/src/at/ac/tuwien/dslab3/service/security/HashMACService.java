package at.ac.tuwien.dslab3.service.security;

import java.security.GeneralSecurityException;

public interface HashMACService {
    /**
     * Creates a new HashMAC.
     * @param data
     * @return the creates HashMAC
     * @throws GeneralSecurityException
     */
    public byte[] createHashMAC(byte[] data) throws GeneralSecurityException;

    /**
     * Verifies if the actual HashMAC matches the expected HashMAC
     * @param expectedHash
     * @param actualHash
     * @return true if it matches false otherwise
     */
    public boolean verifyHashMAC(byte[] expectedHash, byte[] actualHash);
}
