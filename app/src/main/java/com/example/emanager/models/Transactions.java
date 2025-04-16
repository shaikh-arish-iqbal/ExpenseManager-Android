package com.example.emanager.models;

import java.util.Date;

public class Transactions {
    private long id;
    private String type;
    private String category;
    private String account;
    private String note;
    private Date date;
    private double amount;

    public Transactions() {
        // Empty constructor required for serialization or future database usage (Room/Firebase/etc.)
    }

    public Transactions(String type, String category, String account, String note, Date date, double amount, long id) {
        this.type = type;
        this.category = category;
        this.account = account;
        this.note = note;
        this.date = date;
        this.amount = amount;
        this.id = id;
    }

    // Getters and setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}
