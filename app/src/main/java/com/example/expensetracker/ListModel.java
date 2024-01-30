package com.example.expensetracker;

public class ListModel {

    String amount;
    String category;
    String date;

    public ListModel(String category, String amount, String date) {
        this.date = date;
        this.amount = amount;
        this.category = category;
    }

    public String getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }


}
