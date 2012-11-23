/**
 * 
 */
package at.ac.tuwien.dslab2.service.billingServer;

import java.io.IOException;

/**
 * @author klaus
 * 
 */
public abstract class BillingServerFactory {
	public static BillingServer newBillingServer() throws IOException {
		return new BillingServerImpl();
	}

	static BillingServerSecure newBillingServerSecure() {
		return new BillingServerSecureImpl();
	}
}
