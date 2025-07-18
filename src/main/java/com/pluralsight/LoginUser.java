package com.pluralsight;

import java.util.Scanner;

public class LoginUser {
    private static final Scanner input = new Scanner(System.in);

    public static boolean userAuth() {
        System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                "                                 🔐 LOGIN REQUIRED                                    " +
                "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

        String correctUsername = "heldana";
        String correctPassword = "ledger123";
        int attempts = 0;

        while (attempts < 3) {
            String enteredUsername = askQuestion("Username: ");
            String enteredPassword = askQuestion("Password: ");

            if (enteredUsername.equals(correctUsername) && enteredPassword.equals(correctPassword)) {
                System.out.println("Login successful.");
                System.out.println("-----------------------------------------------------------------------------------\n");
                return true;
            } else {
                if (!enteredUsername.equals(correctUsername) && !enteredPassword.equals(correctPassword)) {
                    System.out.println("Both username and password are incorrect.\n");
                } else if (!enteredUsername.equals(correctUsername)) {
                    System.out.println("Username is incorrect.\n");
                } else {
                    System.out.println("Password is incorrect.\n");
                }
                attempts++;
            }
        }

        System.out.println("-----------------------------------------------------------------------------------\n");
        return false;
    }

    public static String askQuestion(String question) {
        System.out.print(question);
        return input.nextLine().trim();
    }
}
