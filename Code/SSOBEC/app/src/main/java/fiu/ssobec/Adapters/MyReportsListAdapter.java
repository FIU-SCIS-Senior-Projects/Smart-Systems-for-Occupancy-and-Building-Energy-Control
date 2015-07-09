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

import fiu.ssobec.AdaptersUtil.ReportListParent;
import fiu.ssobec.R;

/**
 * Created by diana on July 2015.
 */
public class MyReportsListAdapter implements ListAdapter {
    private LayoutInflater inflater;
    private ArrayList<ReportListParent> parents;
    private Context mContext;

    public MyReportsListAdapter(Context context) {
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
        final ReportListParent mparent = parents.get(position);

        if(convertView == null) {

            mViewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row_report_list, parent, false);

            mViewHolder.name = (TextView) convertView.findViewById(R.id.name);
            mViewHolder.description = (TextView) convertView.findViewById(R.id.description);
            mViewHolder.value = (TextView) convertView.findViewById(R.id.value);
            mViewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(mViewHolder);

        }
        else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.name.setText(mparent.getName());
        mViewHolder.description.setText(mparent.getDescription());
        mViewHolder.value.setText(mparent.getValue());

        int imageResource = 0;
        if(mparent.getDescription().equalsIgnoreCase("user")){
            imageResource = mContext.getResources().getIdentifier("@drawable/user", null, mContext.getPackageName());
        }else if(mparent.getDescription().equalsIgnoreCase("light")){
            imageResource = mContext.getResources().getIdentifier("@drawable/lightbulb", null, mContext.getPackageName());
        }else{
            imageResource = mContext.getResources().getIdentifier("@drawable/plug", null, mContext.getPackageName());
        }

        Drawable res = mContext.getResources().getDrawable(imageResource);
        mViewHolder.icon.setImageDrawable(res);

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

    public void setParents(ArrayList<ReportListParent> newparent){
        parents = newparent;
    }


    static class ViewHolder {

        private  TextView name;
        private  TextView description;
        private  TextView value;
        private ImageView icon;

    }
}
