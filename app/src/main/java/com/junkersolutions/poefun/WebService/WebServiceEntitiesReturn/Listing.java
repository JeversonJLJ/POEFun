package com.junkersolutions.poefun.WebService.WebServiceEntitiesReturn;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Listing {

    @SerializedName("method")
    private String method;

    @SerializedName("indexed")
    private Date indexed;

    @SerializedName("stash")
    private Stash stash;

    @SerializedName("whisper")
    private String whisper;

    @SerializedName("account")
    private Account account;

    @SerializedName("price")
    private Price price;


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Date getIndexed() {
        return indexed;
    }

    public void setIndexed(Date indexed) {
        this.indexed = indexed;
    }

    public Stash getStash() {
        return stash;
    }

    public void setStash(Stash stash) {
        this.stash = stash;
    }

    public String getWhisper() {
        return whisper;
    }

    public void setWhisper(String whisper) {
        this.whisper = whisper;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }
}
