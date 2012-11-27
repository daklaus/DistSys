/**
 * 
 */
package at.ac.tuwien.dslab2.service;

import java.io.IOException;
import java.util.Properties;

/**
 * @author klaus
 * 
 */
public interface PropertiesService {
	static final String REGISTRY_PROPERTIES_FILE = "registry.properties";
	static final String USER_PROPERTIES_FILE = "user.properties";
	static final String LOADTEST_PROPERTIES_FILE = "loadtest.properties";
	static final String REGISTRY_PROPERTIES_PORT_KEY = "registry.port";
	static final String REGISTRY_PROPERTIES_HOST_KEY = "registry.host";
	static final String BILLINGSERVER_USERNAME = "auctionClientUser";
	static final String BILLINGSERVER_PASSWORD = "12345";

	Properties getRegistryProperties() throws IOException;

	Properties getUserProperties() throws IOException;

	Properties getLoadTestProperties() throws IOException;
}
