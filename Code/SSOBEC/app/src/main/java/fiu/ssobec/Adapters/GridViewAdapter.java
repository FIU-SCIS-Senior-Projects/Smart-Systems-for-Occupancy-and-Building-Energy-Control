package fiu.ssobec.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import fiu.ssobec.Activity.MyZonesActivity;
import fiu.ssobec.Activity.ZonesDescriptionActivity;
import fiu.ssobec.AdaptersUtil.RewardListParent;
import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.DataAccess.ExternalDatabaseController;
import fiu.ssobec.Model.Zones;
import fiu.ssobec.R;

/**
 * Created by irvincardenas on 5/27/15.
 */
public class GridViewAdapter extends BaseSwipeAdapter {

    private Context mContext;

    private ArrayList zone_names; //Each button will display the name of each zone
    private ArrayList zone_id; //Each button will have an id according to each zone

    private ArrayList<Zones> currentZones;

    public GridViewAdapter(Context mContext, ArrayList zone_names, ArrayList zone_id) {
        this.mContext = mContext;
        this.zone_names = zone_names;
        this.zone_id = zone_id;
        this.currentZones = new ArrayList<Zones>();

        for(int i = 0; i < zone_names.size(); i++) {
            currentZones.add(new Zones((Integer) zone_id.get(i), (String) zone_names.get(i)));
        }
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_grid;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.grid_item, null);
        final SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));

//        swipeLayout.setId((int) zone_id.get(position));

        final int viewPosition = position;

//        swipeLayout.setOnClickListener(new SwipeLayout.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        /*
        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });

        swipeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                swipeLayout.open(true);
                return false;
            }
        });

        v.findViewById(R.id.top_layer).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                swipeLayout.open(true);
                return false;
            }
        });

        v.findViewById(R.id.top_layer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ZonesDescriptionActivity.class);

                //send the region_id or button_id to the ZonesDescriptionActivity
                intent.putExtra("button_id", currentZones.get(position).getZone_id());
                mContext.startActivity(intent);
            }
        });
        */

        v.findViewById(R.id.trash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a = -1;
                //Toast.makeText(mContext, "Delete: " + currentZones.get(viewPosition).getZone_name(), Toast.LENGTH_SHORT).show();
                swipeLayout.close(true);
                TextView zoneTV = (TextView)swipeLayout.findViewById(R.id.position);
                String zoneIdStr = zoneTV.getText().toString().trim();
                int zoneId = Integer.parseInt(zoneIdStr);

                // Remove user-region pair from DB
                //DataAccessUser.getInstance(mContext).userUnfollowZone(MyZonesActivity.user_id,viewPosition);

                userUnfollowZone(MyZonesActivity.user_id,zoneId);

                for(int i = 0; i < currentZones.size(); i++){
                    if(currentZones.get(i).getZone_id() == zoneId){
                        a = i;
                    }
                }

                Toast.makeText(mContext, "user: " + MyZonesActivity.user_id
                        + "zone: " + zoneId + " "
                        + currentZones.get(a).getZone_name(), Toast.LENGTH_SHORT).show();

                currentZones.remove(a);
                zone_names.remove(a);
                zone_id.remove(a);
                notifyDataSetChanged();
            }
        });

        return v;
    }

    public boolean userUnfollowZone(int userId, int zoneId) {

        ArrayList<RewardListParent> parents = new ArrayList<>();

        List<NameValuePair> emptyarr = new ArrayList<>(1);

        emptyarr.add( new BasicNameValuePair("region_id", Integer.toString(zoneId) ));
        emptyarr.add( new BasicNameValuePair("user_id", Integer.toString(userId) ));

        String res=null;

        try {
            res = new ExternalDatabaseController((ArrayList<NameValuePair>) emptyarr,
                    "http://smartsystems-dev.cs.fiu.edu/unfollowZone.php").send();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void fillValues(int position, View convertView) {
        //convertView.setId((Integer) zone_id.get(position));
        TextView t = (TextView)convertView.findViewById(R.id.position);
        t.setText( String.valueOf( currentZones.get(position).getZone_id() ) + " " );

        TextView zoneName = (TextView)convertView.findViewById(R.id.zone_name);
//        zoneName.setText((String) zone_names.get(position));
        zoneName.setText(currentZones.get(position).getZone_name());
    }

    public void removeItem(int position){
        currentZones.remove(position);
    }

    @Override
    public int getCount() {
        return currentZones.size();
    }

    @Override
    public Object getItem(int position) {
        return currentZones.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setListData(ArrayList<String> zone_n, ArrayList<Integer> zone_i)
    {
        zone_names = zone_n;
        zone_id = zone_i;
        for(int i = 0; i < zone_names.size(); i++) {
            currentZones.add(new Zones((Integer) zone_id.get(i), (String) zone_names.get(i)));
        }
    }
}