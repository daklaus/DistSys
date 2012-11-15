package at.ac.tuwien.dslab2.service.rmi;

import java.io.Closeable;
import java.rmi.Remote;

public interface RMIServerService extends Closeable {

	/**
	 * Binds an Remote interface to the specified name so that it can receive
	 * incoming calls.
	 * 
	 * @param name
	 *            the binding name
	 * @param remote
	 *            the Remote interface
	 */
	void bind(String name, Remote remote);
}
