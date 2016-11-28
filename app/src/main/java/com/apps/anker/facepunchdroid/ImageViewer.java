package com.apps.anker.facepunchdroid;

import android.Manifest;
import android.app.ActionBar;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import com.apps.anker.facepunchdroid.Tools.Downloading;
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
                    mAttacher.setMaximumScale(5);
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

        @Override
        public void onOutsidePhotoTap() {
            finish();
        }
    }

    public void onViewTap(View view, float x, float y) {
        finish();
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
                    Downloading.downloadImage(getIntent(), Uri.parse(url).toString(), this);
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
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Downloading.downloadImage(getIntent(), Uri.parse(url).toString(), this);
                    return;
                }
            }
        }
    }
}
