package com.apps.anker.facepunchdroid.Tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;

import com.apps.anker.facepunchdroid.MainActivity;
import com.apps.anker.facepunchdroid.PinnedItem;
import com.apps.anker.facepunchdroid.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Mikkel on 28-08-2017.
 */

public class ShortcutsManager {
    public static void updateShortcuts(Realm realm, Activity activity) {
        /**
         * Shortcuts!
         */
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = activity.getSystemService(ShortcutManager.class);

            ArrayList<ShortcutInfo> shortcutlist = new ArrayList<>();

            // Get Pinned items
            RealmResults<PinnedItem> pinnedItems = realm.where(PinnedItem.class).findAll();

            if (pinnedItems.size() > 0) {
                int count = 0;
                for (PinnedItem pitem : pinnedItems) {
                    Intent linkIntent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(pitem.getUrl()),
                            activity,
                            activity.getClass());

                    linkIntent.putExtra("shortcut", pitem.getUrl());


                    ShortcutInfo shortcut = new ShortcutInfo.Builder(activity, "shortcut" + count)
                            .setShortLabel(pitem.getTitle())
                            .setLongLabel(pitem.getTitle())
                            .setIcon(Icon.createWithResource(activity, R.drawable.ic_link_black_24dp))
                            .setIntent(linkIntent)
                            .build();

                    shortcutlist.add(shortcut);
                    count++;
                }

                shortcutManager.setDynamicShortcuts(shortcutlist);
            }
        }
    }
}
