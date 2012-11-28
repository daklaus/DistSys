/**
 * 
 */
package at.ac.tuwien.dslab2.service.analyticsServer;

import java.io.IOException;

/**
 * @author klaus
 * 
 */
public abstract class AnalyticsServerFactory {
	public static AnalyticsServer newAnalyticsServer(String analyticsServerBindingName) throws IOException {
		return new AnalyticsServerImpl();
	}
}
