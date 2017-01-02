package com.apps.anker.facepunchdroid.Services.Objects;

/**
 * Created by Mikkel on 21-12-2016.
 */

public class SubscribedThread {
    String title;
    Integer newpostCount;
    Integer threadID;
    String threadUrl;

    public String getThreadUrl() {
        return threadUrl;
    }

    public void setThreadUrl(String threadUrl) {
        this.threadUrl = threadUrl;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getNewpostCount() {
        return newpostCount;
    }

    public void setNewpostCount(Integer newpostCount) {
        this.newpostCount = newpostCount;
    }

    public Integer getThreadID() {
        return threadID;
    }

    public void setThreadID(Integer threadID) {
        this.threadID = threadID;
    }




}
