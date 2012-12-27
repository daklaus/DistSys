package at.ac.tuwien.dslab2.service.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

class RMIClientServiceImpl implements RMIClientService {

	private final Registry registry;

	public RMIClientServiceImpl(String host, int port) throws RemoteException {
		try {
			registry = LocateRegistry.getRegistry(host, port);
		} catch (RemoteException e) {
			throw new RemoteException("Unable to locate registry for " + host
					+ ":" + port, e);
		}
	}

	@Override
	public Object lookup(String name) throws RemoteException {
		try {
			return this.registry.lookup(name);
		} catch (Exception e) {
			throw new RemoteException("Unable to lookup " + name
					+ " in registry", e);
		}
	}

	@Override
	public void close() {
	}
}
