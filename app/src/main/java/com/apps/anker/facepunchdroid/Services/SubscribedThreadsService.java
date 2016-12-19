package com.apps.anker.facepunchdroid.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.CookieManager;
import android.widget.Toast;

import com.apps.anker.facepunchdroid.Cookies.Cookies;
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

public class SubscribedThreadsService extends Service {
    private NotificationManager mNM;

    public static List<Integer> notifiedThreads = new ArrayList<>();

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Get the latest
        getSubscribedThreads();

        // Make callback
        SmartScheduler.JobScheduledCallback callback = new SmartScheduler.JobScheduledCallback() {
            @Override
            public void onJobScheduled(Context context, Job job) {
                getSubscribedThreads();
            }
        };

        SmartScheduler jobScheduler = SmartScheduler.getInstance(getApplicationContext());

        // Get saved interval
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Integer interval = Integer.valueOf(sharedPref.getString("subthreads_check_interval", "900000") );

        Job.Builder builder = new Job.Builder(1, callback, Job.Type.JOB_TYPE_PERIODIC_TASK, "com.apps.anker.facepunchdroid.SubThreadPeriodicTask")
                .setIntervalMillis(interval).setPeriodic(interval);

        Job job = builder.build();

        boolean result = jobScheduler.addJob(job);

        if (result) {
            Log.d("Job", "Job sub thread check added! Interval: "+interval);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("SubscribedThreadService", "Received start id " + startId + ": " + intent);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Tell the user we stopped.
        Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
                            .get();

                    Elements threads = doc.select("#threads tr");

                    if(!threads.isEmpty()) {
                        Log.d("ServiceSubThreads", "There is new posts in subscribed threads");
                    } else {
                        Log.d("Service Messages", "There is NO new posts in subscribed threads");
                    }

                    for (Element thread : threads) {
                        //Elements unreadmessages = message.children().select(".unread");

                        String threadTitle = thread.select(".threadtitle .title").text();
                        String newPostsCount = thread.select(".threadtitle .newposts").text();
                        String threadUrl = thread.select(".threadtitle .title").attr("href");
                        Integer threadId = Integer.parseInt( threadUrl.replaceAll("[^0-9]", "") );

                        if(!notifiedThreads.contains(threadId)) {


                            notifiedThreads.add(threadId);

                            Log.d("Service Thread title", threadTitle);
                            Log.d("Service Thread NPCount", newPostsCount);
                            Log.d("Service Thread URL", threadUrl);
                            Log.d("Service Thread id", String.valueOf( threadId ));

                            // The PendingIntent to launch our activity if the user selects this notification
                            Intent intent = new Intent(SubscribedThreadsService.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("viewThreadUrl", threadUrl);
                            intent.putExtra("viewThreadId", threadId);


                            PendingIntent contentIntent = PendingIntent.getActivity(SubscribedThreadsService.this, 0,
                                    intent, PendingIntent.FLAG_UPDATE_CURRENT);



                            // Get notification settings
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            Boolean shouldVibrate = sharedPref.getBoolean("subthreads_check_vibrate", true);
                            Boolean shouldPlaySound = sharedPref.getBoolean("subthreads_check_sound", true);
                            Boolean shouldUseLight = sharedPref.getBoolean("subthreads_check_light", true);

                            // Set the info for the views that show in the notification panel.
                            Notification.Builder notificationbuilder = new Notification.Builder(SubscribedThreadsService.this)
                                    .setSmallIcon(R.drawable.ic_stat_placeholder_trans)  // the status icon
                                    .setTicker(newPostsCount)  // the status text
                                    .setWhen(System.currentTimeMillis())  // the time stamp
                                    .setContentTitle(threadTitle)  // the label of the entry
                                    .setContentText(newPostsCount)  // the contents of the entry
                                    .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                                    .setPriority(Notification.PRIORITY_DEFAULT)
                                    .setAutoCancel(true);

                            // set notification ligt
                            if(shouldUseLight) {
                                notificationbuilder.setLights(0xff00ff00, 1000, 1000);
                            }

                            Notification notification = notificationbuilder.build();

                            // Set notification sound
                            if(shouldPlaySound) {
                                notification.defaults |= Notification.DEFAULT_SOUND;
                            }

                            // Set notification vibration
                            if(shouldVibrate) {
                                notification.defaults |= Notification.DEFAULT_VIBRATE;
                            }

                            // Send the notification.
                            mNM.notify(threadId, notification);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
