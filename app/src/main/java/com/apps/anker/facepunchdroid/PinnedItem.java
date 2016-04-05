package com.apps.anker.facepunchdroid;




import java.util.UUID;

import io.realm.RealmObject;

/**
 * Created by Mikkel on 23-03-2016.
 */
public class PinnedItem extends RealmObject {
    String title;
    String url;


    public String getTitle() { return this.title; }
    public void setTitle(String title) { this.title = title; }

    public String getUrl() { return this.url; }
    public void setUrl(String url) { this.url = url; }


}
