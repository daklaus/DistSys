package at.ac.tuwien.dslab2.service.security;

import java.io.IOException;

public abstract class HashMACServiceFactory {
    public static HashMACService getService(String directory, String username) throws IOException {
        return new HashMACServiceImpl(directory, username);
    }
}
