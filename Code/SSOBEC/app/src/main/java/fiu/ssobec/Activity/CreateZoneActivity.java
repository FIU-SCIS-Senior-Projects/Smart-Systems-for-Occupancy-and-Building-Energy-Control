package fiu.ssobec.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import fiu.ssobec.DataAccess.ExternalDatabaseController;
import fiu.ssobec.R;

//Created by Diana 5/27/2015

public class CreateZoneActivity extends ActionBarActivity {

    public static final String LOG_TAG = "CreateZoneActivity";
    public static final String CREATEZONE_PHP = "http://smartsystems-dev.cs.fiu.edu/createzone.php";
    TextView warning_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_zone);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_zone, menu);
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


    //onClick Create Zone, create the new Zone with its attributes
    public void createZone(View view){
        List<NameValuePair> new_zone_info = new ArrayList<>(3);
        System.out.println("Create Zone button works");

        String zone_name = ((EditText) findViewById(R.id.zone_name_field)).getText().toString();
        String zone_location = ((EditText) findViewById(R.id.zone_location_field)).getText().toString();
        String zone_windows = ((EditText) findViewById(R.id.zone_windows_field)).getText().toString();

        if(!zone_name.isEmpty())
        {
            new_zone_info.add(new BasicNameValuePair("region_name", zone_name.trim()));
            new_zone_info.add(new BasicNameValuePair("location", zone_location.trim()));
            new_zone_info.add(new BasicNameValuePair("windows", zone_windows.trim()));

            String res = "";
            //Create a new Zone
            try {
                res = new ExternalDatabaseController((ArrayList<NameValuePair>) new_zone_info,
                        CREATEZONE_PHP).send();

                warning_msg = (TextView) findViewById(R.id.warning_message_view);
                runOnUiThread(new Runnable() {

                    public void run() {
                        warning_msg.setText("");
                    }

                });

                Log.i(LOG_TAG, "Insert DB Result: " + res);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(res.equalsIgnoreCase("successful")){
                Intent intent = new Intent(this, MyZonesActivity.class);
                startActivity(intent);
            }
            else{

                Toast.makeText(getApplicationContext(), "Unable to create zone on DB", Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
            warning_msg = (TextView) findViewById(R.id.warning_message_view);
            runOnUiThread(new Runnable() {

                public void run() {
                    warning_msg.setText("Zone Name cannot be empty!");
                }

            });
        }

    }
}
