package fiu.ssobec;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

        /*
        gridViewButtons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                // Send intent to SingleViewActivity
                Intent intent = new Intent(getApplicationContext(), ZonesDescriptionActivity.class);

                System.out.println("ID: "+position);
                // Pass image index
                intent.putExtra("id", position);
                startActivity(intent);
            }
        });*/

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
