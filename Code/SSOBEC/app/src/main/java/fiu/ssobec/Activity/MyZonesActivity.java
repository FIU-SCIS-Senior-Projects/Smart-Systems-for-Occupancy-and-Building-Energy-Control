package fiu.ssobec.Activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fiu.ssobec.ButtonAdapter;
import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.DataAccess.ExternalDatabaseController;
import fiu.ssobec.Model.User;
import fiu.ssobec.R;


/*
    MyZonesActivity is our Main and Launcher Activity
    Our application will only need that the user log in once.
    A column in the UserSQLiteDatabase 'loggedIn'
* */

public class MyZonesActivity extends ActionBarActivity{

    public static final int USER_LOGGEDIN = 1;
    public static final String LOG_TAG = "MyZonesActivity";
    public static final String GETZONES_PHP = "http://smartsystems-dev.cs.fiu.edu/zonepost.php";
    private static DataAccessUser data_access; //data access variable for user
    private Location mLastLocation;

    public static int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_zones);

        //Declare the access to the SQLite table for user
        data_access = new DataAccessUser(this);

        //Open the data access to the tables
        try { data_access.open(); } catch (SQLException e) { e.printStackTrace(); }

        User user = data_access.getUser(USER_LOGGEDIN); //Get me a User that is currently logged in the system

        //If a user that is logged in into the system is not found then start a new LoginActivity
        if(user == null)
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        //User that is currently logged in is found
        else
        {
            //SyncUtils.CreateSyncAccount(this);
            //SyncUtils.TriggerRefresh();
            user_id = user.getId(); //Get the ID of the user

            List<NameValuePair> userId = new ArrayList<>(1);

            String res = null;
            userId.add(new BasicNameValuePair("user_id", (user_id+"").trim()));

            //send the user_id to zonepost.php and get the zones
            try {
                res = new ExternalDatabaseController((ArrayList<NameValuePair>) userId, GETZONES_PHP).send();
                Log.i(LOG_TAG, "Zone Response is: "+res);
            } catch (InterruptedException e) {
                Log.e(LOG_TAG, "Database Interrupted Exception thrown: "+e.getMessage());
                e.printStackTrace();

            }

            if(res != null) {
                JSONObject obj;
                JSONArray arr;
                try {
                    obj = new JSONObject(res);
                    arr = obj.getJSONArray("zone_obj");

                    for (int i = 0; i < arr.length(); i++) {
                        int region_id = arr.getJSONObject(i).getInt("region_id");
                        String region_name = arr.getJSONObject(i).getString("region_name");
                        if (!region_name.equalsIgnoreCase("null")&&(data_access.getZone(region_id)==null))
                            data_access.createZones(region_name,region_id);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //Set buttons in a Grid View order
            GridView gridViewButtons = (GridView) findViewById(R.id.grid_view_buttons);
            ButtonAdapter m_badapter = new ButtonAdapter(this);
            m_badapter.setListData(data_access.getAllZoneNames(), data_access.getAllZoneID());
            gridViewButtons.setAdapter(m_badapter);

        }
        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("FF729AD1")));
        }
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

                //Clean the Activity Stack
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;

                //Logout the user from the system by declaring the
                //loggedIn column as '0'.
                data_access.userLogout(user_id);

                //when the user logs out, take him/her to the login activity
                startActivity(intent);
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //When an Activity is resumed, open the SQLite
    //connection
    @Override
    protected void onResume() {
        super.onResume();
        try {
            data_access.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //When an Activity is left, close the
    //SQLite connection
    @Override
    protected void onPause() {
        super.onPause();
        data_access.close();
    }

}

