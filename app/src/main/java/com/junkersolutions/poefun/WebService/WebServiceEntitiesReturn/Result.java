package com.junkersolutions.poefun.WebService.WebServiceEntitiesReturn;

import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("id")
    private String id;

    @SerializedName("listing")
    private Listing listing;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }
}
