package com.apps.anker.facepunchdroid;

import android.Manifest;
import android.app.ActionBar;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.apps.anker.facepunchdroid.Tools.Language;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nbsp.materialfilepicker.MaterialFilePicker;


import java.util.regex.Pattern;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewer extends AppCompatActivity {
    PhotoViewAttacher mAttacher;
    ImageView imgView;
    String url;
    String fileType;

    private SharedPreferences sharedPref;
    String selectedLang;

    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Update language
        selectedLang = sharedPref.getString("language", "system");
        Language.setLanguage(selectedLang, getResources());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        pb = (ProgressBar) findViewById(R.id.progressBarIMGViewer);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        fileType = MimeTypeMap.getFileExtensionFromUrl(url);

        Log.d("FILETYPE", fileType);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.ImageViewerBackground)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        imgView = (ImageView) findViewById(R.id.imageView);

        FutureCallback<ImageView> imageLoadedCallback = new FutureCallback<ImageView>() {
            @Override
            public void onCompleted(Exception e, ImageView result) {
                pb.setVisibility(View.GONE);
                if(fileType != "gif") {
                    if (mAttacher != null) {
                        mAttacher.update();
                    } else {
                        mAttacher = new PhotoViewAttacher(imgView);
                    }
                    mAttacher.setOnPhotoTapListener(new PhotoTapListener());
                }
            }
        };

        Ion.with(this)
                .load(url)
                .progressBar(pb)
                .withBitmap()
                .deepZoom()
                .intoImageView(imgView)
                .setCallback(imageLoadedCallback);



    }

    private class PhotoTapListener implements PhotoViewAttacher.OnPhotoTapListener {

        @Override
        public void onPhotoTap(View view, float x, float y) {
            float xPercentage = x * 100f;
            float yPercentage = y * 100f;

            finish();
        }
    }

    public void onViewTap(View view, float x, float y) {
        finish();
    }

    private void downloadImage() {
        Intent intent = getIntent();
        DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
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
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS.toString(), url.substring(url.lastIndexOf('/') + 1));
        //Enqueue a new download and same the referenceId
        Long downloadReference = downloadManager.enqueue(request);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_imageviewer_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_download:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                } else {
                    downloadImage();
                }
                break;
            case R.id.openinbrowser:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            case R.id.sharepage:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, url);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 2: {
                downloadImage();
                return;
            }
        }
    }
}
