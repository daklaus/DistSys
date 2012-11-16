package at.ac.tuwien.dslab2.service.rmi;

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
		try {
			this.registry = LocateRegistry.createRegistry(port);
		} catch (RemoteException e) {
			throw new RemoteException("Unable to create registry with port "
					+ port, e);
		}
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
	public void close() {
		try {
			if (this.bindingName != null)
				this.registry.unbind(this.bindingName);
			if (this.toBeStubbed != null)
				UnicastRemoteObject.unexportObject(this.toBeStubbed, false);
		} catch (Exception e) {
			// Maybe we should ignore the error to proceed with closing the
			// service
			throw new RuntimeException("Unable to close the RMI service", e);
		}
	}
}
