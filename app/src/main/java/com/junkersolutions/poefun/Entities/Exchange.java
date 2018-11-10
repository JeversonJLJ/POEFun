package com.junkersolutions.poefun.Entities;

import java.util.List;

public class Exchange {
    private List<String> have;
    private List<String> want;
    private Status status;

    public List<String> getHave() {
        return have;
    }

    public void setHave(List<String> have) {
        this.have = have;
    }

    public List<String> getWant() {
        return want;
    }

    public void setWant(List<String> want) {
        this.want = want;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
