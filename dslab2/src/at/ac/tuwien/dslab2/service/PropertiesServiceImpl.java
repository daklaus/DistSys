/**
 * 
 */
package at.ac.tuwien.dslab2.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author klaus
 * 
 */
public class PropertiesServiceImpl implements PropertiesService {
	private Properties registry;
	private Properties user;
	private Properties loadTest;

	// Private constructor prevents instantiation from other classes
	private PropertiesServiceImpl() {
		registry = null;
		user = null;
		loadTest = null;
	}

	private static class PropertiesServiceHolder {
		public static final PropertiesService INSTANCE = new PropertiesServiceImpl();
	}

	public static PropertiesService getInstance() {
		return PropertiesServiceHolder.INSTANCE;
	}

	@Override
	public Properties getRegistryProperties() throws IOException {
		if (registry == null) {
			registry = loadProperties(REGISTRY_PROPERTIES_FILE);
		}

		checkKey(registry, REGISTRY_PROPERTIES_HOST_KEY);
		checkKey(registry, REGISTRY_PROPERTIES_PORT_KEY);

		return registry;
	}

	@Override
	public Properties getUserProperties() throws IOException {
		if (user == null) {
			user = loadProperties(USER_PROPERTIES_FILE);
		}

		return user;
	}

	@Override
	public Properties getLoadTestProperties() throws IOException {
		if (loadTest == null) {
			loadTest = loadProperties(LOADTEST_PROPERTIES_FILE);
		}

		checkKey(loadTest, LOADTEST_PROPERTIES_CLIENTS_KEY);
		checkKey(loadTest, LOADTEST_PROPERTIES_AUCTIONSPERMIN_KEY);
		checkKey(loadTest, LOADTEST_PROPERTIES_AUCTIONDURATION_KEY);
		checkKey(loadTest, LOADTEST_PROPERTIES_UPDATEINTERVALSEC_KEY);
		checkKey(loadTest, LOADTEST_PROPERTIES_BIDSPERMIN_KEY);

		return loadTest;
	}

	private Properties loadProperties(String fileName) throws IOException {
		InputStream is = ClassLoader.getSystemResourceAsStream(fileName);
		if (is == null)
			throw new IOException(fileName + " not found!");

		Properties prop = new Properties();
		try {
			try {
				prop.load(is);
			} finally {
				is.close();
			}
		} catch (IOException e) {
			throw new IOException("Couldn't load " + fileName + ":", e);
		}

		return prop;
	}

	private void checkKey(Properties prop, String key) throws IOException {
		if (prop == null)
			throw new IllegalArgumentException("properties is null");
		if (key == null)
			throw new IllegalArgumentException("key is null");

		if (!prop.containsKey(key)) {
			throw new IOException("Properties file doesn't contain the key "
					+ key);
		}
	}

}
