package com.apps.anker.facepunchdroid.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.CookieManager;
import android.widget.Toast;

import com.apps.anker.facepunchdroid.MainActivity;
import com.apps.anker.facepunchdroid.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.hypertrack.smart_scheduler.Job;
import io.hypertrack.smart_scheduler.SmartScheduler;

/**
 * Created by Mikkel on 07-12-2016.
 */

public class PrivateMessageService extends Service {
    private NotificationManager mNM;

    private int NOTIFICATION = 1;

    private List<Integer> notifiedMessages = new ArrayList<>();

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        SmartScheduler.JobScheduledCallback callback = new SmartScheduler.JobScheduledCallback() {
            @Override
            public void onJobScheduled(Context context, Job job) {
                getUnreadMessages();
            }
        };

        // Display a notification about us starting.  We put an icon in the status bar.
        //showNotification();
        getUnreadMessages();

        SmartScheduler jobScheduler = SmartScheduler.getInstance(getApplicationContext());


        Job.Builder builder = new Job.Builder(1, callback, Job.Type.JOB_TYPE_PERIODIC_TASK, "com.apps.anker.facepunchdroid.JobPeriodicTask")
                .setIntervalMillis(60000).setPeriodic(60000);

        Job job = builder.build();

        boolean result = jobScheduler.addJob(job);

        if (result) {
            Log.d("Job", "Job added!");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("PrivateMessageService", "Received start id " + startId + ": " + intent);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);

        // Tell the user we stopped.
        Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "Service started!";

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle("Facepunch Droid - New private message")  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

    private void getUnreadMessages() {
        final String bb_sessionhash = getCookie("https://facepunch.com/", "bb_sessionhash");
        final String bb_password = getCookie("https://facepunch.com/", "bb_password");
        final String bb_userid = getCookie("https://facepunch.com/", "bb_userid");

        new Thread(new Runnable() {
            public void run() {
                try {
                    Document doc = Jsoup.connect("https://facepunch.com/private.php")
                            .cookie("bb_sessionhash", bb_sessionhash)
                            .cookie("bb_password", bb_password)
                            .cookie("bb_userid", bb_userid)
                            .get();

                    Elements messages = doc.select("#pmfolderlist .blockrow:has(.unread)");

                    if(!messages.isEmpty()) {
                        Log.d("Service Messages", "You have unread messages");
                    } else {
                        Log.d("Service Messages", "You have NO unread messages");
                    }

                    for (Element message : messages) {
                        //Elements unreadmessages = message.children().select(".unread");

                        String subject = message.select(".unread").text();
                        String user = message.select(".commalist").text();
                        String messageUrl = message.select(".unread a").attr("href");
                        Integer pmid = Integer.parseInt( messageUrl.replaceAll("[^0-9]", "") );

                        if(!notifiedMessages.contains(pmid)) {


                            notifiedMessages.add(pmid);

                            Log.d("Service message User", message.select(".commalist").text());
                            Log.d("Service message Subject", message.select(".unread").text());
                            Log.d("Service message URL", messageUrl);
                            Log.d("Service message id", String.valueOf( pmid ));

                            // The PendingIntent to launch our activity if the user selects this notification
                            Intent intent = new Intent(PrivateMessageService.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("viewMessage", messageUrl);


                            PendingIntent contentIntent = PendingIntent.getActivity(PrivateMessageService.this, 0,
                                    intent, PendingIntent.FLAG_UPDATE_CURRENT);


                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                            // Set the info for the views that show in the notification panel.
                            Notification notification = new Notification.Builder(PrivateMessageService.this)
                                    .setSmallIcon(R.mipmap.ic_launcher)  // the status icon
                                    .setTicker("From: " + user)  // the status text
                                    .setWhen(System.currentTimeMillis())  // the time stamp
                                    .setContentTitle(subject)  // the label of the entry
                                    .setContentText("From: " + user)  // the contents of the entry
                                    .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                                    .setAutoCancel(true)
                                    .setSound(alarmSound)
                                    .build();

                            // Send the notification.
                            mNM.notify(pmid, notification);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public String getCookie(String siteName,String CookieName){
        String CookieValue = null;

        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        String[] temp=cookies.split(";");
        for (String ar1 : temp ){
            if(ar1.contains(CookieName)){
                String[] temp1=ar1.split("=");
                CookieValue = temp1[1];
            }
        }
        return CookieValue;
    }
}
