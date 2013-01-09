/**
 * 
 */
package at.ac.tuwien.dslab3.service.billingServer;

import java.io.IOException;

/**
 * @author klaus
 * 
 */
public abstract class BillingServerFactory {
	public static BillingServer newBillingServer(String bindingName)
			throws IOException {
		return new BillingServerImpl(bindingName);
	}

	static BillingServerSecure getBillingServerSecure() {
		return BillingServerSecureImpl.getInstance();
	}
}
