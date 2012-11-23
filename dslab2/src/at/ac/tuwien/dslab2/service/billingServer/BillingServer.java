package at.ac.tuwien.dslab2.service.billingServer;

import java.io.Closeable;

public interface BillingServer extends Closeable {

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
	BillingServerSecure login(String username, String password);
}
