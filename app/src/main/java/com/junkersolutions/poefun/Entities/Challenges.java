package com.junkersolutions.poefun.Entities;
import com.google.gson.annotations.SerializedName;

public class Challenges {

    @SerializedName("total")
    private int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
