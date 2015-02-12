package fiu.ssobec;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.view.ViewGroup.LayoutParams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import fiu.ssobec.Activity.MyZonesActivity;
import fiu.ssobec.Activity.ZonesDescriptionActivity;

/**
 * Created by Maria on 2/4/2015.
 */
public class ButtonAdapter extends BaseAdapter {

    private Context mContext;
    //private HashMap<Integer, String> data;

    private ArrayList zone_names;
    private ArrayList zone_id;

    public ButtonAdapter(Context c) {
        mContext = c;
        zone_names = new ArrayList();
        zone_id = new ArrayList();
    }

    @Override
    public int getCount() {

        return MyZonesActivity.zoneNames.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {

        Button b;
        if (convertView == null) {
            b = new Button(mContext);
            b.setLayoutParams(new GridView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            b.setPadding(5, 5, 5, 5);
            b.setTextSize(0xa);
            b.setTextColor(Color.BLACK);
        } else {
            b = (Button) convertView;
        }
        b.setBackgroundColor(Color.parseColor("#ff6bb3ff"));

        //b.setText(MyZonesActivity.zoneNames.get(position)); //set name of the button as name of the region
        //b.setId(position); //set id of the button as region_id
        b.setText((String) zone_names.get(position));
        b.setId((int) zone_id.get(position));

        System.out.println("Adding to button adapter id: "+zone_id.get(position).toString());
        System.out.println("Adding name: "+zone_names.get(position).toString());

        final int iposition = position;
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ZonesDescriptionActivity.class);
                intent.putExtra("id",iposition);
                mContext.startActivity(intent);
            }
        });

        return b;
    }

    //pass the data
    public void setListData(ArrayList<String> zone_n, ArrayList<Integer> zone_i)
    {
        System.out.println("setListData");
        zone_names = zone_n;
        zone_id = zone_i;
    }
}
