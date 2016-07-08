package com.apps.anker.facepunchdroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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

    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pinned_items_activity);

        mActivity = this;

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
                public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setTitle(R.string.userscript_dialog_title)
                            .setItems(R.array.editpinneditem_options, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            AlertDialog.Builder titlebuilder = new AlertDialog.Builder(mActivity);
                                            titlebuilder.setTitle(R.string.dialog_enternewtitle_title);


                                            // Set up the input
                                            final EditText titleinput = new EditText(mActivity);

                                            titleinput.setText(pinnedItems.get(position).getTitle());

                                            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                            titleinput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);

                                            titlebuilder.setView(titleinput);

                                            // Set up the buttons
                                            titlebuilder.setPositiveButton(R.string.answer_ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    realm.beginTransaction();
                                                    pinnedItems.get(position).setTitle(titleinput.getText().toString());
                                                    realm.commitTransaction();

                                                    Snackbar.make(mlayout , R.string.pinned_item_was_updated, Snackbar.LENGTH_LONG).show();
                                                }
                                            });
                                            titlebuilder.setNegativeButton(R.string.answer_cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });

                                            titlebuilder.show();
                                            break;
                                        case 1:
                                            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                            builder.setTitle(R.string.dialog_enternewurl_title);


                                            // Set up the input
                                            final EditText input = new EditText(mActivity);

                                            input.setText(pinnedItems.get(position).getUrl());

                                            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                            input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);

                                            builder.setView(input);

                                            // Set up the buttons
                                            builder.setPositiveButton(R.string.answer_ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    realm.beginTransaction();
                                                    pinnedItems.get(position).setUrl(input.getText().toString());
                                                    realm.commitTransaction();

                                                    Snackbar.make(mlayout , R.string.pinned_item_was_updated, Snackbar.LENGTH_LONG).show();
                                                }
                                            });
                                            builder.setNegativeButton(R.string.answer_cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });

                                            builder.show();



                                            break;
                                        case 2:
                                            Snackbar.make(mlayout, "Item: " + pinnedItems.get(position).getTitle() + " was deleted", Snackbar.LENGTH_LONG).show();
                                            realm.beginTransaction();
                                            pinnedItems.get(position).removeFromRealm(); // Delete and remove object directly
                                            realm.commitTransaction();

                                            adapter.notifyDataSetChanged();


                                            break;
                                    }
                                }
                            });
                    builder.create().show();


                    /*PinnedItem item = pinnedItems.get(Integer.parseInt(view.getTag().toString()));
                    //Toast.makeText(getApplicationContext() ,item.getTitle() + " deleted",Toast.LENGTH_SHORT).show();
                    Snackbar.make(mlayout,"Item: " + item.getTitle() + " was deleted", Snackbar.LENGTH_LONG).show();

                    // Remove from Realm
                    realm.beginTransaction();
                    pinnedItems.remove(position);
                    realm.commitTransaction();*/




                }
            });
        }

    }
}