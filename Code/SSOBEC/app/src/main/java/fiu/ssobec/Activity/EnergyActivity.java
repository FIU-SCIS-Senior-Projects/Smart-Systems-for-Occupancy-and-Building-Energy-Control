package fiu.ssobec.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.DataAccess.Database;
import fiu.ssobec.R;

public class EnergyActivity extends ActionBarActivity {


    private DataAccessUser data_access;
    private String app_title="";
    int energy_val = 0;
    String time_stamp="";
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        data_access = new DataAccessUser(this);

        try {
            System.out.println("Open data access");
            data_access.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        System.out.println("Activity Intent: "+intent.toString());

        app_title = getIntent().getStringExtra(ZonesDescriptionActivity.ACTIVITY_NAME);
        this.setTitle(app_title);

        switch (app_title)
        {
            case "Temperature":
                break;
            case "Occupancy":
                getOccupancy();
                break;
        }

        setContentView(R.layout.activity_energy);
        mTextView = (TextView) findViewById(R.id.EnergyValueTextView);

        mTextView.setText("Current occupancy: "+energy_val);
    }

    private void getOccupancy()
    {
        System.out.println("Get occupancy from region_id: "+ZonesDescriptionActivity.regionID);

        //Add the region id to the NameValuePair ArrayList;
        List<NameValuePair> regionId = new ArrayList<>(1);
        regionId.add(new BasicNameValuePair("region_id",(ZonesDescriptionActivity.regionID+"").toString().trim()));

        String res = "";
        try {
            res = new Database((ArrayList<NameValuePair>) regionId, "http://smartsystems-dev.cs.fiu.edu/occupancypost.php").send();
            System.out.println("Occupancy Response is: "+res);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        parseDatabaseResponse(res);
        System.out.println("Current occupancy: "+energy_val);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_zones, menu);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void parseDatabaseResponse(String response)
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
