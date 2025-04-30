package com.pluralsight;


import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.lang.*;

public class AccountingLedgerApp {
    static Scanner input = new Scanner(System.in);
    static DateTimeFormatter currentTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    static LocalDateTime currentTime = LocalDateTime.now();
    static String formatedCurrentTime = currentTime.format(currentTimeFormatter);
    static ArrayList<Transaction> transactions = new ArrayList<>();


    public static void main(String[] args) {
        try {
            if (authenticateUser()) {
                createFileWithHeader();
                mainMenu();
            } else {
                System.out.println("Too many failed attempts. Exiting application.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Prompts the user for a username and password (3 attempts max)
    public static boolean authenticateUser() {
        // Section header
        System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                            "                                 🔐 LOGIN REQUIRED                                    " +
                            "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

        String correctUsername = "heldana";
        String correctPassword = "ledger123";
        int attempts = 0;

        // Allow up to 3 login attempts
        while (attempts < 3) {
            // Prompt for username
            String enteredUsername = askQuestion("Username: ");

            String enteredPassword;

            // Attempt to use hidden password input if possible
            Console console = System.console();
            if (console != null) {
                char[] passwordChars = console.readPassword("Password: ");
                enteredPassword = new String(passwordChars);
            } else {

                enteredPassword = askQuestion("Password: ");
            }

            // Check login credentials
            if (enteredUsername.equals(correctUsername) && enteredPassword.equals(correctPassword)) {
                System.out.println("Login successful.");
                System.out.println("-----------------------------------------------------------------------------------\n");
                return true;
            } else {
                // Specific feedback
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

        // Section footer
        System.out.println("-----------------------------------------------------------------------------------\n");
        return false;
    }

    public static void mainMenu() {
        // Boolean to keep the app running until user chooses to exit
        boolean appRunning = true;
        // Loop to keep showing the menu until appRunning becomes false
        while (appRunning) {
            try {
                System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                                    "            💰 WELCOME TO ACCOUNTING LEDGER APPLICATION 💰                        \n" +
                                    "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                                    " What would you like to do today?                                                   \n" +
                                    " D️ -  Add Deposit                                                                   \n" +
                                    " P️ -  Make Payment                                                                   \n" +
                                    " L️ -  Ledger (View transactions)                                            \n" +
                                    " X️ -  Exit the application                                                           \n" +
                                    "-----------------------------------------------------------------------------------");

                // Read user's menu choice
                String menuChoice = askQuestion("Please enter your choice: ").toUpperCase();


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
    public static void addDeposit() {
        // Boolean to keep the app running until user chooses to exit
        boolean appRunning = true;
        // Loop to keep showing the menu until appRunning becomes false
        while (appRunning) {
            try {
                System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"+
                                    "                                🏦 ADD A NEW DEPOSIT                             \n"+
                                    "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");


                // Refresh current time
                currentTime = LocalDateTime.now();
                formatedCurrentTime = currentTime.format(currentTimeFormatter);

                // split current time and date separatly so that we can write it to the file
                String[] dateTimeParts = formatedCurrentTime.split(" ");
                String date = dateTimeParts[0].trim();
                String time = dateTimeParts[1].trim();

                // Prompt user for deposit details Prompt user
                String description = capitalizeWords(askQuestion("Enter some Description about the deposit: "));
                while (description.isEmpty()) {
                    description = capitalizeWords(askQuestion("Description cannot be empty. Enter description again: "));
                }

                // Prompt user for vendor name and validate input
                String vendor = capitalizeWords(askQuestion("Enter vendor: "));
                while (vendor.isEmpty()) {
                    vendor = capitalizeWords(askQuestion("Vendor cannot be empty. Enter vendor again: "));
                }

                // Prompt user for deposit amount and validate input
                double amount = Double.parseDouble(askQuestion("Enter deposit amount: "));
                while (amount <= 0) {
                    amount = Double.parseDouble(askQuestion("Amount must be positive. Enter deposit amount: "));
                }

                // Create new entry line
                String newEntry = String.format("%-12s | %-8s | %-25s | %-19s | %-10.2f",
                        date, time, description, vendor, amount);

                // Read existing lines
                BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/transactions.csv"));
                String header = reader.readLine(); // preserve header

                //You're creating a new StringBuilder to hold all the content that will be written to the file again.
                StringBuilder updatedContent = new StringBuilder(header).append("\n").append(newEntry).append("\n");

                //Now we start reading the rest of the file (old transactions).
                //Each one is added after the new entry.
                //So we’re building a complete new version of the file in memory with this order
                String line;
                while ((line = reader.readLine()) != null) {
                    updatedContent.append(line).append("\n");
                }
                reader.close();

                // Rewrite file
                BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/transactions.csv"));
                //That line replaces the file contents with the full updatedContent.
                writer.write(updatedContent.toString());
                writer.close();

                // Add to memory list
                Transaction newDeposit = new Transaction(LocalDate.parse(date), LocalTime.parse(time), description, vendor, amount);
                transactions.add(0, newDeposit); // add to top of list

                System.out.println("\nDeposit added successfully!");

                // Ask if the user wants to add another deposit and validate input
                String depositeAgain =askQuestion("Would you like to add another deposit? (y/n): ");

                if (depositeAgain.equalsIgnoreCase("y")) {
                    continue;
                }else if (depositeAgain.equalsIgnoreCase("n")) {
                    appRunning = false;
                } else {
                    depositeAgain = askQuestion("Invalid Input! Please Enter y or n: ");

                    while (!(depositeAgain.equalsIgnoreCase("y") || depositeAgain.equalsIgnoreCase("n"))) {
                        depositeAgain = askQuestion("Invalid Input! Please Enter y or n: ");
                        if (depositeAgain.equalsIgnoreCase("y")) {
                            continue;
                            // If user doesn't want to add again, stop the loop
                        } else {
                            appRunning = false;
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println("An error occurred. Please try again!");
                input.nextLine(); // clear the buffer
            }
        }
    }

    // Method for Make Payment
    public static void makePayment() {
        // Boolean to keep the app running until user chooses to exit
        boolean appRunning = true;
        // Loop to keep showing the menu until appRunning becomes false
        while (appRunning) {
            try {
                System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"+
                                    "                                🧾 MAKE A PAYMENT                                \n"+
                                    "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                // Refresh current time
                currentTime = LocalDateTime.now();
                formatedCurrentTime = currentTime.format(currentTimeFormatter);

                String[] dateTimeParts = formatedCurrentTime.split(" ");
                String date = dateTimeParts[0].trim();
                String time = dateTimeParts[1].trim();

                // Prompt user for payment details
                String description = capitalizeWords(askQuestion("Enter some Description about the payment: "));
                while (description.isEmpty()) {
                    description = capitalizeWords(askQuestion("Description cannot be empty. Enter description again: "));
                }

                String vendor = capitalizeWords(askQuestion("Enter vendor: "));
                while (vendor.isEmpty()) {
                    vendor = capitalizeWords(askQuestion("Vendor cannot be empty. Enter vendor again: "));
                }

                double amount = Double.parseDouble(askQuestion("Enter payment amount: "));
                while (amount <= 0) {
                    amount = Double.parseDouble(askQuestion("Payment amount can't be zero. Enter payment amount: "));
                }

                // Make amount negative for payment
                amount = -Math.abs(amount);

                // Create new entry line
                String newEntry = String.format("%-12s | %-8s | %-25s | %-19s | %-9.2f",
                        date, time, description, vendor, amount);

                // Read existing lines
                BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/transactions.csv"));
                String header = reader.readLine(); // preserve header
                //You're creating a new StringBuilder to hold all the content that will be written to the file again.
                StringBuilder updatedContent = new StringBuilder(header).append("\n").append(newEntry).append("\n");

                //Now we start reading the rest of the file (old transactions).
                //Each one is added after the new entry.
                //So we’re building a complete new version of the file in memory with this order
                String line;
                while ((line = reader.readLine()) != null) {
                    updatedContent.append(line).append("\n");// maintain their order
                }
                reader.close();

                // Rewrite file
                BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/transactions.csv"));
                //That line replaces the file contents with the full updatedContent.
                writer.write(updatedContent.toString());
                writer.close();

                // Add to memory list
                Transaction newPayment = new Transaction(LocalDate.parse(date), LocalTime.parse(time), description, vendor, amount);
                transactions.add(0, newPayment); // add to top of list

                System.out.println("\nPayment added successfully!");

                // Ask if the user wants to make another payment
                String payAgain = askQuestion("Would you like to make another payment? (y/n): ");

                if (payAgain.equalsIgnoreCase("y")) {
                    continue;
                } else if (payAgain.equalsIgnoreCase("n")) {
                    appRunning = false;
                } else {
                    payAgain = askQuestion("Invalid Input! Please Enter y or n: ");

                    while (!(payAgain.equalsIgnoreCase("y") || payAgain.equalsIgnoreCase("n"))) {
                        payAgain = askQuestion("Invalid Input! Please Enter y or n: ");
                        if (payAgain.equalsIgnoreCase("y")) {
                            continue;
                            // If user doesn't want to add again, stop the loop
                        } else {
                            appRunning = false;
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println("An error occurred. Please try again!");
                input.nextLine(); // clear the buffer
            }
        }
    }

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
                        reportsMenu();
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
                        mainMenu(); // Return to Home Menu
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

        // Load fresh data from file
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

    // will load current contents of the transaction fror reports only
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
    // check if file has a header if not create header for the file only
    public static void createFileWithHeader() {
        try {
            String filePath = "src/main/resources/transactions.csv";
            File file = new File(filePath);

            if (!file.exists()) {
                // If file doesn't exist, create it and write header
                BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
                writer.write(String.format("%-12s | %-8s | %-25s | %-20s | %-10s",
                        "Date", "Time", "Description", "Vendor", "Amount"));
                writer.newLine();
                writer.close();
            } else {
                // If file exists, check if it's empty
                BufferedReader reader = new BufferedReader(new FileReader(filePath));
                String firstLine = reader.readLine();
                reader.close();

                if (firstLine == null || firstLine.isEmpty()) {
                    // File is empty, so write header
                    BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
                    writer.write(String.format("%-12s | %-8s | %-25s | %-20s | %-10s",
                            "Date", "Time", "Description", "Vendor", "Amount"));
                    writer.newLine();
                    writer.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Error checking or creating transaction file: " + e.getMessage());
        }
    }
    // capitalize each input that made to the file
    public static String capitalizeWords(String input) {
        String[] words = input.trim().toLowerCase().split("\\s+");  // Split by spaces
        StringBuilder capitalized = new StringBuilder();

        for (String word : words) {
            if (word.length() > 0) {
                capitalized.append(Character.toUpperCase(word.charAt(0)))  // Capitalize first char
                        .append(word.substring(1))                     // Append rest of word
                        .append(" ");                                   // Add space
            }
        }
        return capitalized.toString().trim();  // Remove extra space at end
    }
    // to use to ask questions instead of writing multiple line to ask
    public static String askQuestion(String question){
        System.out.print(question);
        String answer =input.nextLine();
        return answer.trim();
    }
}
