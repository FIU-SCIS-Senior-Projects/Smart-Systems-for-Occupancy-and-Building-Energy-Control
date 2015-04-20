package fiu.ssobec.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fiu.ssobec.Adapters.MyZoneListAdapter;
import fiu.ssobec.AdaptersUtil.ZoneListParent;
import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.DataAccess.ExternalDatabaseController;
import fiu.ssobec.R;

public class AddZoneActivity extends ActionBarActivity {

    // Search EditText
    public static final String GETALLZONES_PHP = "http://smartsystems-dev.cs.fiu.edu/getallzones.php";
    private static DataAccessUser data_access; //data access variable for user


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_zone);

        //Declare the access to the SQLite table for user
        data_access = new DataAccessUser(this);

        //Open the data access to the tables
        try { data_access.open(); } catch (SQLException e) { e.printStackTrace(); }

        FragmentManager fm = getSupportFragmentManager();

        //get Zones from Database
        ArrayList<ZoneListParent> parents = getZoneList();
        ListView mListView = (ListView) findViewById(R.id.zonelist_view);
        MyZoneListAdapter myZoneListAdapter = new MyZoneListAdapter(this, fm);
        myZoneListAdapter.setParents(parents);
        mListView.setAdapter(myZoneListAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_zone, menu);
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

    private ArrayList<ZoneListParent> getZoneList(){
        //access the db and get all the zones
        //boolean: zone already added
        List<NameValuePair> id_and_timestamp = new ArrayList<>(1);

        ArrayList<ZoneListParent> zoneListParentArrayList = new ArrayList<>();

        try {
            String res = new ExternalDatabaseController((ArrayList<NameValuePair>) id_and_timestamp,GETALLZONES_PHP).send();
            Log.i("AddZoneActivity", "Response from Database for table: " + res);

            int j=0;
            JSONObject obj =  new JSONObject(res);
            JSONObject myobj;

            while (obj.has(j + "")) {
                myobj = obj.getJSONObject(j + "");
                int zone_id = myobj.getInt("region_id");
                String zone_name  = myobj.getString("region_name");

                ZoneListParent zoneListParent = new ZoneListParent();

                if(data_access.getZone(zone_id) != null)
                    zoneListParent.setZone_added(false);
                else
                    zoneListParent.setZone_added(true);

                zoneListParent.setZone_id(zone_id);
                zoneListParent.setZone_name(zone_name);
                zoneListParentArrayList.add(zoneListParent);
                j++;
            }


        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return zoneListParentArrayList;
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
