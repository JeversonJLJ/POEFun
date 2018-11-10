package com.junkersolutions.poefun.WebService.WebServiceEntitiesReturn;

import com.google.gson.annotations.SerializedName;

public class Stash {

    @SerializedName("name")
    private String name;

    @SerializedName("x")
    private int x;

    @SerializedName("y")
    private int y;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
