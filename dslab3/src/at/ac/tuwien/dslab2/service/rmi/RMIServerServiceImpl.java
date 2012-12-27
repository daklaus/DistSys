package at.ac.tuwien.dslab2.service.rmi;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

class RMIServerServiceImpl implements RMIServerService {
	private final Registry registry;
	private String bindingName;
	private Remote stub;

	/*
	 * This reference is basically just for holding a strong reference so that
	 * the Garbage Collector won't erase it. (see:
	 * http://stackoverflow.com/questions
	 * /645208/java-rmi-nosuchobjectexception-no
	 * -such-object-in-table/854097#854097)
	 */
	private Remote toBeStubbed;

	public RMIServerServiceImpl(int port) throws RemoteException {
		Registry r = null;
		try {
			r = LocateRegistry.createRegistry(port);
		} catch (RemoteException e) {
			// Try to get the registry if it couldn't be created at that port
			try {
				r = LocateRegistry.getRegistry("localhost", port);
			} catch (RemoteException e2) {
				throw new RemoteException(
						"Unable to create registry with port " + port, e);
			}
		}
		this.registry = r;
	}

	@Override
	public void bind(String name, Remote remote) throws RemoteException {
		try {
			this.toBeStubbed = remote;
			this.stub = UnicastRemoteObject.exportObject(this.toBeStubbed, 0);
			this.bindingName = name;
			this.registry.rebind(name, this.stub);
		} catch (Exception e) {
			throw new RemoteException(
					"Unable to bind object with name " + name, e);
		}
	}

	@Override
	public void close() throws IOException {
		try {
			if (this.bindingName != null)
				this.registry.unbind(this.bindingName);
		} catch (NotBoundException e) {
			// We don't care if it wasn't bound
		} catch (IOException e) {
			// We don't care if the registry isn't available anymore
		}

		if (this.toBeStubbed != null) {
			UnicastRemoteObject.unexportObject(this.toBeStubbed, true);
		}
	}
}
