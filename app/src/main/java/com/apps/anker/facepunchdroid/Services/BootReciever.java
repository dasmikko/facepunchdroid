package com.apps.anker.facepunchdroid.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Mikkel on 21-12-2016.
 */

public class BootReciever extends BroadcastReceiver {
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    Intent SubThreadsService;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        Log.d("Reciver", "On Recieve");
        SubThreadsService = new Intent(context, SubscribedThreadsService.class);

        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmIntent = PendingIntent.getBroadcast(context, 0, SubThreadsService, 0);

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Start Subscribed threads service if enabled
            if(sharedPref.getBoolean("useSubThreadsNotifications", false) && sharedPref.getBoolean("isLoggedIn", false)) {
                Integer interval = Integer.valueOf(sharedPref.getString("subthreads_check_interval", "900000") );
                alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime(),
                        interval,
                        alarmIntent);

                Log.d("Alarm", "Alarm started");
                //startSubscribedThreadsService();
            } else {
                Log.d("Services", "Not starting SubThreads service, user is either not logged in, or haven't enabled it");
            }
        } else {
            ServiceManager serviceManager = new ServiceManager();
            serviceManager.startSubscribedThreadsService(context);
        }
    }
}
