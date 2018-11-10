package com.junkersolutions.poefun.WebService.WebServiceEntitiesReturn;

import com.google.gson.annotations.SerializedName;

public class Online {

    @SerializedName("league")
    private String league;

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }
}
