package com.apps.anker.facepunchdroid.Pagepinning;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.apps.anker.facepunchdroid.Facepunch.Subscription;
import com.apps.anker.facepunchdroid.MainActivity;
import com.apps.anker.facepunchdroid.PinnedItem;
import com.apps.anker.facepunchdroid.R;
import com.apps.anker.facepunchdroid.Tools.UriHandling;

import io.realm.Realm;

/**
 * Created by Mikkel on 22-12-2016.
 */

public class PagePinningManager {
    static Realm realm = MainActivity.realm;


    public static void pin_page(final Context context, String title, final String url) {
        realm.beginTransaction();

        // Add a pinning
        PinnedItem pinitem = realm.createObject(PinnedItem.class);
        pinitem.setTitle(title);
        pinitem.setUrl(url);
        realm.commitTransaction();

        if(url.contains("showthread.php?t=")) {
            Log.d("Pinning", "Is Thread");
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Do you want to recieve notifications?")
                    .setPositiveButton(R.string.answer_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.d("Pinning", "Yes!");
                            Subscription.createSubscription(context, UriHandling.getThreadIdFromURL(url));
                        }
                    })
                    .setNegativeButton(R.string.answer_no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.d("Pinning", "No!");
                        }
                    });
            // Create the AlertDialog object and return it
            builder.create().show();

        }

    }
}
