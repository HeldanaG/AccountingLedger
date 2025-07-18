package com.pluralsight;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class Ledger {
    static Scanner input = new Scanner(System.in);
    static ArrayList<Transaction> transactions = new ArrayList<>();

    // Method for Ledger Menu
    public static void ledgerMenu() {
        // Boolean to keep the app running until user chooses to exit
        boolean appRunning = true;
        // Loop to keep showing the menu until appRunning becomes false
        while (appRunning) {
            try {
                System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "                             📒 LEDGER MENU                                         \n" +
                        "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        " What would you like to do today?                                                   \n" +
                        " A - View All Transactions                                                           \n" +
                        " B - View Balance Summary                                                              \n" +
                        " D - View Deposits Only                                                                \n" +
                        " P - View Payments Only                                                               \n" +
                        " R - View Reports                                                                     \n" +
                        " H - Return to Home Menu                                                             \n" +
                        "-----------------------------------------------------------------------------------");

                // Read user's menu choice
                String choice = askQuestion("Please enter your choice (A/D/P/R/H): ").toUpperCase();

                switch (choice) {
                    case "A":
                        loadTransactions();
                        break;
                    case "B":
                        viewBalanceSummary();
                        break;
                    case "D":
                        viewDepositsOnly();
                        break;
                    case "P":
                        viewPaymentsOnly();
                        break;
                    case "R":
                        Report.reportsMenu();
                        break;
                    case "H":
                        appRunning = false; // Return to home
                        break;
                    default:
                        System.out.println("Invalid choice. Please select A, D, P, R, or H.");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please try again!");
                input.nextLine(); // Clear the buffer
            }
        }
    }
    // Displays all transactions
    public static void loadTransactions() {
        transactions.clear(); //  Clear old data before reloading
        try {
            System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"+
                    "                           📋 ALL TRANSACTIONS                                     " +
                    "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/transactions.csv"));
            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // skip empty lines
                }
                String[] fields = line.split("\\|");

                Transaction transactionFields = new Transaction(
                        LocalDate.parse(fields[0].trim()),
                        LocalTime.parse(fields[1].trim()),
                        fields[2].trim(),
                        fields[3].trim(),
                        Double.parseDouble(fields[4].trim())
                );
                transactions.add(transactionFields);
            }
            reader.close();
            System.out.println("Date        | Time     | Description               | Vendor               | Amount     | Type\n");

            // Sort transactions from newest to oldest (by date and time)
            Collections.sort(transactions, Comparator.comparing(
                    (Transaction t) -> LocalDateTime.of(t.getDate(), t.getTime())
            ).reversed());

            for (Transaction transaction : transactions) {
                System.out.println(transaction.toString());
            }
            System.out.println("-----------------------------------------------------------------------------------\n");

        } catch (FileNotFoundException e) {
            System.out.println("No existing transactions found. Starting fresh!");
        } catch (IOException e) {
            System.out.println("Error reading transactions file: " + e.getMessage());
        }
    }
    // Displays total deposits, total payments, and the current balance
    public static void viewBalanceSummary() {
        // Section header
        System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                "                                 💰 BALANCE SUMMARY                                   " +
                "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

        // Load the most up-to-date list of transactions
        loadTransactionForReportOnly();

        double totalDeposits = 0;
        double totalPayments = 0;

        // Loop through transactions and calculate totals
        for (Transaction t : transactions) {
            if (t.getAmount() > 0) {
                totalDeposits += t.getAmount();
            } else {
                totalPayments += t.getAmount(); // Already negative
            }
        }

        // Compute current balance
        double balance = totalDeposits + totalPayments;

        // Display the summary in formatted rows
        System.out.printf("Total Deposits:     $%.2f\n", totalDeposits);
        System.out.printf("Total Payments:     $%.2f\n", totalPayments);
        System.out.printf("Current Balance:    $%.2f\n", balance);

        System.out.println("-----------------------------------------------------------------------------------\n");
    }
    // Displays all deposits in the Transaction
    private static void viewDepositsOnly() {
        System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"+
                "                                   💵 ALL DEPOSITS                                     " +
                "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        // Load a fresh copy of transactions
        loadTransactionForReportOnly();

        // Sort transactions from newest to oldest (by date and time)
        Collections.sort(transactions, Comparator.comparing(
                (Transaction t) -> LocalDateTime.of(t.getDate(), t.getTime())
        ).reversed());

        for (Transaction transaction : transactions) {
            if (transaction.getAmount() > 0) {
                System.out.println(transaction.toString());
            }
        }
        System.out.println("-----------------------------------------------------------------------------------\n");

    }
    // Displays all Payments in the Transaction
    private static void viewPaymentsOnly() {
        System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                "                                 🧾 ALL PAYMENTS                                      " +
                "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

        // Load a fresh copy of transactions
        loadTransactionForReportOnly();

        // Sort transactions from newest to oldest (by date and time)
        Collections.sort(transactions, Comparator.comparing(
                (Transaction t) -> LocalDateTime.of(t.getDate(), t.getTime())
        ).reversed());

        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
                System.out.println(transaction.toString());
            }
        }
        System.out.println("-------------------------------------------------------------------------------------\n");
    }
    // will load current contents of the transaction for reports only
    public static void loadTransactionForReportOnly(){
        transactions.clear(); //  Clear old data before reloading
        try {

            BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/transactions.csv"));
            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // skip empty lines
                }
                String[] fields = line.split("\\|");

                Transaction transactionFields = new Transaction(
                        LocalDate.parse(fields[0].trim()),
                        LocalTime.parse(fields[1].trim()),
                        fields[2].trim(),
                        fields[3].trim(),
                        Double.parseDouble(fields[4].trim())
                );
                transactions.add(transactionFields);
            }
            reader.close();

        } catch (FileNotFoundException e) {
            System.out.println("No existing transactions found. Starting fresh!");
        } catch (IOException e) {
            System.out.println("Error reading transactions file: " + e.getMessage());
        }

    }
    public static String askQuestion(String question){
        System.out.print(question);
        String answer =input.nextLine();
        return answer.trim();
    }
}
