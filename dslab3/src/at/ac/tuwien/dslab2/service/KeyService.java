package at.ac.tuwien.dslab2.service;

import at.ac.tuwien.dslab2.domain.User;

public interface KeyService {

    /**
     * Creates a new HashMAC for the specified User. This method actually
     * uses one key in the keys directory specified by the Username (<keysDir>/<username>.key)
     * @param user
     * @return the creates HashMAC
     */
    public byte[] createHashMACFor(User user);

    /**
     * Verifies if the actual HashMAC matches the expected HashMAC
     * @param expectedHash
     * @param actualHash
     * @return true if it matches false otherwise
     */
    public boolean verifyHashMAC(byte[] expectedHash, byte[] actualHash);
}
