package com.apps.anker.facepunchdroid;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.apps.anker.facepunchdroid.Adapters.PinnedItemsAdapter;
import com.apps.anker.facepunchdroid.Migrations.MainMigration;

import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


public class EditPinnedItemsActivity extends AppCompatActivity {
    PinnedItemsAdapter adapter;
    ListView lv;
    RealmResults<PinnedItem> pinnedItems;

    // Pinned items
    RealmConfiguration realmConfig;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pinned_items_activity);

        // Create the Realm configuration
        realmConfig = new RealmConfiguration.Builder(this)
                .schemaVersion(Constants.schemaVersion) // Must be bumped when the schema changes
                .migration(MainMigration.getMigration()) // Migration to run instead of throwing an exception
                .build();

        Realm.setDefaultConfiguration(realmConfig);

        // Open the Realm for the UI thread.
        realm = Realm.getDefaultInstance();

        final RelativeLayout mlayout = (RelativeLayout) findViewById(R.id.mainLayout);

        pinnedItems = realm.where(PinnedItem.class).findAll();

        if(pinnedItems.size() > 0)
        {
            adapter = new PinnedItemsAdapter(
                    this,
                    R.layout.pinneditem_list_item, pinnedItems);
            lv = (ListView) findViewById(R.id.pitem_list);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    PinnedItem item = pinnedItems.get(Integer.parseInt(view.getTag().toString()));
                    //Toast.makeText(getApplicationContext() ,item.getTitle() + " deleted",Toast.LENGTH_SHORT).show();
                    Snackbar.make(mlayout,"Item: " + item.getTitle() + " was deleted", Snackbar.LENGTH_LONG).show();

                    // Remove from Realm
                    realm.beginTransaction();
                    pinnedItems.remove(position);
                    realm.commitTransaction();


                    adapter.notifyDataSetChanged();

                }
            });
        }

    }
}