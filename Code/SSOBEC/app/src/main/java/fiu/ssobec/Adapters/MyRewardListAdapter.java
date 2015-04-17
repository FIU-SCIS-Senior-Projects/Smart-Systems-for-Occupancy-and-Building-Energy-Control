
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

import org.w3c.dom.Text;

import java.util.ArrayList;

import fiu.ssobec.R;
import fiu.ssobec.AdaptersUtil.RewardListParent;

/**
 * Created by Fresa on 4/10/2015.
 */
public class MyRewardListAdapter  implements ListAdapter {

    private LayoutInflater inflater;
    private ArrayList<RewardListParent> parents;

    private Context mContext;

    public MyRewardListAdapter(Context context) {
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
        final RewardListParent mparent = parents.get(position);

        if(convertView == null) {

            mViewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row_rewards_list, parent, false);


            mViewHolder.appliance_name = (TextView) convertView.findViewById(R.id.reward_name);
            mViewHolder.appliance_description = (TextView) convertView.findViewById(R.id.reward_description);
            mViewHolder.points = (TextView) convertView.findViewById(R.id.reward_points);
            mViewHolder.appliance_icon = (ImageView) convertView.findViewById(R.id.reward_icon);
            mViewHolder.zone_name = (TextView) convertView.findViewById(R.id.reward_room_name);
            convertView.setTag(mViewHolder);

        }
        else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.appliance_name.setText(mparent.getName());
        mViewHolder.appliance_description.setText(mparent.getDescription());
        mViewHolder.points.setText(mparent.getPoints());
        int imageResource = mContext.getResources().getIdentifier("@drawable/award_transparent", null, mContext.getPackageName());
        Drawable res = mContext.getResources().getDrawable(imageResource);
        mViewHolder.appliance_icon.setImageDrawable(res);
        mViewHolder.zone_name.setText(mparent.getZone_name());

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

    public void setParents(ArrayList<RewardListParent> newparent){
        parents = newparent;
    }


    static class ViewHolder {

        private  TextView appliance_name;
        private  TextView appliance_description;
        private  TextView points;
        private ImageView appliance_icon;
        private TextView zone_name;

    }

}
