package com.pluralsight;


import java.util.Scanner;

public class AccountingLedgerApp {
    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {

        mainMenu();
    }

    public static void mainMenu() {
        // Boolean to keep the app running until user chooses to exit
        boolean appRunning = true;
        // Loop to keep showing the menu until appRunning becomes false
        while (appRunning) {
            try {
                System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                                    "            💰 WELCOME TO ACCOUNTING LEDGER APPLICATION 💰                        \n" +
                                    "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                                    " What would you like to do today?                                                   \n" +
                                    " D️ -  Add Deposit                                                                   \n" +
                                    " P️ -  Make Payment                                                                   \n" +
                                    " L️ -  Ledger (View all your transactions)                                            \n" +
                                    " X️ -  Exit the application                                                           \n" +
                                    "-------------------------------------------------------------------------------------");
                System.out.print("Please enter your choice: ");

                // Read user's menu choice
                String menuChoice = input.nextLine().trim().toUpperCase();

                // Handle each choice accordingly using switch
                switch (menuChoice) {
                    case "D":
                        addDeposit();
                        break;
                    case "P":
                        makePayment();
                        break;
                    case "L":
                        ledgerMenu();
                        break;
                    case "X":
                        // Exit the program by making appRunning false
                        System.out.println("Thank you for using the Accounting Ledger. Goodbye!");
                        appRunning = false;
                        break;
                    default:
                        // returns back if user put invalid choice number
                        System.out.println("Invalid choice! Please select D, P, L, or X.");
                        break;
                }
                // catches and returns back if user put invalid input
            } catch (Exception e) {
                System.out.println("Invalid input! Please try again.");
                input.nextLine(); // clear the buffer

            }
        }

    }

    // Method for Add Deposit
    private static void addDeposit() {
        System.out.println("\n[Add Deposit feature will be implemented here]");
    }

    // Method for Make Payment
    private static void makePayment() {
        System.out.println("\n[Make Payment feature will be implemented here]");
    }

    // Method for Ledger Menu
    private static void ledgerMenu() {
        System.out.println("\n[Ledger Menu will be implemented here]");
    }
}
