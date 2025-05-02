# 💼 Accounting Ledger Application 

A console-based Java application that allows users to securely log in, manage financial transactions, and generate professional accounting reports. This project simulates a mini-ledger system, complete with deposits, payments, filtering options, and custom reporting.


![GitHub last commit](https://img.shields.io/github/last-commit/HeldanaG/AccountingLedger)


## 🚀 Features

### 🔐 Secure Login System

- Up to 3 login attempts

- Specific feedback on incorrect username/password

### 📥 Transaction Management

- Add deposits and payments with validation

- Automatically records timestamps (date & time)

- All text inputs are automatically capitalized for clean formatting.

- Uses aligned column formatting with String.format()

### 📄 CSV File Handling

- Maintains a clean transactions.csv file

- Automatically inserts new entries at the top (most recent first)

- Preserves header format and spacing

### 📒 Ledger Menu

- View all transactions

- Filter by deposits only, payments only, or view a balance summary

### 📊 Reports Menu

- Month-to-date report

- Previous month report

- Year-to-date report

- Previous year report

- Search by vendor

- 🔍 Custom report (filter by date range, description, vendor, or amount)

  

## 🚀 Technologies Used

- Java 17+
  
- Java I/O (BufferedReader, BufferedWriter)
  
- Java Time API (`LocalDate`, `LocalTime`, `LocalDateTime`)
  
- CSV file manipulation
  
- Object-Oriented Programming
  

## 🧪 How to Run the Project

1. **Clone or download** this repository.
2. Make sure Java is installed on your system.
3. Compile and run the `AccountingLedgerApp.java` file.
4. On first run, the program will create a `transactions.csv` file with the correct header.


## 🖼️ Screenshot

![image](https://github.com/user-attachments/assets/3d2617b5-fab2-4b4d-aa85-afe6671b5a0e)

![image](https://github.com/user-attachments/assets/a8fb6b87-cca4-4c27-ab3e-070c25816ba1)

![image](https://github.com/user-attachments/assets/052ff34d-5dc4-4ab1-9905-25e92f278fda)

![image](https://github.com/user-attachments/assets/9c1380c5-1661-4436-a96b-529038fb013c)

![image](https://github.com/user-attachments/assets/2685b06d-471d-416a-8ad7-61bd69e1a5b3)


## 🔍 Interesting Code

```java

// Create new entry line
String newEntry = String.format("%-12s | %-8s | %-25s | %-19s | %-10.2f",
        date, time, description, vendor, amount);

// Read existing lines
BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/transactions.csv"));
String header = reader.readLine(); // preserve header

// You're creating a new StringBuilder to hold all the content that will be written to the file again.
StringBuilder updatedContent = new StringBuilder(header).append("\n").append(newEntry).append("\n");

// Now we start reading the rest of the file (old transactions).
// Each one is added after the new entry.
// So we’re building a complete new version of the file in memory with this order
String line;
while ((line = reader.readLine()) != null) {
    updatedContent.append(line).append("\n");
}
reader.close();

// Rewrite file
BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/transactions.csv"));
// That line replaces the file contents with the full updatedContent.
writer.write(updatedContent.toString());
writer.close();

// Add to memory list
Transaction newDeposit = new Transaction(LocalDate.parse(date), LocalTime.parse(time), description, vendor, amount);
transactions.add(0, newDeposit); // add to top of list

System.out.println("\nDeposit added successfully!");

```

## 🎯 Why It Matters

This block is central to how both addDeposit() and makePayment() methods ensure the latest transaction is always displayed first in the ledger. Instead of appending new records to the bottom, it reads the existing file, inserts the new entry immediately below the header, and then rewrites the full file.

By preserving the header and prepending new data, the code maintains a professional and readable format — crucial for real-world ledger systems where recent activity is most relevant. Using StringBuilder and manual reconstruction also gives full control over file structure and ordering, which is not possible with basic append operations.

Additionally, placing the new Transaction at the top of the in-memory transactions list (transactions.add(0, ...)) ensures that both file output and program logic reflect the same order — making the system consistent, intuitive, and ready for time-based reporting or analysis.







## 📧 Author
Heldana Gebremariam
