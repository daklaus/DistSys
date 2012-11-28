/**
 * 
 */
package at.ac.tuwien.dslab2.presentation.billingServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import at.ac.tuwien.dslab2.service.billingServer.BillingServerFactory;

/**
 * @author klaus
 * 
 */
public class BillingServer {
	private static at.ac.tuwien.dslab2.service.billingServer.BillingServer bs;

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
		/*
		 * Process arguments
		 */
		if (args.length != 1)
			usage();

		String bindingName = args[0];

		try {
			bs = BillingServerFactory.newBillingServer(bindingName);
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

	private static void error(String msg, Throwable e) {
		System.err.println(msg);
		if (e != null)
			e.printStackTrace();
		close();
		System.exit(1);
	}

	private static void close() {
		try {
			if (bs != null) {
				bs.close();
			}
		} catch (Exception e) {
			System.err.println("Something went wrong while closing:");
			e.printStackTrace();
		}
	}
}
