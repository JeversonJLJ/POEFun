package com.junkersolutions.poefun.Entities;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Guild {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("tag")
    private String tag;

    @SerializedName("createdAt")
    private Date createdAt;

    @SerializedName("statusMessage")
    private String statusMessage;

}
