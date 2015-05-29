package fiu.ssobec.Adapters;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.adapters.ArraySwipeAdapter;

import java.util.ArrayList;
import java.util.List;

import fiu.ssobec.R;

/**
 * Sample usage of ArraySwipeAdapter.
 * @param <T>
 */
public class ArraySwipeAdapterSample<T> extends ArraySwipeAdapter {

    private ArrayList zone_names; //Each button will display the name of each zone
    private ArrayList zone_id; //Each button will have an id according to each zone


    public ArraySwipeAdapterSample(Context context, int resource) {
        super(context, resource);
    }

    public ArraySwipeAdapterSample(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public ArraySwipeAdapterSample(Context context, int resource, Object[] objects) {
        super(context, resource, objects);
    }

    public ArraySwipeAdapterSample(Context context, int resource, int textViewResourceId, Object[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public ArraySwipeAdapterSample(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    public ArraySwipeAdapterSample(Context context, int resource, int textViewResourceId, List objects) {
        super(context, resource, textViewResourceId, objects);
    }

    //Here we get the data we need to generate the buttons and their button id
    public void setListData(ArrayList<String> zone_n, ArrayList<Integer> zone_i)
    {
        zone_names = zone_n;
        zone_id = zone_i;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }
}