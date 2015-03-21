package fiu.ssobec.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.androidplot.pie.PieChart;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;

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

        setContentView(R.layout.activity_zones_description2);

        PieChart pie = (PieChart) findViewById(R.id.mySimplePieChart);

        Segment s1 = new Segment("Cooling", 7);
        Segment s2  = new Segment("Lighting", 20);
        Segment s3 = new Segment("PlugLoad", 15);

        SegmentFormatter sf1 = new SegmentFormatter();
        sf1.configure(getApplicationContext(), R.xml.pie_segment_formatter1);

        SegmentFormatter sf2 = new SegmentFormatter();
        sf2.configure(getApplicationContext(), R.xml.pie_segment_formatter2);

        SegmentFormatter sf3 = new SegmentFormatter();
        sf3.configure(getApplicationContext(), R.xml.pie_segment_formatter3);

        pie.addSeries(s1, sf1);
        pie.addSeries(s2, sf2);
        pie.addSeries(s3, sf3);

        pie.getBorderPaint().setColor(Color.TRANSPARENT);
        pie.getBackgroundPaint().setColor(Color.TRANSPARENT);
        pie.setPlotMargins(0, 0, 0, 0);
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
