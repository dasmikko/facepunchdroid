package com.apps.anker.facepunchdroid;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewer extends AppCompatActivity {
    PhotoViewAttacher mAttacher;
    ImageView imgView;
    String url;
    String fileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        fileType = MimeTypeMap.getFileExtensionFromUrl(url);

        Log.d("FILETYPE", fileType);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        imgView = (ImageView) findViewById(R.id.imageView);

        FutureCallback<ImageView> imageLoadedCallback = new FutureCallback<ImageView>() {
            @Override
            public void onCompleted(Exception e, ImageView result) {

                if(fileType != "gif") {
                    if (mAttacher != null) {
                        mAttacher.update();
                    } else {
                        mAttacher = new PhotoViewAttacher(imgView);
                    }
                }
            }
        };

        Ion.with(this)
                .load(url)
                .withBitmap()
                .deepZoom()
                .intoImageView(imgView)
                .setCallback(imageLoadedCallback);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
