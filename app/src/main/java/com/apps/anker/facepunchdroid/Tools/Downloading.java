package com.apps.anker.facepunchdroid.Tools;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import com.apps.anker.facepunchdroid.Constants;

/**
 * Created by Mikkel on 20-05-2016.
 */
public class Downloading {


    public static void downloadImage(Intent i, String url, Activity activity ) {
        Intent intent = i;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.DOWNLOAD_IMAGE_PERMISSION);
                return;
            }
        }


        DownloadManager downloadManager = (DownloadManager)activity.getSystemService(activity.DOWNLOAD_SERVICE);
        Uri Download_Uri = Uri.parse(url);

        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        //Restrict the types of networks over which this download may proceed.
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

        //Set whether this download may proceed over a roaming connection.
        request.setAllowedOverRoaming(false);

        //Set the title of this download, to be displayed in notifications.
        request.setTitle(url.substring(url.lastIndexOf('/') + 1));

        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalPublicDir("Facepunch Droid", url.substring(url.lastIndexOf('/') + 1));

        //Enqueue a new download and same the referenceId
        Long downloadReference = downloadManager.enqueue(request);
    }

    public static void downloadUrl(Intent i, String url, Activity activity ) {
        Intent intent = i;
        DownloadManager downloadManager = (DownloadManager)activity.getSystemService(activity.DOWNLOAD_SERVICE);
        Uri Download_Uri = Uri.parse(url);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.DOWNLOAD_URL_PERMISSION);
                return;
            }
        }

        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        //Restrict the types of networks over which this download may proceed.
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

        //Set whether this download may proceed over a roaming connection.
        request.setAllowedOverRoaming(false);

        //Set the title of this download, to be displayed in notifications.
        request.setTitle(url.substring(url.lastIndexOf('/') + 1));

        //Set the local destination for the downloaded file to a path within the application's external files directory
        // For download folder use: Environment.DIRECTORY_DOWNLOADS

        request.setDestinationInExternalPublicDir("Facepunch Droid", url.substring(url.lastIndexOf('/') + 1));

        //Enqueue a new download and same the referenceId
        Long downloadReference = downloadManager.enqueue(request);
    }
}
