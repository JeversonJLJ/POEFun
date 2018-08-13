package com.junkersolutions.poefun.Entities;
import com.google.gson.annotations.SerializedName;
import com.junkersolutions.poefun.Entities.Account;
import com.junkersolutions.poefun.Entities.Character;

public class Leaderboards {


    @SerializedName("rank")
    private int rank;

    @SerializedName("dead")
    private boolean dead;

    @SerializedName("online")
    private boolean online;

    @SerializedName("time")
    private String time;

    @SerializedName("character")
    private Character character;

    @SerializedName("account")
    private Account account;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
