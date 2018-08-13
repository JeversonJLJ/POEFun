package com.junkersolutions.poefun.Class;

import android.content.Context;

public class Preferences {
    Properties properties;


    public Preferences(Context ctx) {
        properties = new Properties(ctx);
    }


    public boolean isUpdateNewsWifiConnected() throws Exception {
        return properties.Read("chk_pref_update_news_when_start_only_wifi", false);
    }

    public boolean isUpdateTrackerWifiConnected() throws Exception {
        return properties.Read("chk_pref_update_news_tracker_start_only_wifi", false);
    }

    public boolean isShowFinishedEvents() throws Exception {
        return properties.Read("chk_pref_show_finished_events", false);
    }

    public String getLastNews() throws Exception {
        return properties.Read("last_news", "");
    }

    public void setLastNews(String lastNewsTitle) throws Exception {
        properties.Save("last_news", lastNewsTitle);
    }

    public String getLeague() throws Exception {
        return properties.Read("league", "Standard");
    }

    public void setLeague(String league) throws Exception {
        properties.Save("league", league);
    }

    public boolean isLabyrinth() throws Exception {
        return properties.Read("labyrinth", false);
    }

    public void setLabyrinth(boolean labyrinth) throws Exception {
        properties.Save("labyrinth", labyrinth);
    }

    public String getDifficulty() throws Exception {
        return properties.Read("difficulty", "Normal");
    }

    public void setDifficulty(String difficulty) throws Exception {
        properties.Save("difficulty", difficulty);
    }

    public boolean isByAccountName() throws Exception {
        return properties.Read("by_account_name", false);
    }

    public void setByAccountName(boolean byAccountName) throws Exception {
        properties.Save("by_account_name", byAccountName);
    }

    public String getAccount() throws Exception {
        return properties.Read("account", "");
    }

    public void setAccount(String difficulty) throws Exception {
        properties.Save("account", difficulty);
    }


}