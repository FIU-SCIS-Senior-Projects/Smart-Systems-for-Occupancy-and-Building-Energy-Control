package fiu.ssobec.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.R;
import fiu.ssobec.Synchronization.SyncUtils;

public class EnergyActivity extends ActionBarActivity {


    private DataAccessUser data_access;
    private String app_title="";
    int energy_val = 0;
    String time_stamp="";
    TextView mTextView;
    TextView mTextView1;
    TextView mTextView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SyncUtils.CreateSyncAccount(this);
        SyncUtils.TriggerRefresh();

        data_access = new DataAccessUser(this);


        try {
            System.out.println("Open data access");
            data_access.open();
            System.out.println("Open data access of Temperature");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        System.out.println("Activity Intent: "+intent.toString());

        //Get the title of the activity
        app_title = getIntent().getStringExtra(ZonesDescriptionActivity.ACTIVITY_NAME);

        this.setTitle(app_title);

        setContentView(R.layout.activity_energy);
        mTextView = (TextView) findViewById(R.id.EnergyValueTextView);

        switch (app_title)
        {
            case "Temperature":
                setContentView(R.layout.activity_temperature);
                mTextView1 = (TextView) findViewById( R.id.Fahrenheit);
                mTextView2 = (TextView) findViewById( R.id.Celsius);

                getTemperature();
                break;

            case "Occupancy":
                getOccupancy();
                break;

            case "PlugLoad":
                getPlugLoad();
                break;

            case "Lighting":
                getLighting();
                break;

        }

    }

    // Convert to Celsius
    private float convertFahrenheitToCelsius(float fahrenheit) {
        return ((fahrenheit - 32) * 5 / 9);
    }

    private void getOccupancy()
    {
        System.out.println("Get occupancy from region_id: "+ZonesDescriptionActivity.regionID);

        int zone_id = ZonesDescriptionActivity.regionID;

        ArrayList<String> info = data_access.getLatestOccupancy(zone_id);

        if(info == null)
        {
            mTextView.setText("No Data");
        }
        else
        {
            mTextView.setText("Current Occupancy: "+info.get(1)+"\nTime:"+info.get(0));
        }
    }

    private void getTemperature()
    {
        System.out.println("Get temperature from region_id: "+ZonesDescriptionActivity.regionID);

        int zone_id = ZonesDescriptionActivity.regionID;

        ArrayList<String> info = data_access.getLatestTemperature(zone_id);

        if(info == null)
        {
            mTextView1.setText("No Data");
        }
        else
        {
            TextView time_stamp = (TextView) findViewById(R.id.temperatureTimeStamp);
            mTextView1.setText("Fahrenheit: "+info.get(1)+(char) 0x00B0+"F");
            mTextView2.setText("Celsius: "+convertFahrenheitToCelsius(Float.parseFloat(info.get(1)))+(char) 0x00B0+"C");
            time_stamp.setText(info.get(0)); //set time stamp
        }


    }

    private void getPlugLoad()
    {
        System.out.println("Get plug load from region_id: "+ZonesDescriptionActivity.regionID);

        int zone_id = ZonesDescriptionActivity.regionID;

        ArrayList<String> info = data_access.getLatestPlugLoad(zone_id);

        if(info == null)
        {
            mTextView.setText("No Data");
        }
        else
        {
            mTextView.setText("Current PlugLoad: "+info.get(1)+"\nTime:"+info.get(0));
        }
    }

    private void getLighting()
    {
        System.out.println("Get occupancy from region_id: "+ZonesDescriptionActivity.regionID);

        int zone_id = ZonesDescriptionActivity.regionID;

        ArrayList<String> info = data_access.getLatestLighting(zone_id);

        if(info == null)
        {
            mTextView.setText("No Data");
        }
        else
        {
            mTextView.setText("The Lighting in the room is: "+info.get(1)+"\nTime:"+info.get(0));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_energy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_logout:
                Intent intent = new Intent(this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
                data_access.userLogout(MyZonesActivity.user_id);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                return true;

            case R.id.menu_refresh:
                System.out.println("Refresh!");
                SyncUtils.TriggerRefresh();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void parseDatabaseResponseOcc(String response)
    {
        String str_before = "";
        StringTokenizer stringTokenizer = new StringTokenizer(response, "|");

        while (stringTokenizer.hasMoreElements()) {

            String temp = stringTokenizer.nextElement().toString();

            if (str_before.equalsIgnoreCase("time_stamp"))
            {
                time_stamp = temp;
                System.out.println("Time: "+temp);
            }
            else if (str_before.equalsIgnoreCase("occupancy"))
            {
                energy_val = Integer.parseInt(temp);
                System.out.println("Occupancy: "+temp);
            }
            str_before = temp;
        }
    }

    private void parseDatabaseResponseTemp(String response)
    {
        String str_before = "";
        StringTokenizer stringTokenizer = new StringTokenizer(response, "|");

        while (stringTokenizer.hasMoreElements()) {

            String temp = stringTokenizer.nextElement().toString();

            if (str_before.equalsIgnoreCase("time_stamp"))
            {
                time_stamp = temp;
                System.out.println("Time: "+temp);
            }
            else if (str_before.equalsIgnoreCase("temperature"))
            {
                energy_val = Integer.parseInt(temp);
                System.out.println("Temperature: "+temp);
            }
            str_before = temp;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            data_access.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        data_access.close();
    }
}
