package com.apps.anker.facepunchdroid.Services;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.apps.anker.facepunchdroid.Cookies.Cookies;
import com.koushikdutta.ion.Ion;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Mikkel on 21-12-2016.
 */

public class SubscribedThreadsReciever extends BroadcastReceiver {
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    Intent SubThreadsService;

    Integer subthreadid;

    Boolean wasSuccessful = false;

    private NotificationManager mNM;

    @Override
    public void onReceive(final Context context, Intent intent) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        Log.d("Reciver", "On Recieve");
        Log.d("Reciver", intent.getExtras().toString());
        mNM = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);

        if(intent.getAction() != null) {
            Log.d("getAction", intent.getAction());
            if (intent.getAction().equals("removeSubThread")) {
                Log.d("SubThreadNotifyList", "Removed thread from list");
                //SubscribedThreadsService.notifiedThreads.remove(intent.getIntExtra("subthreadindex", 0));
            }

            if (intent.getAction().equals("checkForNewPosts")) {
                ServiceManager serviceManager = new ServiceManager();
                serviceManager.startSubscribedThreadsService(context);
            }

            if (intent.getAction().equals("unsubscribe")) {




                subthreadid = intent.getExtras().getInt("viewThreadId");
                final String bb_sessionhash = Cookies.getCookie("https://facepunch.com/", "bb_sessionhash");
                final String bb_password = Cookies.getCookie("https://facepunch.com/", "bb_password");
                final String bb_userid = Cookies.getCookie("https://facepunch.com/", "bb_userid");

                Log.d("Notify", "Usubscribing thread "+ subthreadid);

                Thread t = new Thread(new Runnable() {
                    public void run() {
                        try {
                            Document doc = Jsoup.connect("https://facepunch.com/subscription.php?do=removesubscription&return=ucp&t="+subthreadid)
                                    .cookie("bb_sessionhash", bb_sessionhash)
                                    .cookie("bb_password", bb_password)
                                    .cookie("bb_userid", bb_userid)
                                    .get();
                            wasSuccessful = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                            wasSuccessful = false;
                        }
                    }
                });

                t.start();

                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(wasSuccessful) {
                    mNM.cancel(subthreadid);
                    Toast.makeText(context, "Unsubscribing thread...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Something went wrong...", Toast.LENGTH_SHORT).show();
                }

            }
        } else {
            Log.d("getAction", "Is null for some reason");
        }

    }
}
