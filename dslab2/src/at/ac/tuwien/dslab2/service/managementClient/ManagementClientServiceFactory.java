/**
 * 
 */
package at.ac.tuwien.dslab2.service.managementClient;

import java.io.IOException;

/**
 * @author klaus
 * 
 */
public abstract class ManagementClientServiceFactory {
	public static ManagementClientService newManagementClientService(
			String analyticsServerName, String billingServerName)
			throws IOException {
		return new ManagementClientServiceImpl(analyticsServerName,
				billingServerName);
	}
}
