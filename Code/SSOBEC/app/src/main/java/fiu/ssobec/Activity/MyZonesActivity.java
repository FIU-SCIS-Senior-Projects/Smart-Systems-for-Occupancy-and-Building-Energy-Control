package fiu.ssobec.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import fiu.ssobec.ButtonAdapter;
import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.DataAccess.DataAccessZones;
import fiu.ssobec.DataAccess.Database;
import fiu.ssobec.Model.User;
import fiu.ssobec.R;


public class MyZonesActivity extends ActionBarActivity {

    static final String STATE_USER_ID = "User's ID";
    private GridView gridViewButtons;
    public static ArrayList<String> zoneNames;
    public static ArrayList<Integer> zoneIDs;

    private HashMap<Integer, String> zone_names;

    private DataAccessUser data_access; //data access variable for user
    private DataAccessZones data_access_zones;

    public final static String USER_ID = "com.fiu.ssobec.ID";

    int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_zones);

        //Open the access to the SQLite table for user
        data_access = new DataAccessUser(this);
        data_access_zones = new DataAccessZones(this);

        try {
            data_access.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        User user = null;

        if(data_access.doesTableExists())
            user = data_access.getUser(1); //Get me a User that is currently logged in, into the
                                            //system: loggedIn == 1.
        //
        if(user == null)
        {
            System.out.println("User NOT Found on Internal DB");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else
        {
            System.out.println("User Found on Internal DB Name: "+user.getName().toString()
                                +"ID: "+user.getId());
            user_id = user.getId();

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
            zoneIDs = new ArrayList<>();
            zone_names = new HashMap<>();

            zoneDetails(res);

            //Set buttons in a Grid View order
            gridViewButtons = (GridView) findViewById(R.id.grid_view_buttons);
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
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
                data_access.userLogout(user_id); //Letting know the system that the user has logout

                startActivity(intent);
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //TODO: Save zone details in the database
    public void zoneDetails(String response)
    {

        int id = 0;
        String name="";
        try {
            data_access_zones.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String str_before = "";
        StringTokenizer stringTokenizer = new StringTokenizer(response, ":");

        while (stringTokenizer.hasMoreElements()) {

            String temp = stringTokenizer.nextElement().toString();

            if (str_before.equalsIgnoreCase("id"))
            {
                id = Integer.parseInt(temp);
                System.out.println("id: "+temp);
            }
            else if (str_before.equalsIgnoreCase("name"))
            {
                name = temp;
                System.out.println("name: "+temp);
                zoneNames.add(temp);
                zoneIDs.add(id);

                //add zone if the zone is not found in the internal database
                /*if (data_access_zones.getZone(id) == null)
                {
                    data_access_zones.createZones(name, id);
                }*/
            }
            str_before = temp;

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        data_access.close();
        data_access_zones.close();
    }
}
