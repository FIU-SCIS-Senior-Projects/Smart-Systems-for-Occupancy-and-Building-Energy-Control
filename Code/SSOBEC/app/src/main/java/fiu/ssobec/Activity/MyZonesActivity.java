package fiu.ssobec.Activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import fiu.ssobec.ButtonAdapter;
import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.DataAccess.ExternalDatabaseController;
import fiu.ssobec.Model.User;
import fiu.ssobec.R;
import fiu.ssobec.Synchronization.SyncUtils;


/*
    MyZonesActivity is our Main and Launcher Activity
    Our application will only need that the user log in once.
    A column in the UserSQLiteDatabase 'loggedIn'
* */

public class MyZonesActivity extends ActionBarActivity {

    public static final String LOG_TAG = "MyZonesActivity";

    public static final String GETZONES_PHP = "http://smartsystems-dev.cs.fiu.edu/zonepost.php";
    public static ArrayList<String> zoneNames;
    public static ArrayList<Integer> zoneIDs;
    private static DataAccessUser data_access; //data access variable for user

    public static int user_id;

    // Global variables
    // A content resolver for accessing the provider
    ContentResolver mResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_zones);

        //Declare the access to the SQLite table for user
        data_access = new DataAccessUser(this);

        //Declare the access to the SQLite table for zones`
        //data_access_zones = new DataAccessZones(this);

        //Open the data access to the tables
        try {
            data_access.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Declare the object user
        User user = null;

        if(data_access.doesTableExists())
            user = data_access.getUser(1); //Get me a User that is currently logged in, into the
                                            //system: loggedIn == 1.

        //If a user that is logged in into the system is
        //not found then start a new LoginActivity
        if(user == null)
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        //User that is currently logged in is found
        else
        {
            SyncUtils.CreateSyncAccount(this);
            SyncUtils.TriggerRefresh();
            user_id = user.getId(); //Get the ID of the user

            List<NameValuePair> userId = new ArrayList<>(1);

            String res = null;
            userId.add(new BasicNameValuePair("user_id", (user_id + "").trim()));

            //send the user_id to zonepost.php and get the zones
            try {
                res = new ExternalDatabaseController((ArrayList<NameValuePair>) userId, GETZONES_PHP).send();
                System.out.println("Zone Response is: "+res);
            } catch (InterruptedException e) {
                Log.e(LOG_TAG, "Database Interrupted Exception thrown: "+e.getMessage());
                e.printStackTrace();
            }

            zoneNames = new ArrayList<>();
            zoneIDs = new ArrayList<>();

            if(res != null)
                zoneDetails(res);

            //Set buttons in a Grid View order
            GridView gridViewButtons = (GridView) findViewById(R.id.grid_view_buttons);
            ButtonAdapter m_badapter = new ButtonAdapter(this);
            m_badapter.setListData(zoneNames, zoneIDs);
            gridViewButtons.setAdapter(m_badapter);
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

    public void zoneDetails(String response)
    {
        int id = 0;
        String str_before = "";
        StringTokenizer stringTokenizer = new StringTokenizer(response, ":");

        while (stringTokenizer.hasMoreElements()) {

            String temp = stringTokenizer.nextElement().toString();

            if (str_before.equalsIgnoreCase("id"))
            {
                id = Integer.parseInt(temp);
            }
            else if (str_before.equalsIgnoreCase("name"))
            {
                zoneNames.add(temp);
                zoneIDs.add(id);

                //If zone is not in the DB, add new zone
                if (data_access.getZone(id) == null)
                {
                    System.out.println("Create Zone!");
                    data_access.createZones(temp, id);
                }
            }
            str_before = temp;
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

