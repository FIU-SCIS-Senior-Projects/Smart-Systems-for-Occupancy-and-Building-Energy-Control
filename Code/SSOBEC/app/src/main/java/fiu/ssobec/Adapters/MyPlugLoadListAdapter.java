package fiu.ssobec.Adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import fiu.ssobec.AdaptersUtil.PlugLoadListParent;
import fiu.ssobec.R;

/**
 * Created by Maria on 4/4/2015.
 */
public class MyPlugLoadListAdapter implements ListAdapter {

    private LayoutInflater inflater;
    private ArrayList<PlugLoadListParent> parents; //Each button will have an id according to each zone

    private Context mContext;

    public MyPlugLoadListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        mContext = context;
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
        return parents.get(position);
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


        ViewHolder mViewHolder = null;
        final PlugLoadListParent mparent = parents.get(position);

        if(convertView == null) {

            mViewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row_plugload_list, parent, false);
            mViewHolder.appliance_name = (TextView) convertView.findViewById(R.id.appliance_name);
            mViewHolder.appliance_status = (TextView) convertView.findViewById(R.id.appliance_status);
            mViewHolder.energy_consumed_today = (TextView) convertView.findViewById(R.id.energy_consumed_today);
            mViewHolder.appliance_icon = (ImageView) convertView.findViewById(R.id.appliance_icon);
            convertView.setTag(mViewHolder);

        }
        else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.appliance_name.setText(mparent.getName());
        mViewHolder.appliance_status.setText(mparent.getStatus());
        mViewHolder.energy_consumed_today.setText(mparent.getEnergy_consumed());

        int imageResource = mContext.getResources().getIdentifier("@drawable/plug", null, mContext.getPackageName());
        Drawable res = mContext.getResources().getDrawable(imageResource);
        mViewHolder.appliance_icon.setImageDrawable(res);

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
        return  ((parents == null) || parents.isEmpty());
    }

    public void setParents(ArrayList<PlugLoadListParent> newparent){
        parents = newparent;
    }


    static class ViewHolder {

        private  TextView appliance_name;
        private  TextView appliance_status;
        private  TextView energy_consumed_today;
        private ImageView appliance_icon;

    }
}
