package com.apps.anker.facepunchdroid;

import com.orm.SugarRecord;

/**
 * Created by Mikkel on 23-03-2016.
 */
public class PinnedItem extends SugarRecord {
    String title;
    String url;

    public PinnedItem(){
    }

    public PinnedItem(String title, String url){
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return this.title;
    }

}
