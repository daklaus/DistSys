package at.ac.tuwien.dslab1.service;

import java.io.Closeable;
import java.rmi.RemoteException;

public interface RMIClientService extends Closeable {

	/**
	 * Searches the RMIRegistry for a remote binding associated with the
	 * specified name.
	 * 
	 * @param name
	 * @return the remote binding for the given name
	 * @throws RemoteException
	 */
	Object lookup(String name) throws RemoteException;
}
