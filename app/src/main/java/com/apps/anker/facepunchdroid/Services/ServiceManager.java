package com.apps.anker.facepunchdroid.Services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Mikkel on 08-12-2016.
 */

public class ServiceManager {

    Intent PMservice;
    Context SMcontext;

    Intent SubThreadsService;

    /**
     * Start all services if needed
     * @param context
     */
    public void initServices(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        PMservice = new Intent(context, PrivateMessageService.class);
        SubThreadsService = new Intent(context, SubscribedThreadsService.class);
        SMcontext = context;

        // Start PM service if enabled
        if(sharedPref.getBoolean("useNotifications", false) && sharedPref.getBoolean("isLoggedIn", false)) {
            startPrivateMessageService();
        } else {
            Log.d("Services", "Not starting PM service, user is either not logged in, or haven't enabled it");
        }

        // Start Subscribed threads service if enabled
        if(sharedPref.getBoolean("useSubThreadsNotifications", false) && sharedPref.getBoolean("isLoggedIn", false)) {
            startSubscribedThreadsService();
        } else {
            Log.d("Services", "Not starting SubThreads service, user is either not logged in, or haven't enabled it");
        }
    }

    public void killServices() {
        if(PMservice != null) {
            Log.d("Service", "Stopping PMService");
            SMcontext.stopService(PMservice);
        }

        if(SubThreadsService != null) {
            Log.d("Service", "Stopping SubThreadsService");
            SMcontext.stopService(SubThreadsService);
        }
    }

    public void startPrivateMessageService() {
        SMcontext.startService(PMservice);
    }
    public void stopPrivateMessageService() { SMcontext.stopService(PMservice); }
    public void restartPrivateMessageService() {
        SMcontext.stopService(PMservice);
        SMcontext.startService(PMservice);
    }


    public void startSubscribedThreadsService() {
        SMcontext.startService(SubThreadsService);
    }
    public void stopSubscribedThreadsService() { SMcontext.stopService(SubThreadsService); }
    public void restartSubscribedThreadsService() {
        SMcontext.stopService(SubThreadsService);
        SMcontext.startService(SubThreadsService);
    }

}
