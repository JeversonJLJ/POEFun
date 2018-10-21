package com.junkersolutions.poefun.Entities;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.List;

public class CurrencyGroup implements Parent<Currency>, Cloneable {
    private String groupName;
    private List<Currency> currencyList;


    public CurrencyGroup(String groupName) {
        this.groupName = groupName;
    }

    public CurrencyGroup(String groupName, List<Currency> currencyList) {
        this.groupName = groupName;
        this.currencyList = currencyList;
    }

    public List<Currency> getCurrencyList() {
        return currencyList;
    }

    public void setCurrencyList(List<Currency> currencyList) {
        this.currencyList = currencyList;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    @Override
    public List<Currency> getChildList() {
        return currencyList;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public CurrencyGroup getClone() {
        try {
            // call clone in Object.
            List<Currency> cloneCurrency = new ArrayList<Currency>();
            for (Currency currency: currencyList) {
                cloneCurrency.add(currency.getClone());
            }
            CurrencyGroup clonableGroup = new CurrencyGroup(groupName,cloneCurrency);
            return clonableGroup;
        } catch (Exception e) {
            e.printStackTrace();
            return this;
        }
    }
}
