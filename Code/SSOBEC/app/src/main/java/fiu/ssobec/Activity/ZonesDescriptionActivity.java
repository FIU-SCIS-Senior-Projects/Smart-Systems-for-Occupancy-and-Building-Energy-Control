package fiu.ssobec.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.sql.SQLException;

import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.R;


public class ZonesDescriptionActivity extends ActionBarActivity {

    private DataAccessUser data_access;
    public static int regionID;
    public static String ACTIVITY_NAME = "activity_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        data_access = new DataAccessUser(this);

        try {
            data_access.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();

        if(intent.getExtras() != null)
        {
            regionID = intent.getIntExtra("button_id", 0);
        }

        System.out.println("Region ID: "+regionID);

        setContentView(R.layout.activity_zones_description);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_zones, menu);
        return true;
    }

    //open energy activity for temperature view
    public void getTemperature(View view) {
        Intent intent = new Intent(this,EnergyActivity.class);
        intent.putExtra(ACTIVITY_NAME,"Temperature");
        startActivity(intent);
    }

    //open energy activity for occupancy view
    public void getOccupancy(View view) {
        Intent intent = new Intent(this,EnergyActivity.class);
        intent.putExtra(ACTIVITY_NAME,"Occupancy");
        startActivity(intent);
    }

    //open energy activity for plugLoad view
    public void getPlugLoad(View view) {
        Intent intent = new Intent(this,EnergyActivity.class);
        intent.putExtra(ACTIVITY_NAME,"PlugLoad");
        startActivity(intent);
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
