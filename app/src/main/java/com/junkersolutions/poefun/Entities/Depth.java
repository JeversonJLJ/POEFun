package com.junkersolutions.poefun.Entities;

import com.google.gson.annotations.SerializedName;

public class Depth {

    @SerializedName("default")
    private String defaulti;

    @SerializedName("solo")
    private String solo;

    public String getDefault() {
        return defaulti;
    }

    public void setDefault(String defaulti) {
        this.defaulti = defaulti;
    }

    public String getSolo() {
        return solo;
    }

    public void setSolo(String solo) {
        this.solo = solo;
    }
}
