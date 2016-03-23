package com.apps.anker.facepunchdroid;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.apps.anker.facepunchdroid.Adapters.PinnedItemsAdapter;

import java.util.List;

public class EditPinnedItemsActivity extends AppCompatActivity {
    PinnedItemsAdapter adapter;
    ListView lv;
    List<PinnedItem> pinnedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pinned_items_activity);

        final RelativeLayout mlayout = (RelativeLayout) findViewById(R.id.mainLayout);

        pinnedItems = PinnedItem.listAll(PinnedItem.class);

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
                    pinnedItems.remove(position);
                    PinnedItem.delete(item);
                    adapter.notifyDataSetChanged();

                }
            });
        }

    }
}
