/**
 * 
 */
package at.ac.tuwien.dslab2.service.billingServer;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import at.ac.tuwien.dslab2.service.PropertiesServiceFactory;

/**
 * @author klaus
 * 
 */
public class BillingServerImpl implements BillingServer {
	private final Map<String, String> users;
	private final BillingServerSecure bss;

	public BillingServerImpl() throws IOException {

		// Get the users from the properties file
		Properties prop = PropertiesServiceFactory.getPropertiesService()
				.getUserProperties();

		users = new ConcurrentHashMap<String, String>(prop.size());
		for (Entry<Object, Object> entry : prop.entrySet()) {
			users.put(entry.getKey().toString(), entry.getValue().toString());
		}

		// Get the BillingServerSecure
		bss = BillingServerFactory.getBillingServerSecure();
		UnicastRemoteObject.exportObject(bss, 0);
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

		return bss;
	}

	@Override
	public void close() throws IOException {
		bss.close();
		UnicastRemoteObject.unexportObject(bss, false);
	}

}
