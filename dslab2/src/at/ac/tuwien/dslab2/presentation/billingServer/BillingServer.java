/**
 * 
 */
package at.ac.tuwien.dslab2.presentation.billingServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Scanner;

import at.ac.tuwien.dslab2.service.billingServer.BillingServerFactory;
import at.ac.tuwien.dslab2.service.rmi.RMIServerService;
import at.ac.tuwien.dslab2.service.rmi.RMIServiceFactory;

/**
 * @author klaus
 * 
 */
public class BillingServer {
	private static final String REGISTRY_PROPERTIES_FILE = "registry.properties";
	private static final String REGISTRY_PROPERTIES_PORT_KEY = "registry.port";
	private static at.ac.tuwien.dslab2.service.billingServer.BillingServer bs;
	private static RMIServerService rss;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			initialize(args);
			readInput();
		} finally {
			close();
		}
	}

	private static void initialize(String[] args) {
		String bindingName;
		int port;

		/*
		 * Process arguments
		 */
		Scanner sc;
		if (args.length != 1)
			usage();

		sc = new Scanner(args[0]);
		if (!sc.hasNext())
			usage();
		bindingName = sc.next();

		/*
		 * Read the properties file
		 */
		InputStream is = ClassLoader
				.getSystemResourceAsStream(REGISTRY_PROPERTIES_FILE);
		if (is == null) {
			error(REGISTRY_PROPERTIES_FILE + " not found!");
		}
		Properties prop = new Properties();
		try {
			try {
				prop.load(is);
			} finally {
				is.close();
			}
		} catch (IOException e) {
			error("Couldn't load " + REGISTRY_PROPERTIES_FILE + ":", e);
		}

		// Check if key exists
		if (!prop.containsKey(REGISTRY_PROPERTIES_PORT_KEY)) {
			error("Properties file doesn't contain the key "
					+ REGISTRY_PROPERTIES_PORT_KEY);
		}

		// Parse value
		sc = new Scanner(prop.getProperty(REGISTRY_PROPERTIES_PORT_KEY));
		if (!sc.hasNextInt()) {
			error("Couldn't parse the properties value of "
					+ REGISTRY_PROPERTIES_PORT_KEY);
		}
		port = sc.nextInt();

		/*
		 * Bind the RMI interface
		 */
		try {
			bs = BillingServerFactory.newBillingServer();
			rss = RMIServiceFactory.newRMIServerService(port);
			rss.bind(bindingName, bs);
		} catch (IOException e) {
			error("Couldn't initialize server:", e);
		}
	}

	private static void usage() {
		System.err.println("usage: java BillingServer bindingName\n");
		System.err.println("bindingName: binding name of the servers "
				+ "interface in the RMI registry\n");

		close();
		System.exit(0);
	}

	private static void readInput() {
		System.out.println("--- press Enter to exit ---");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		try {
			br.readLine();
		} catch (IOException e) {
			System.err.println("Something went wrong while reading input:");
			e.printStackTrace();
		}
	}

	private static void error(String msg) {
		error(msg, null);
	}

	private static void error(String msg, Throwable e) {
		System.err.println(msg);
		if (e != null)
			e.printStackTrace();
		close();
		System.exit(1);
	}

	private static void close() {
		try {
			if (rss != null) {
				rss.close();
			}
			if (bs != null) {
				bs.close();
			}
		} catch (IOException e) {
			System.err.println("Something went wrong while closing:");
			e.printStackTrace();
		}
	}
}
