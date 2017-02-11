package com.apps.anker.facepunchdroid.Facepunch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.apps.anker.facepunchdroid.Cookies.Cookies;
import com.apps.anker.facepunchdroid.Facepunch.Objects.SubscriptionFolder;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Mikkel on 22-12-2016.
 */

public class Subscription {
    static ArrayList<SubscriptionFolder> subsarray = new ArrayList<>();

    public static void createSubscription(Context context, final Integer threadID) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        final String securityToken = sharedPref.getString("securitytoken", null);
        final String bb_sessionhash = Cookies.getCookie("https://facepunch.com/", "bb_sessionhash");
        final String bb_password = Cookies.getCookie("https://facepunch.com/", "bb_password");
        final String bb_userid = Cookies.getCookie("https://facepunch.com/", "bb_userid");

        Log.d("Notify", "Usubscribing thread "+ threadID);

        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Document doc = Jsoup.connect("https://facepunch.com/subscription.php?do=doaddsubscription&threadid="+threadID)
                            .cookie("bb_sessionhash", bb_sessionhash)
                            .cookie("bb_password", bb_password)
                            .cookie("bb_userid", bb_userid)
                            .data("emailupdate", "0")
                            .data("folderid", "0")
                            .data("s", "")
                            .data("securitytoken", securityToken)
                            .data("do", "doaddsubscription")
                            .data("threadid", String.valueOf(threadID))
                            .data("url", "https://facepunch.com/showthread.php?t=" + threadID)
                            .post();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
    }

    public static void removeSubscription(final Integer threadID) {
        final String bb_sessionhash = Cookies.getCookie("https://facepunch.com/", "bb_sessionhash");
        final String bb_password = Cookies.getCookie("https://facepunch.com/", "bb_password");
        final String bb_userid = Cookies.getCookie("https://facepunch.com/", "bb_userid");

        Log.d("Notify", "Unsubscribing thread "+ threadID);

        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Document doc = Jsoup.connect("https://facepunch.com/subscription.php?do=removesubscription&return=ucp&t="+threadID)
                            .cookie("bb_sessionhash", bb_sessionhash)
                            .cookie("bb_password", bb_password)
                            .cookie("bb_userid", bb_userid)
                            .get();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
    }

    public static ArrayList<SubscriptionFolder> getSubscriptionFolders() {
        final String bb_sessionhash = Cookies.getCookie("https://facepunch.com/", "bb_sessionhash");
        final String bb_password = Cookies.getCookie("https://facepunch.com/", "bb_password");
        final String bb_userid = Cookies.getCookie("https://facepunch.com/", "bb_userid");



        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Document doc = Jsoup.connect("https://facepunch.com/subscription.php")
                            .cookie("bb_sessionhash", bb_sessionhash)
                            .cookie("bb_password", bb_password)
                            .cookie("bb_userid", bb_userid)
                            .get();

                    Element subscriptionbox = doc.select("#usercp_nav > div:nth-child(2) > div > ul:nth-child(1)").first();
                    Elements folders = subscriptionbox.select("li");

                    ArrayList<SubscriptionFolder> subsarray = new ArrayList<>();

                    for (Element folder : folders) {
                        SubscriptionFolder subscriptionfolder = new SubscriptionFolder();

                        // Folder id
                        Integer folderid = Integer.parseInt(folder.select("a").attr("href").replace("subscription.php?folderid=", ""));
                        subscriptionfolder.setId(folderid);

                        // foldername
                        String foldername = folder.select("a").text();
                        subscriptionfolder.setName(foldername);

                        subsarray.add(subscriptionfolder);

                        Log.d("FolderLoop", foldername + " ("+ folderid +")");
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

        t.start();
        try {
            t.join();
            return subsarray;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return subsarray;
    }


    public static void createSubscriptionFolder(Context context, final String foldername) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        final String securityToken = sharedPref.getString("securitytoken", null);
        final String bb_sessionhash = Cookies.getCookie("https://facepunch.com/", "bb_sessionhash");
        final String bb_password = Cookies.getCookie("https://facepunch.com/", "bb_password");
        final String bb_userid = Cookies.getCookie("https://facepunch.com/", "bb_userid");

        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Connection doc = Jsoup.connect("https://facepunch.com/subscription.php?do=doeditfolders")
                            .cookie("bb_sessionhash", bb_sessionhash)
                            .cookie("bb_password", bb_password)
                            .cookie("bb_userid", bb_userid)
                            .data("folderid", "0")
                            .data("s", "")
                            .data("securitytoken", securityToken)
                            .data("do", "doeditfolders");

                    int Count = 0;

                    final ArrayList<SubscriptionFolder> currentfolders = getSubscriptionFolders();

                    for (SubscriptionFolder folder : currentfolders) {

                        doc.data("folderlist["+Count+"]", folder.getName());

                        Count++;
                    }

                    Log.d("oldcount", String.valueOf(Count));
                    Count++;

                    Log.d("newfolder", Count + " " + foldername);
                    doc.data("folderlist["+Count+"]", foldername);

                    doc.post();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

        t.start();
    }

    private class getSubscriptionFoldersTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            //TextView txt = (TextView) findViewById(R.id.output);
            //txt.setText("Executed"); // txt.setText(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
