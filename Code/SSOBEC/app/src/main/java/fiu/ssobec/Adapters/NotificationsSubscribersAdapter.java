package fiu.ssobec.Adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import fiu.ssobec.R;

/**
 * Created by irvincardenas on 7/10/15.
 */
/*
public class NotificationsSubscribersAdapter extends ListAdapter {

    private ArrayList<String> subscribers;
    private Context context;
    private LayoutInflater inflater;

    public NotificationsSubscribersAdapter(Context context){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.subscribers = subscribers;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return subscribers.size();
    }

    @Override
    public Object getItem(int position) {
        return subscribers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {

            convertView = inflater.inflate(R.layout.row_subscriber_email_list, parent, false);
        }

        TextView tv = (TextView) convertView.findViewById(R.id.subscriber_list_item_titleTextView);
        tv.setText(subscribers.get(position));
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

}
*/
