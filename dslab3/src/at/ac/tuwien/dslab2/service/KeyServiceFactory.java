package at.ac.tuwien.dslab2.service;

public abstract class KeyServiceFactory {
    public static KeyService getService(String direcory) {
        return new KeyServiceImpl(direcory);
    }
}
