package at.ac.tuwien.dslab3.presentation;

import org.bouncycastle.openssl.PasswordFinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PasswordFinderImpl implements PasswordFinder {
    @Override
    public char[] getPassword() {
        System.out.println("Enter pass phrase:");
        try {
            return new BufferedReader(new InputStreamReader(System.in)).readLine().toCharArray();
        } catch (IOException e) {
            System.err.println("Something went wrong while reading password !");
        }
        return new char[0];
    }

}
