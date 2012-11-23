/**
 * 
 */
package at.ac.tuwien.dslab2.service.billingServer;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author klaus
 * 
 */
public class BillingServerImpl implements BillingServer {
	private final String USERPROPERTIES_FILE = "user.properties";
	private final Map<String, String> users;

	public BillingServerImpl() throws IOException {

		// Get the users from the properties file
		InputStream is = ClassLoader
				.getSystemResourceAsStream(USERPROPERTIES_FILE);
		if (is == null)
			throw new IOException(USERPROPERTIES_FILE + " not found!");
		Properties prop = new Properties();
		try {
			try {
				prop.load(is);
			} finally {
				is.close();
			}
		} catch (IOException e) {
			throw new IOException("Couldn't load " + USERPROPERTIES_FILE, e);
		}
		users = new ConcurrentHashMap<String, String>(prop.size());
		for (Entry<Object, Object> entry : prop.entrySet()) {
			users.put(entry.getKey().toString(), entry.getValue().toString());
		}
	}

	@Override
	public BillingServerSecure login(String username, String password) {
		if (username == null || password == null)
			throw new IllegalArgumentException(
					"Either username or password is null");

		/*
		 * Check the username
		 */
		if (!users.containsKey(username.trim()))
			return null;

		/*
		 * Check the password
		 */
		// Generate the md5 hash of the password
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// We know that md5 is supported, and by the way I wonder why there
			// are no constants or enumerations for the algorithm...
		}
		BigInteger i = new BigInteger(1, md.digest(password.getBytes()));
		// String md5Hash = String.format("%032x", i); usually padding is not
		// necessary
		String md5Hash = String.format("%x", i);

		// Check password
		if (!md5Hash.equals(users.get(username.trim())))
			return null;

		// Generate the BillingServerSecure
		BillingServerSecure bss = BillingServerFactory.newBillingServerSecure();

		return bss;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

}
