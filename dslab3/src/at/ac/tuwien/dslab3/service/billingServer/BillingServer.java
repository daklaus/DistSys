package at.ac.tuwien.dslab3.service.billingServer;

import java.io.Closeable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BillingServer extends Closeable, Remote {

	/**
	 * Logs the user in.
	 * 
	 * @param username
	 *            the users user name
	 * @param password
	 *            the users password
	 * @return a <code>BillingServerSecure</code> if successfully logged in,
	 *         null otherwise.
	 */
	BillingServerSecure login(String username, String password)
			throws RemoteException;
}
