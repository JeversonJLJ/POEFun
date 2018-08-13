package com.junkersolutions.poefun.Entities;
import com.google.gson.annotations.SerializedName;

public class Account {

    @SerializedName("name")
    private String name;

    @SerializedName("challenges")
    private Challenges challenges;

    @SerializedName("twitch")
    private Twitch twitch;

    @SerializedName("guild")
    private Guild guild;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Challenges getChallenges() {
        return challenges;
    }

    public void setChallenges(Challenges challenges) {
        this.challenges = challenges;
    }

    public Twitch getTwitch() {
        return twitch;
    }

    public void setTwitch(Twitch twitch) {
        this.twitch = twitch;
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }
}
