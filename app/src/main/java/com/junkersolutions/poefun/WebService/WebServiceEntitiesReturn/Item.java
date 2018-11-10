package com.junkersolutions.poefun.WebService.WebServiceEntitiesReturn;

import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("currency")
    private String currency;

    @SerializedName("amount")
    private double amount;

    @SerializedName("stock")
    private int stock;

    @SerializedName("id")
    private String id;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
