package at.ac.tuwien.dslab2.presentation.loadTest;

import at.ac.tuwien.dslab2.service.loadTest.LoadTestService;
import at.ac.tuwien.dslab2.service.loadTest.LoadTestServiceFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class LoadTest {

    private static LoadTestService loadTestService;

    public static void main(String[] args) {

            initialize(args);
            readInput();

            close();

    }

    private static void initialize(String[] args) {
        Scanner sc;

        if (args.length != 4)
            usage();

        String serverHost = args[0];
        sc = new Scanner(args[1]);
        if (!sc.hasNextInt())
            usage();
        int serverPort = sc.nextInt();
        String analyticsBindingName = args[2];
        String billingBindingName = args[3];

        try {
            loadTestService = LoadTestServiceFactory.newLoadTest(serverPort, billingBindingName, analyticsBindingName, serverHost);
            loadTestService.start();
        } catch (IOException e) {
            System.err.println("Error while connecting:");
            e.printStackTrace();

            close();
            System.exit(1);
        }
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

    private static void usage() {
        System.err.println("usage: java LoadTest serverHost serverPort analyticsBindingName billingBindingName"
                + " \n\n"
                + "\tserverHost: host name or IP of the auction server\n"
                + "\tserverPort: TCP connection port on which the "
                + "auction server will receive incoming messages "
                + "(commands) from clients.\n"
                + "\tanalyticsBindingName: the binding name of the "
                + "analytics server in the RMI registry\n"
                + "\tbillingBindingName: the binding name of the "
                + "billing server in the RMI registry");

        close();
        System.exit(0);
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

    synchronized static void close() {
        if (loadTestService != null) {
            try {
                loadTestService.close();
            } catch (IOException e) {
                System.err.println("Something went wrong while closing:");
                e.printStackTrace();
            }
        }
    }
}
