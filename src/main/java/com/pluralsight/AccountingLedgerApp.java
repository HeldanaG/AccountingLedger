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
            createFileWithHeader();  // << create file and header if needed
            //loadTransactions();      // << load transactions to memory
            mainMenu();              // << run menu
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
                System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                                    "            💰 WELCOME TO ACCOUNTING LEDGER APPLICATION 💰                        \n" +
                                    "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                                    " What would you like to do today?                                                   \n" +
                                    " D️ -  Add Deposit                                                                   \n" +
                                    " P️ -  Make Payment                                                                   \n" +
                                    " L️ -  Ledger (View all your transactions)                                            \n" +
                                    " X️ -  Exit the application                                                           \n" +
                                    "-------------------------------------------------------------------------------------");

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

        System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"+
                            "                                🏦 ADD A NEW DEPOSIT                             \n"+
                            "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        // Boolean to keep the app running until user chooses to exit
        boolean appRunning = true;
        // Loop to keep showing the menu until appRunning becomes false
        while (appRunning) {
            try {
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
                String depositEntry = date + " | " + time + " | " + description + " | " + vendor + " | " + amount;
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
        boolean appRunning = true;
        System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"+
                "                                🧾 MAKE A PAYMENT                                \n"+
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        while (appRunning) {
            try {
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
                String paymentEntry = date + " | " + time + " | " + description + " | " + vendor + " | " + amount;
                buffWriter.write(paymentEntry);
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
        System.out.println("\n[Ledger Menu will be implemented here]");
    }
    public static void loadTransactions() {
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
    public static void createFileWithHeader() {
        try {
            String filePath = "src/main/resources/transactions.csv";
            File file = new File(filePath);

            if (!file.exists()) {
                // If file doesn't exist, create it and write header
                BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
                writer.write("Date | Time | Description | Vendor | Amount");
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
                    writer.write("Date | Time | Description | Vendor | Amount");
                    writer.newLine();
                    writer.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Error checking or creating transaction file: " + e.getMessage());
        }
    }

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
    public static String askQuestion(String question){
        System.out.print(question);
        String answer =input.nextLine();
        return answer.trim();
    }
}
