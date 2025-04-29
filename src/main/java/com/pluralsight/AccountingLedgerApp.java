package com.pluralsight;


import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class AccountingLedgerApp {
    static Scanner input = new Scanner(System.in);
    static DateTimeFormatter currentTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    static LocalDateTime currentTime = LocalDateTime.now();
    static String formatedCurrentTime = currentTime.format(currentTimeFormatter);
    static ArrayList<Transaction> transactions = new ArrayList<>();


    public static void main(String[] args) {
        try {
            createFileWithHeader();
            mainMenu();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

                // OPEN a new BufferedWriter locally in append mode
                BufferedWriter buffWriter = new BufferedWriter(new FileWriter("src/main/resources/transactions.csv", true));


                // Write the transaction details
                String depositEntry = String.format("%-12s | %-8s | %-25s | %-20s | %-10.2f",
                        date, time, description, vendor, amount);
                buffWriter.write(depositEntry); // write inputs to transactions file
                buffWriter.newLine(); // go to next line for the next entry
                // Close the writer
                buffWriter.close();

                // Also add to transactions ArrayList
                Transaction newDeposit = new Transaction(LocalDate.parse(date), LocalTime.parse(time), description, vendor, amount);
                transactions.add(newDeposit);

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

                // OPEN a new BufferedWriter locally in append mode
                BufferedWriter buffWriter = new BufferedWriter(new FileWriter("src/main/resources/transactions.csv", true));

                // Write the payment details
                String paymentEntry = String.format("%-12s | %-8s | %-25s | %-20s | %-10.2f",
                        date, time, description, vendor, amount);                buffWriter.write(paymentEntry);
                buffWriter.newLine(); // go to next line for the next entry
                buffWriter.close();

                // Also add to transactions ArrayList
                Transaction newPayment = new Transaction(LocalDate.parse(date), LocalTime.parse(time), description, vendor, amount);
                transactions.add(newPayment);

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
                                    "                             📒 LEDGER MENU                                   \n" +
                                    "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                                    " What would you like to do today?                                                   \n" +
                                    " A - View All Transactions                                                            \n" +
                                    " D - View Deposits Only                                                                \n" +
                                    " P - View Payments Only                                                               \n" +
                                    " R - View Reports                                                               \n" +
                                    " H - Return to Home Menu                                                             \n" +
                                    "-----------------------------------------------------------------------------------");

                // Read user's menu choice
                String choice = askQuestion("Please enter your choice (A/D/P/R/H): ").toUpperCase();

                switch (choice) {
                    case "A":
                        loadTransactions();
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
    // Displays all deposits in the Transaction
    private static void viewDepositsOnly() {
        System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"+
                            "                                         ALL DEPOSITS                                   "+
                            "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
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
                        "                                    ALL PAYMENTS                                      "+
                        "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
                System.out.println(transaction.toString());
            }
        }
        System.out.println("-------------------------------------------------------------------------------------\n");
    }
    // Displays all transactions
    public static void loadTransactions() {
        try {
            System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"+
                                "                                  ALL TRANSACTIONS                                 "+
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
                        " 6 - Challenge Yourself - Custom Report                                              \n" +
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
                        //challengeYourselfReport();
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
        System.out.println("-------------------------------------------------------------------------------------\n");

        // If no transactions found, inform the user
        if (!anyFound) {
            System.out.println("No transactions found for this month.");
        }
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
        System.out.println("-------------------------------------------------------------------------------------\n");


        // Inform user if no results found
        if (!anyFound) {
            System.out.println("No transactions found for the previous month.");
        }
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
        System.out.println("-------------------------------------------------------------------------------------\n");

        // If no matching entries were found, let the user know
        if (!anyFound) {
            System.out.println("No transactions found for this year up to today.");
        }
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
        System.out.println("-------------------------------------------------------------------------------------\n");

        // If nothing was found, notify the user
        if (!anyFound) {
            System.out.println("No transactions found for the previous year.");
        }
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
        System.out.println("-------------------------------------------------------------------------------------\n");
        // If no matches were found, notify the user
        if (!anyFound) {
            System.out.println("No transactions found for vendor: " + searchVendor);
        }
    }

    // will load current contents of the transaction fror reports only
    public static void loadTransactionForReportOnly(){
        transactions.clear(); // ✅ Clear old data before reloading
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
