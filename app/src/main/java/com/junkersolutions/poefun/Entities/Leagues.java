package com.junkersolutions.poefun.Entities;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Leagues {

    @SerializedName("id")
    private String id;

    @SerializedName("url")
    private String url;

    @SerializedName("startAt")
    private Date startAt;

    @SerializedName("endAt")
    private Date endAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public Date getEndAt() {
        return endAt;
    }

    public void setEndAt(Date endAt) {
        this.endAt = endAt;
    }
}
