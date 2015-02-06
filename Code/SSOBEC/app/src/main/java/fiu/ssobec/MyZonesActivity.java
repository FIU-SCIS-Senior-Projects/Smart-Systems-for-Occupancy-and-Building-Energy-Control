package fiu.ssobec;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import java.util.ArrayList;


public class MyZonesActivity extends ActionBarActivity {

    private GridView gridViewButtons;

    public static ArrayList<String> zoneNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_zones);

        zoneNames = new ArrayList<>();
        zoneNames.add("Zone 1");
        zoneNames.add("Zone 2");

        //Set buttons in a Grid View order
        gridViewButtons = (GridView) findViewById(R.id.grid_view_buttons);
        gridViewButtons.setAdapter(new ButtonAdapter(this));

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
                startActivity(intent);
                return true;
            case R.id.action_settings:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
