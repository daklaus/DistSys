package at.ac.tuwien.dslab2.service.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class RMIClientServiceImpl implements RMIClientService {

	private Registry registry;

	public RMIClientServiceImpl(String host, int port) {
		try {
			registry = LocateRegistry.getRegistry(host, port);
		} catch (RemoteException e) {
			throw new RuntimeException("Unable to locate Registry for " + host
					+ ":" + port, e);
		}
	}

	@Override
	public Object lookup(String name) {
		try {
			return this.registry.lookup(name);
		} catch (Exception e) {
			throw new RuntimeException("Unable to lookup " + name
					+ " in registry", e);
		}
	}

	@Override
	public void close() {
		this.registry = null;
	}
}
