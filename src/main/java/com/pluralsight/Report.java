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

public class Report {
    static Scanner input = new Scanner(System.in);
    static ArrayList<Transaction> transactions = new ArrayList<>();

    // Method for Report Menu
    public static void reportsMenu() {
        // Boolean to keep the app running until user chooses to exit
        boolean appRunning = true;
        // Loop to keep showing the menu until appRunning becomes false
        while (appRunning) {
            try {
                System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "                             📑 REPORTS MENU                                   \n" +
                        "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        " What would you like to do today?                                                   \n" +
                        " 1 - Month To Date                                                                   \n" +
                        " 2 - Previous Month                                                                  \n" +
                        " 3 - Year To Date                                                                    \n" +
                        " 4 - Previous Year                                                                   \n" +
                        " 5 - Search by Vendor                                                                \n" +
                        " 6 - Custom Report                                              \n" +
                        " 0 - Back to Ledger Menu                                                             \n" +
                        " H - Return to Home Menu                                                             \n" +
                        "-----------------------------------------------------------------------------------");
                String choice = askQuestion("Enter your choice (1-6, 0, H): ").toUpperCase();

                switch (choice) {
                    case "1":
                        monthToDateReport();
                        break;
                    case "2":
                        previousMonthReport();
                        break;
                    case "3":
                        yearToDateReport();
                        break;
                    case "4":
                        previousYearReport();
                        break;
                    case "5":
                        searchByVendor();
                        break;
                    case "6":
                        customSearchReport();
                        break;
                    case "0":
                        appRunning = false; // Go back to Ledger Menu
                        break;
                    case "H":
                        AccountingLedgerApp.mainMenu(); // Return to Home Menu
                        break;
                    default:
                        System.out.println("Invalid choice. Please select 1-6, 0, or H.");
                        break;
                }
            } catch (Exception e) {
                System.out.println("An error occurred. Please try again!");
                input.nextLine(); // Clear buffer
            }
        }
    }
    // Displays all transactions that occurred in the current month and year
    public static void monthToDateReport() {
        // Display the report heading
        System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"+
                "                             📆 MONTH TO DATE REPORT                                \n"+
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

        // Load latest transactions from file
        loadTransactionForReportOnly();
        // Get today's date
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        boolean anyFound = false; // Track if any transactions were found

        System.out.println("Date        | Time     | Description               | Vendor               | Amount     | Type\n");

        // Sort transactions from newest to oldest (by date and time)
        Collections.sort(transactions, Comparator.comparing(
                (Transaction t) -> LocalDateTime.of(t.getDate(), t.getTime())
        ).reversed());

        // Loop through all transactions stored in memory
        for (Transaction transaction : transactions) {
            try {

                LocalDate transactionDate = transaction.getDate();
                // Check if the transaction's year and month match today's year and month
                if (transactionDate.getYear() == currentYear &&
                        transactionDate.getMonthValue() == currentMonth) {

                    // Print the transaction details
                    System.out.println(transaction.toString());
                    anyFound = true; // At least one transaction found

                }

            } catch (Exception e) {
                // Handle any parsing errors without crashing the program
                System.out.println("Skipping an invalid transaction date: " + transaction.getDate());
            }
        }
        // If no transactions found, inform the user
        if (!anyFound) {
            System.out.println("No transactions found for this month.");
        }
        System.out.println("-------------------------------------------------------------------------------------\n");
    }
    // Displays all transactions that occurred in the previous month and current year
    public static void previousMonthReport() {
        // Print report header
        System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"+
                "                             📆 PREVIOUS MONTH REPORT                                \n"+
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

        // Load transactions without printing to avoid duplicate output
        loadTransactionForReportOnly();

        // Get today's date and calculate the previous month
        LocalDate today = LocalDate.now();
        LocalDate previousMonth = today.minusMonths(1);

        // Extract the target year and month
        int targetYear = previousMonth.getYear();
        int targetMonth = previousMonth.getMonthValue();

        boolean anyFound = false; // Tracks whether any transactions were found

        System.out.println("Date        | Time     | Description               | Vendor               | Amount     | Type\n");

        // Sort transactions from newest to oldest (by date and time)
        Collections.sort(transactions, Comparator.comparing(
                (Transaction t) -> LocalDateTime.of(t.getDate(), t.getTime())
        ).reversed());

        // Loop through all loaded transactions
        for (Transaction transaction : transactions) {
            try {
                // Get the transaction's date
                LocalDate transactionDate = transaction.getDate();

                // Check if it matches the previous month and year
                if (transactionDate.getYear() == targetYear &&
                        transactionDate.getMonthValue() == targetMonth) {

                    // Print matching transaction
                    System.out.println(transaction.toString());
                    anyFound = true;
                }

            } catch (Exception e) {
                // Catch and report errors but continue
                System.out.println("Skipping invalid transaction: " + e.getMessage());
            }
        }
        // Inform user if no results found
        if (!anyFound) {
            System.out.println("No transactions found for the previous month.");
        }
        System.out.println("-------------------------------------------------------------------------------------\n");
    }
    // Displays all transactions that occurred in the current year
    public static void yearToDateReport() {
        // Display the report header
        System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"+
                "                             📆 YEAR TO DATE REPORT                                 \n"+
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

        // Load fresh transaction data
        loadTransactionForReportOnly();

        // Get today's date
        LocalDate today = LocalDate.now();

        // Get the first day of the current year (January 1st)
        LocalDate startOfYear = LocalDate.of(today.getYear(), 1, 1);

        boolean anyFound = false; // Flag to track if matching transactions are found

        System.out.println("Date        | Time     | Description               | Vendor               | Amount     | Type\n");

        // Sort transactions from newest to oldest (by date and time)
        Collections.sort(transactions, Comparator.comparing(
                (Transaction t) -> LocalDateTime.of(t.getDate(), t.getTime())
        ).reversed());

        // Loop through all transactions in memory
        for (Transaction transaction : transactions) {
            try {
                LocalDate transactionDate = transaction.getDate();

                // Check if transaction date is within start of year and today (inclusive)
                if ((transactionDate.isEqual(startOfYear) || transactionDate.isAfter(startOfYear)) &&
                        (transactionDate.isBefore(today) || transactionDate.isEqual(today))) {

                    // Print matching transaction
                    System.out.println(transaction.toString());
                    anyFound = true;
                }

            } catch (Exception e) {
                // Catch any parsing or date logic errors without crashing
                System.out.println("Skipping transaction due to error: " + e.getMessage());
            }
        }

        // If no matching entries were found, let the user know
        if (!anyFound) {
            System.out.println("No transactions found for this year up to today.");
        }
        System.out.println("-------------------------------------------------------------------------------------\n");
    }
    // Displays all transactions that occurred in the previous year
    public static void previousYearReport() {
        // Print the section header
        System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"+
                "                             📆 PREVIOUS YEAR REPORT                                 \n"+
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

        // Load a fresh copy of transactions
        loadTransactionForReportOnly();

        // Get today's date and determine last year
        LocalDate today = LocalDate.now();
        int previousYear = today.getYear() - 1;

        // Define the range: Jan 1st to Dec 31st of last year
        LocalDate startDate = LocalDate.of(previousYear, 1, 1);
        LocalDate endDate = LocalDate.of(previousYear, 12, 31);

        boolean anyFound = false; // Used to track if any match is found

        System.out.println("Date        | Time     | Description               | Vendor               | Amount     | Type\n");

        // Sort transactions from newest to oldest (by date and time)
        Collections.sort(transactions, Comparator.comparing(
                (Transaction t) -> LocalDateTime.of(t.getDate(), t.getTime())
        ).reversed());

        // Loop through all transactions in memory
        for (Transaction transaction : transactions) {
            try {
                LocalDate transactionDate = transaction.getDate();

                // Check if the transaction date falls within the previous year range
                if ((transactionDate.isEqual(startDate) || transactionDate.isAfter(startDate)) &&
                        (transactionDate.isBefore(endDate) || transactionDate.isEqual(endDate))) {

                    // Display the transaction
                    System.out.println(transaction.toString());
                    anyFound = true;
                }

            } catch (Exception e) {
                // Skip problematic rows gracefully
                System.out.println("Skipping due to date error: " + e.getMessage());
            }
        }

        // If nothing was found, notify the user
        if (!anyFound) {
            System.out.println("No transactions found for the previous year.");
        }
        System.out.println("-------------------------------------------------------------------------------------\n");

    }
    // Displays all transactions by receiving vendor from user
    public static void searchByVendor() {
        // Print the section header
        System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"+
                "                            🔍 SEARCH BY VENDOR                                   \n"+
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

        // Load the latest transaction data into memory
        loadTransactionForReportOnly();

        // Ask the user for a vendor name
        String searchVendor = askQuestion("Enter vendor name to search: ").toLowerCase();

        boolean anyFound = false; // Track if any match is found

        System.out.println("Date        | Time     | Description               | Vendor               | Amount     | Type\n");

        // Sort transactions from newest to oldest (by date and time)
        Collections.sort(transactions, Comparator.comparing(
                (Transaction t) -> LocalDateTime.of(t.getDate(), t.getTime())
        ).reversed());

        // Loop through all transactions in memory
        for (Transaction transaction : transactions) {
            try {
                // Convert vendor name to lowercase for case-insensitive search
                String transactionVendor = transaction.getVendor().toLowerCase();

                // If the vendor name contains the user's input, display it
                if (transactionVendor.contains(searchVendor)) {
                    System.out.println(transaction.toString());
                    anyFound = true;
                }

            } catch (Exception e) {
                System.out.println("Skipping due to vendor error: " + e.getMessage());
            }
        }
        // If no matches were found, notify the user
        if (!anyFound) {
            System.out.println("No transactions found for vendor: " + searchVendor);
        }
        System.out.println("-------------------------------------------------------------------------------------\n");

    }
    // Displays all transactions by receiving customize input from user
    public static void customSearchReport() {
        System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"+
                "                        🛠️ CUSTOM SEARCH REPORT (CHALLENGE)                       \n"+
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");


        loadTransactionForReportOnly();

        // Prompt user for filters (can skip any field)
        String startDateInput = askQuestion("Enter start date (yyyy-MM-dd) or leave blank: ");
        String endDateInput = askQuestion("Enter end date (yyyy-MM-dd) or leave blank: ");
        String descInput = askQuestion("Enter a description to search or leave blank: ").toLowerCase();
        String vendorInput = askQuestion("Enter vendor name to search or leave blank: ").toLowerCase();
        String amountInput = askQuestion("Enter amount to search or leave blank: ");

        boolean anyFound = false;
        System.out.println("\nDate        | Time     | Description               | Vendor               | Amount     | Type\n");

        // Sort transactions from newest to oldest (by date and time)
        Collections.sort(transactions, Comparator.comparing(
                (Transaction t) -> LocalDateTime.of(t.getDate(), t.getTime())
        ).reversed());

        for (Transaction transaction : transactions) {
            try {
                // Initial assumptions: all conditions match
                boolean matches = true;

                // Check each field only if user provided a value
                if (!startDateInput.isEmpty()) {
                    LocalDate startDate = LocalDate.parse(startDateInput);
                    if (transaction.getDate().isBefore(startDate)) {
                        matches = false;
                    }
                }

                if (!endDateInput.isEmpty()) {
                    LocalDate endDate = LocalDate.parse(endDateInput);
                    if (transaction.getDate().isAfter(endDate)) {
                        matches = false;
                    }
                }

                if (!descInput.isEmpty() && !transaction.getDescription().toLowerCase().contains(descInput)) {
                    matches = false;
                }

                if (!vendorInput.isEmpty() && !transaction.getVendor().toLowerCase().contains(vendorInput)) {
                    matches = false;
                }

                if (!amountInput.isEmpty()) {
                    double amountSearch = Double.parseDouble(amountInput);
                    if (transaction.getAmount() != amountSearch) {
                        matches = false;
                    }
                }

                // If all matched conditions are true, display the transaction
                if (matches) {
                    System.out.println(transaction.toString());
                    anyFound = true;
                }

            } catch (Exception e) {
                System.out.println("Skipping entry due to input or filter error: " + e.getMessage());
            }
        }

        if (!anyFound) {
            System.out.println("No matching transactions found based on your filters.");
        }
        System.out.println("-------------------------------------------------------------------------------------\n");
    }
    public static String askQuestion(String question){
        System.out.print(question);
        String answer =input.nextLine();
        return answer.trim();
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
}
