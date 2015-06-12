package fiu.ssobec.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fiu.ssobec.Activity.EditZone;
import fiu.ssobec.AdaptersUtil.PlugLoadListParent;
import fiu.ssobec.AdaptersUtil.ZoneListParent;
import fiu.ssobec.R;

/**
 * Created by irvincardenas on 6/11/15.
 */
public class AppliancesAdapter implements ListAdapter {

    private LayoutInflater inflater;
    private Context mcontext;
    private ArrayList<PlugLoadListParent> parents;

    public AppliancesAdapter(Context mcontext, ArrayList<PlugLoadListParent> parents){
        inflater = LayoutInflater.from(mcontext);
        this.mcontext = mcontext;
        this.parents = parents;
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
        return parents.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final PlugLoadListParent mparent = parents.get(position);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.row_appliance, parent, false);
        }

        TextView tvZoneName = (TextView) convertView.findViewById(R.id.appliance_name);
        tvZoneName.setText(mparent.getName());

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return parents.isEmpty();
    }
}
