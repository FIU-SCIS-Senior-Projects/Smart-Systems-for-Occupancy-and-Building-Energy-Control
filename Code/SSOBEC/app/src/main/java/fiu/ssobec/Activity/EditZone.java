package fiu.ssobec.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.w3c.dom.Text;

import fiu.ssobec.AdaptersUtil.ZoneListParent;
import fiu.ssobec.R;

public class EditZone extends ActionBarActivity implements NumberPicker.OnValueChangeListener {

    public static final String EXTRA_ZONE_ID = "fiu.ssobec.Activity.extra_zone_id";
    private ZoneListParent zone;
    private EditText etName;
    private TextView tvWindows;
    private EditText etLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_zone);
        Intent i = getIntent();
        this.zone = (ZoneListParent)i.getSerializableExtra(EXTRA_ZONE_ID);

        etName = (EditText) findViewById(R.id.edit_zone_name);
        etName.setText(zone.getZone_name());

        tvWindows = (TextView)findViewById(R.id.edit_zone_windows);
        tvWindows.setText(String.valueOf(zone.getZone_windows()));
        tvWindows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });

        etLocation = (EditText) findViewById(R.id.edit_zone_location);
        etLocation.setText(zone.getZone_location());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_zone, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

        //Log.i("value is",""+newVal);

    }

    public void show()
    {

        final Dialog d = new Dialog(EditZone.this);
        d.setTitle("Number of Windows");
        d.setContentView(R.layout.dialog_edit_zone_windows);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);

        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(10);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                tvWindows.setText(String.valueOf(np.getValue()));
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();


    }
}
