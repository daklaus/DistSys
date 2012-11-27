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

}
