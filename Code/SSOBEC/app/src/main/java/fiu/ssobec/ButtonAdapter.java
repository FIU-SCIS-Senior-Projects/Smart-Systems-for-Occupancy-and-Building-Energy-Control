package fiu.ssobec;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.view.ViewGroup.LayoutParams;

/**
 * Created by Maria on 2/4/2015.
 */
public class ButtonAdapter extends BaseAdapter {

    private Context mContext;

    public ButtonAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return 0;
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
            b.setLayoutParams(new GridView.LayoutParams(LayoutParams.FILL_PARENT, 100));
            b.setPadding(8, 8, 8, 8);
            b.setTextSize(25f);
            b.setTextColor(Color.BLACK);
        } else {
            b = (Button) convertView;
        }

        return null;
    }
}
