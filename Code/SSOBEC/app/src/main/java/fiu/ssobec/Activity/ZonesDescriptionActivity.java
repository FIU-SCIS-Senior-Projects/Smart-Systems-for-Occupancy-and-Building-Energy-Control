package fiu.ssobec.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;

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

        setContentView(R.layout.activity_zones_description2);

        DecimalFormat df = new DecimalFormat("#.#");

        HashMap<String, Double> info = data_access.getInfoForZonesDescription(regionID);

        double total_energy =  info.get("light") + info.get("plugload");
        double percentage_light = (info.get("light")/(total_energy))*100;
        double percentage_plug = (info.get("plugload")/(total_energy))*100;

        PieChart mPieChart = (PieChart) findViewById(R.id.mySimplePieChart);

        mPieChart.addPieSlice(new PieModel("Lighting", (float) percentage_light, getResources().getColor(R.color.lighting_yellow)));
        mPieChart.addPieSlice(new PieModel("Plug Load", (float) percentage_plug, getResources().getColor(R.color.plugload_green)));

        mPieChart.startAnimation();


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_zones_description, menu);
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

    //open energy activity for lighting view
    public void getLighting(View view) {
        Intent intent = new Intent(this,EnergyActivity.class);
        intent.putExtra(ACTIVITY_NAME,"Lighting");
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

    public void predictAC (View view)
    {
        Intent intent = new Intent(this,ACConsumptionPrediction.class);
        Log.i("EnergyActivity", "Starting my new prediction for AC");
        startActivity(intent);
    }

    public void predictConsumption(View view)
    {
        Intent intent = new Intent(this,ConsumptionAppliances.class);
        Log.i("EnergyActivity", "Starting my new prediction for ConsumptionAppliances");
        startActivity(intent);
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
