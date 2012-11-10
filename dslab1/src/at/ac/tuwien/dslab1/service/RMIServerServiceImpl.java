package at.ac.tuwien.dslab1.service;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServerServiceImpl implements RMIServerService {
    private final Registry registry;
    private String bindingName;
    private Remote stub;
    /*
     * This reference is basically just for holding a strong reference so that the
     * Garbage Collector won't erase it.
     * (see: http://stackoverflow.com/questions/645208/java-rmi-nosuchobjectexception-no-such-object-in-table/854097#854097)
     */
    private Remote toBeStubbed;

    public RMIServerServiceImpl(int port) {
        try {
            this.registry = LocateRegistry.createRegistry(port);
        } catch (RemoteException e) {
            throw new RuntimeException("Unable to create registry with port " + port, e);
        }
    }

    @Override
    public void bind(String name, Remote remote) {
        try {
            this.toBeStubbed = remote;
            this.stub = UnicastRemoteObject.exportObject(this.toBeStubbed, 0);
            this.bindingName = name;
            this.registry.rebind(name, this.stub);
        } catch (Exception e) {
            throw new RuntimeException("Unable to bind object with name " + name, e);
        }
    }

    @Override
    public void close() {
        try {
            if (this.bindingName != null)
                this.registry.unbind(this.bindingName);
            if (this.toBeStubbed != null)
                UnicastRemoteObject.unexportObject(this.toBeStubbed, false);
        } catch (Exception e) {
            throw new RuntimeException("Unable to close Server",e);
        }
    }
}
