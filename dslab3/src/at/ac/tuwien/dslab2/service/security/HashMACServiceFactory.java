package at.ac.tuwien.dslab2.service.security;

public abstract class HashMACServiceFactory {
    public static HashMACService getService(String directory) {
        return new HashMACServiceImpl(directory);
    }
}
