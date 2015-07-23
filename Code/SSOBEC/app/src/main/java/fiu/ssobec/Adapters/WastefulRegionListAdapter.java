
package fiu.ssobec.Adapters;

import android.content.Context;
import android.content.Intent;
import android.app.Application;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import fiu.ssobec.Activity.MyZonesActivity;
import fiu.ssobec.Activity.SendNotificationActivity;
import fiu.ssobec.Activity.TurnApplianceOffActivity;
import fiu.ssobec.Activity.TurnLightOffActivity;
import fiu.ssobec.AdaptersUtil.WastefulRegionListParent;
import fiu.ssobec.R;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by diana on 6/11/2015.
 */
public class WastefulRegionListAdapter implements ListAdapter {

    private LayoutInflater inflater;
    private ArrayList<WastefulRegionListParent> parents;
    private Context mContext;

    public WastefulRegionListAdapter(Context context) {
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
        final WastefulRegionListParent mparent = parents.get(position);

        if(convertView == null) {

            mViewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row_wasteful_regions_list, parent, false);

            mViewHolder.name = (TextView) convertView.findViewById(R.id.wasteful_region_name);
            mViewHolder.light_description = (TextView) convertView.findViewById(R.id.light_description);
            mViewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            mViewHolder.plugload = (TextView) convertView.findViewById(R.id.plugload);
            mViewHolder.notification = (ImageView) convertView.findViewById(R.id.notification);
            convertView.setTag(mViewHolder);

        }
        else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.name.setText(mparent.getName());

        int imageResource = mContext.getResources().getIdentifier("@drawable/plug", null, mContext.getPackageName());

        if(mparent.getLightDescription().equalsIgnoreCase("ON"))
        {
            mViewHolder.type = "light";
            mViewHolder.light_description.setText("Light "+mparent.getLightDescription());
            imageResource = mContext.getResources().getIdentifier("@drawable/lightbulb", null, mContext.getPackageName());
            mViewHolder.plugload.setText("No Occupants");

        }else{
            mViewHolder.light_description.setText("");
            mViewHolder.plugload.setText("Plugload " + mparent.getPlugload() + " kW");
            mViewHolder.type = "plugload";
        }

        Drawable res = mContext.getResources().getDrawable(imageResource);
        mViewHolder.icon.setImageDrawable(res);

        int imageResource2 = mContext.getResources().getIdentifier("@drawable/envelope_icon1", null, mContext.getPackageName());
        Drawable res2 = mContext.getResources().getDrawable(imageResource2);
        mViewHolder.notification.setImageDrawable(res2);

        final String type = mViewHolder.type;
        mViewHolder.icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if( type.equalsIgnoreCase("plugload")) {
                    Intent intent = new Intent(mContext, TurnApplianceOffActivity.class);
                    mContext.startActivity(intent);
                }
                else{
                    Intent intent2 = new Intent(mContext, TurnLightOffActivity.class);
                    mContext.startActivity(intent2);
                }
            }
        });

        mViewHolder.notification.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(mContext, SendNotificationActivity.class);
                intent.putExtra(SendNotificationActivity.EXTRA_REGION_NAME, mparent.getName());
                mContext.startActivity(intent);
            }
        });

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

    public void setParents(ArrayList<WastefulRegionListParent> newparent){
        parents = newparent;
    }


    static class ViewHolder {

        private TextView name;
        private TextView light_description;
        private ImageView icon;
        private TextView plugload;
        private String type;
        private ImageView notification;

    }

}
