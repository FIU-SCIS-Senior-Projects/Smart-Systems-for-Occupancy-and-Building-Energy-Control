package fiu.ssobec.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import fiu.ssobec.Activity.LoginActivity;
import fiu.ssobec.ButtonAdapter;
import fiu.ssobec.Database;
import fiu.ssobec.R;


public class MyZonesActivity extends ActionBarActivity {

    static final String STATE_USER_ID = "User's ID";
    private GridView gridViewButtons;
    public static ArrayList<String> zoneNames;

    public final static String USER_ID = "com.fiu.ssobec.ID";

    int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_zones);

        Intent intent = getIntent();

        int i=0;
        user_id = intent.getIntExtra(USER_ID, 0);

        System.out.println("User ID: "+user_id);

        String str_user_id = user_id+"";
        List<NameValuePair> userId = new ArrayList<>(1);

        String res="";
        userId.add(new BasicNameValuePair("user_id",str_user_id.toString().trim()));
        //send the user_id to zonepost.php
        try {
            res = new Database((ArrayList<NameValuePair>) userId, "http://smartsystems-dev.cs.fiu.edu/zonepost.php").send();

            System.out.println("Zone Response is: "+res);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        zoneNames = new ArrayList<>();
        zoneDetails(res);

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

    public void zoneDetails(String response)
    {
        String str_before = "";
       // String zone_id, zone_name;
        StringTokenizer stringTokenizer = new StringTokenizer(response, ":");

        System.out.println("User Details");
        while (stringTokenizer.hasMoreElements()) {

            String temp = stringTokenizer.nextElement().toString();
            if (str_before.equalsIgnoreCase("id"))
            {
                System.out.println("id: "+temp);
            }
            else if (str_before.equalsIgnoreCase("name"))
            {
                System.out.println("name: "+temp);
                zoneNames.add(temp);
            }

            str_before = temp;
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(STATE_USER_ID, user_id);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


}
