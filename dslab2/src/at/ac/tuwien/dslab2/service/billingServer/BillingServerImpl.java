/**
 * 
 */
package at.ac.tuwien.dslab2.service.billingServer;

import java.io.IOException;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import at.ac.tuwien.dslab2.service.PropertiesService;
import at.ac.tuwien.dslab2.service.PropertiesServiceFactory;
import at.ac.tuwien.dslab2.service.rmi.RMIServerService;
import at.ac.tuwien.dslab2.service.rmi.RMIServiceFactory;

/**
 * @author klaus
 * 
 */
class BillingServerImpl implements BillingServer {
	private final RMIServerService rss;
	private final Map<String, String> users;
	private final BillingServerSecure bss;

	public BillingServerImpl(String bindingName) throws IOException {
		if (bindingName == null)
			throw new IllegalArgumentException("bindingName is null");

		/*
		 * Read the registry properties file
		 */
		Properties prop = null;
		prop = PropertiesServiceFactory.getPropertiesService()
				.getRegistryProperties();

		// Parse value
		Scanner sc = new Scanner(
				prop.getProperty(PropertiesService.REGISTRY_PROPERTIES_PORT_KEY));
		sc.useLocale(Locale.US);
		if (!sc.hasNextInt()) {
			throw new IOException("Couldn't parse the properties value of "
					+ PropertiesService.REGISTRY_PROPERTIES_PORT_KEY);
		}
		int port = sc.nextInt();

		/*
		 * Bind the RMI interface
		 */
		this.rss = RMIServiceFactory.newRMIServerService(port);
		this.rss.bind(bindingName, this);

		// Get the users from the properties file
		prop = PropertiesServiceFactory.getPropertiesService()
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
	public BillingServerSecure login(String username, String password)
			throws RemoteException {
		if (username == null || password == null)
			throw new IllegalArgumentException(
					"Either username or password is null");

		/*
		 * Check the username
		 */
		if (!users.containsKey(username.trim()))
			throw new RemoteException("User " + username.trim()
					+ " doesn't exist");

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
			throw new RemoteException("Wrong password for user "
					+ username.trim());

		return bss;
	}

	@Override
	public void close() throws IOException {
		if (rss != null) {
			rss.close();
		}
		bss.close();
		UnicastRemoteObject.unexportObject(bss, false);
	}

}
