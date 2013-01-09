package at.ac.tuwien.dslab3.service.rmi;

import java.io.Closeable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServerService extends Closeable {

	/**
	 * Binds an Remote interface to the specified name so that it can receive
	 * incoming calls.
	 * 
	 * @param name
	 *            the binding name
	 * @param remote
	 *            the Remote interface
	 * @throws RemoteException
	 */
	void bind(String name, Remote remote) throws RemoteException;
}
