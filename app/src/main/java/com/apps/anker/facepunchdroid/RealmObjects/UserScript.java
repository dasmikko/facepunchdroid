package com.apps.anker.facepunchdroid.RealmObjects;

import io.realm.RealmObject;

/**
 * Created by Mikkel on 18-05-2016.
 */
public class UserScript extends RealmObject {
    String title;
    String url;
    String javascript;


    public String getTitle() { return this.title; }
    public void setTitle(String title) { this.title = title; }

    public String getUrl() { return this.url; }
    public void setUrl(String url) { this.url = url; }

    public String getJavascript() { return this.javascript; }
    public void setJavascript(String javascript) { this.javascript = javascript; }
}
