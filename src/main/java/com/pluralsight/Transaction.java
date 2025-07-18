package com.pluralsight;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public class Transaction {

    private LocalDate date;
    private LocalTime time;
    private String description;
    private String vendor;
    private Double amount;

    public Transaction(LocalDate date, LocalTime time, String description, String vendor, Double amount) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public String getVendor() {
        return vendor;
    }

    public Double getAmount() {
        return amount;
    }
    @Override
    public String toString() {
        String type = (amount >= 0) ? "Deposit" : "Payment";

        return String.format("%-11s | %-8s | %-25s | %-20s | %-10.2f | %-8s",
                date, time, description, vendor, amount, type);
    }


}
