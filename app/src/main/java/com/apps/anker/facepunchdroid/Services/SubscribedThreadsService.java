package com.apps.anker.facepunchdroid.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.apps.anker.facepunchdroid.Cookies.Cookies;
import com.apps.anker.facepunchdroid.MainActivity;
import com.apps.anker.facepunchdroid.R;
import com.apps.anker.facepunchdroid.Services.Objects.SubscribedThread;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Mikkel on 07-12-2016.
 */

public class SubscribedThreadsService extends IntentService {
    private int startId;

    public static List<SubscribedThread> notifiedThreads = new ArrayList<>();

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public SubscribedThreadsService() {
        super("SubscribedThreadsService");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.


        // Get the latest
        getSubscribedThreads();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.startId = startId;
        //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /*@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("SubscribedThreadService", "Received start id " + startId + ": " + intent);
        return START_STICKY;
    }*/

    @Override
    public void onDestroy() {
        // Tell the user we stopped.
        //Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void makeNotification(SubscribedThread thread) {
        NotificationManager mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        // The PendingIntent to launch our activity if the user selects this notification
        Intent intent = new Intent(SubscribedThreadsService.this, MainActivity.class);
        intent.putExtra("viewThreadUrl", thread.getThreadUrl());
        intent.putExtra("viewThreadId", thread.getThreadID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(SubscribedThreadsService.this, thread.getThreadID(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        /*
        *  Unsubscribe from thread
        * */
        Intent actionUnsubscribe = new Intent(getApplicationContext(), SubscribedThreadsReciever.class);
        actionUnsubscribe.setAction("unsubscribe");
        actionUnsubscribe.putExtra("viewThreadId", thread.getThreadID());
        PendingIntent ActionUnsubscribePendingIntent = PendingIntent.getBroadcast(getApplicationContext(), thread.getThreadID(), actionUnsubscribe, 0);

        /*
        *   On swipe!
        * */
        Intent deleteintent = new Intent(getApplicationContext(), SubscribedThreadsReciever.class);
        deleteintent.setAction("removeSubThread");
        deleteintent.putExtra("subthreadindex", notifiedThreads.indexOf(thread));
        PendingIntent deletependingIntent = PendingIntent.getBroadcast(getApplicationContext(), thread.getThreadID(), deleteintent, 0);




        // Get notification settings
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean shouldVibrate = sharedPref.getBoolean("subthreads_check_vibrate", true);
        Boolean shouldPlaySound = sharedPref.getBoolean("subthreads_check_sound", true);
        Boolean shouldUseLight = sharedPref.getBoolean("subthreads_check_light", true);

        // Set the info for the views that show in the notification panel.
        NotificationCompat.Builder notificationbuilder = new NotificationCompat.Builder(SubscribedThreadsService.this)
                .setSmallIcon(R.drawable.ic_stat_placeholder_trans)  // the status icon
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(thread.getTitle())  // the label of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .setPriority(Notification.PRIORITY_LOW)
                .setDeleteIntent(deletependingIntent)
                .setAutoCancel(true);

        notificationbuilder.addAction(R.drawable.ic_drafts_black_24dp, "Unsubscribe", ActionUnsubscribePendingIntent);

        if (thread.getNewpostCount() > 1) {
            notificationbuilder.setTicker(thread.getNewpostCount() + " new posts");  // the status text
            notificationbuilder.setContentText(thread.getNewpostCount() + " new posts");  // the contents of the entry
        } else {
            notificationbuilder.setTicker(thread.getNewpostCount() + " new post");  // the status text
            notificationbuilder.setContentText(thread.getNewpostCount() + " new post");  // the contents of the entry
        }

        // set notification light
        if (shouldUseLight) {
            notificationbuilder.setLights(0xff00ff00, 1000, 1000);
        }

        Notification notification = notificationbuilder.build();

        // Set notification sound
        if (shouldPlaySound) {
            notification.defaults |= Notification.DEFAULT_SOUND;
        }

        // Set notification vibration
        if (shouldVibrate) {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }

        // Send the notification.
        mNM.notify(thread.getThreadID(), notification);
    }


    private void getSubscribedThreads() {
        final String bb_sessionhash = Cookies.getCookie("https://facepunch.com/", "bb_sessionhash");
        final String bb_password = Cookies.getCookie("https://facepunch.com/", "bb_password");
        final String bb_userid = Cookies.getCookie("https://facepunch.com/", "bb_userid");

        new Thread(new Runnable() {
            public void run() {
                try {
                    Document doc = Jsoup.connect("https://facepunch.com/usercp.php")
                            .cookie("bb_sessionhash", bb_sessionhash)
                            .cookie("bb_password", bb_password)
                            .cookie("bb_userid", bb_userid)
                            .timeout(60000)
                            .get();

                    Elements threads = doc.select("#threads tr");

                    if(!threads.isEmpty()) {
                        Log.d("ServiceSubThreads", "There is new posts in subscribed threads");
                    } else {
                        Log.d("Service Messages", "There is NO new posts in subscribed threads");

                        // Clear all, since there are no new posts in any of them!
                        notifiedThreads = new ArrayList<>();
                    }

                    for (Element thread : threads) {
                        SubscribedThread Subthread = new SubscribedThread();

                        Subthread.setTitle(thread.select(".threadtitle .title").text());
                        if(!thread.select(".threadtitle .newposts").text().isEmpty()) {
                            Subthread.setNewpostCount(Integer.parseInt(thread.select(".threadtitle .newposts").text().replace(" new posts", "").replace(" new post", "")));
                        } else {
                            Subthread.setNewpostCount(0);
                        }
                        Subthread.setThreadUrl(thread.select(".threadtitle .title").attr("href"));
                        Subthread.setThreadID( Integer.parseInt( Subthread.getThreadUrl().replaceAll("[^0-9]", "") ) ) ;

                        // If the new post box is empty, cancel notification that could exist and skip to next thread
                        if(Subthread.getNewpostCount() == 0) {
                            Log.d("notify", ".newpost is empty, skip this one");

                            NotificationManager mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                            mNM.cancel(Subthread.getThreadID());
                            continue;
                        }

                        Log.d("notifiedThreads", notifiedThreads.toString());

                        if(!notifiedThreads.isEmpty()) {
                            for (SubscribedThread singlethread : notifiedThreads) {
                                // Check if we already have been notified about it!
                                if (singlethread.getThreadID().equals(Subthread.getThreadID())) {
                                    Log.d("notify thread list", "Its an old thread, compare it!");
                                    Log.d("notify thread list", "Old Value: " + singlethread.getNewpostCount());
                                    Log.d("notify thread list", "New Value: " + Subthread.getNewpostCount());

                                    if (!singlethread.getNewpostCount().equals(Subthread.getNewpostCount())  ) {
                                        Log.d("notify thread list", "Wow! The thread has more posts now!");

                                        // Update count!
                                        singlethread.setNewpostCount(Subthread.getNewpostCount());

                                        // Throw notification!
                                        makeNotification(Subthread);


                                    }
                                } else {
                                    Log.d("notify thread list", "Its a new thread, add it!");
                                    notifiedThreads.add(Subthread);

                                    Log.d("Service Thread title", Subthread.getTitle());
                                    Log.d("Service Thread NPCount", String.valueOf(Subthread.getNewpostCount()));
                                    Log.d("Service Thread URL", Subthread.getThreadUrl());
                                    Log.d("Service Thread id", String.valueOf(Subthread.getThreadID()));

                                    // Throw notification!
                                    makeNotification(Subthread);
                                }
                            }
                        } else {
                            Log.d("notify thread list", "Its a new thread, add it!");
                            notifiedThreads.add(Subthread);

                            Log.d("Service Thread title", Subthread.getTitle());
                            Log.d("Service Thread NPCount", String.valueOf(Subthread.getNewpostCount()));
                            Log.d("Service Thread URL", Subthread.getThreadUrl());
                            Log.d("Service Thread id", String.valueOf(Subthread.getThreadID()));

                            // Throw notification!
                            makeNotification(Subthread);
                        }
                    }

                    stopSelf();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
