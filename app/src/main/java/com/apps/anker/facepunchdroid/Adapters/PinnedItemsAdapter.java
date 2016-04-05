package com.apps.anker.facepunchdroid.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.apps.anker.facepunchdroid.PinnedItem;
import com.apps.anker.facepunchdroid.R;

import java.util.List;

/**
 * Created by Mikkel on 23-03-2016.
 */
public class PinnedItemsAdapter extends ArrayAdapter<PinnedItem> {

    private List<PinnedItem> pinnedItems;

    public PinnedItemsAdapter(Context context, int resource, List<PinnedItem> objects) {
        super(context, resource, objects);
        pinnedItems = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int dbPosition = position + 1;

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.pinneditem_list_item, parent, false);
        }

        Log.d("Position:", Integer.toString(position));
        PinnedItem pitem = pinnedItems.get(position);

        convertView.setTag(position);
        TextView titleText = (TextView) convertView.findViewById(R.id.item_title);
        titleText.setText(pitem.getTitle());

        return convertView;
    }
}