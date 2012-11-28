/**
 * 
 */
package at.ac.tuwien.dslab2.presentation.analyticsServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import at.ac.tuwien.dslab2.service.analyticsServer.AnalyticsServerFactory;

/**
 * @author klaus
 * 
 */
public class AnalyticsServer {
	private static at.ac.tuwien.dslab2.service.analyticsServer.AnalyticsServer as;

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
			as = AnalyticsServerFactory.newAnalyticsServer(bindingName);
		} catch (IOException e) {
			error("Couldn't initialize server:", e);
		}
	}

	private static void usage() {
		System.err.println("usage: java AnalyticsServer bindingName\n");
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
			if (as != null) {
				as.close();
			}
		} catch (IOException e) {
			System.err.println("Something went wrong while closing:");
			e.printStackTrace();
		}
	}
}
