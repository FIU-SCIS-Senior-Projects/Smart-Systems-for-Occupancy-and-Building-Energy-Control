package fiu.ssobec;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import java.util.ArrayList;

import fiu.ssobec.Activity.ZonesDescriptionActivity;

/**
 * Created by Maria on 2/4/2015.
 */
public class ButtonAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList zone_names; //Each button will display the name of each zone
    private ArrayList zone_id; //Each button will have an id according to each zone

    public ButtonAdapter(Context c) {
        mContext = c;
        zone_names = new ArrayList();
        zone_id = new ArrayList();
    }

    @Override
    public int getCount() {
        return zone_names.size();
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
            b.setLayoutParams(new GridView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            b.setPadding(5, 5, 5, 5);
            b.setTextSize(0xa);
            b.setTextColor(Color.BLACK);

        } else {
            b = (Button) convertView;
        }
        b.setBackgroundColor(Color.parseColor("#ff6bb3ff"));

        //set name of the button as name of the region
        b.setText((String) zone_names.get(position));

        //set id of the button as region_id
        b.setId((int) zone_id.get(position));
        b.setTextSize(12);

        final int button_id = b.getId();
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //When a button is clicked start ZonesDescriptionActivity
                Intent intent = new Intent(mContext, ZonesDescriptionActivity.class);

                //send the region_id or button_id to the ZonesDescriptionActivity
                intent.putExtra("button_id",button_id);
                mContext.startActivity(intent);
            }
        });

        return b;
    }

    //Here we get the data we need to generate the buttons and their button id
    public void setListData(ArrayList<String> zone_n, ArrayList<Integer> zone_i)
    {
        zone_names = zone_n;
        zone_id = zone_i;
    }
}
