package com.junkersolutions.poefun.Entities;
import com.google.gson.annotations.SerializedName;

public class Character {

    @SerializedName("name")
    private String name;

    @SerializedName("level")
    private int level;

    @SerializedName("class")
    private String clas;

    @SerializedName("experience")
    private String experience;

    @SerializedName("depth")
    private Depth depth;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getClas() {
        return clas;
    }

    public void setClas(String clas) {
        this.clas = clas;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public Depth getDepth() {
        return depth;
    }

    public void setDepth(Depth depth) {
        this.depth = depth;
    }
}
