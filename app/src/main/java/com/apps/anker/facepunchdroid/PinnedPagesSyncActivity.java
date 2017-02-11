package com.apps.anker.facepunchdroid;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.apps.anker.facepunchdroid.Facepunch.Subscription;

public class PinnedPagesSyncActivity extends AppCompatActivity {

    Toolbar mActionbar;

    Button btnGetFolder;
    Button btnCreateFolder;
    EditText editTextFolderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinned_pages_sync);


        // Actionbar stuff
        mActionbar = (Toolbar) findViewById(R.id.ppsActionbar);
        setSupportActionBar(mActionbar);


        getSupportActionBar().setTitle("Pinned Pages Sync");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mActionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });
        // Actionbar stuff END


        btnGetFolder = (Button) findViewById(R.id.btnGetFolders);
        btnGetFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                Subscription.getSubscriptionFolders();
                Toast.makeText(PinnedPagesSyncActivity.this, "Check log!", Toast.LENGTH_SHORT).show();
            }
        });

        editTextFolderName = (EditText) findViewById(R.id.editTextFolderName);
        btnCreateFolder = (Button) findViewById(R.id.btnCreateFolder);
        btnCreateFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                Subscription.createSubscriptionFolder(getApplicationContext(), editTextFolderName.getText().toString());
                Toast.makeText(PinnedPagesSyncActivity.this, "Folder created!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
