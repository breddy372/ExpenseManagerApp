package com.example.expensemanager.ui.dashboard.Model;

public class Data {

    private int amount;
    private String id;
    private String type;
    private String note;
    private String date;

    public Data(int amount, String id, String type, String note, String date) {
        this.amount = amount;
        this.id = id;
        this.type = type;
        this.note = note;
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Data() {

    }
}
