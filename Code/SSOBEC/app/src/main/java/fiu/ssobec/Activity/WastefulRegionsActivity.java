package fiu.ssobec.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fiu.ssobec.Adapters.MyRewardListAdapter;
import fiu.ssobec.Adapters.WastefulRegionListAdapter;
import fiu.ssobec.AdaptersUtil.RewardListParent;
import fiu.ssobec.AdaptersUtil.WastefulRegionListParent;
import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.DataAccess.ExternalDatabaseController;
import fiu.ssobec.Model.User;
import fiu.ssobec.R;
import fiu.ssobec.Synchronization.SyncUtils;

/**
 * Created by diana on June 2015.
 */
public class WastefulRegionsActivity extends ActionBarActivity {
    public static final String LOG_TAG = "WastefulRegionsActivity";
    public static final String GETWasteRegions_PHP = "http://smartsystems-dev.cs.fiu.edu/getwastefulregions.php";
    private static DataAccessUser data_access;
    public static final int USER_LOGGEDIN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Declare the access to the SQLite table for user
        data_access = new DataAccessUser(this);

        //Open the data access to the tables
        try {
            data_access.open();
        } catch (SQLException e) {
            System.err.println(LOG_TAG + ": " + e.toString());
            e.printStackTrace();
        }

        //Synchronize Data
        SyncUtils.CreateSyncAccount(this);
        SyncUtils.TriggerRefresh();

        User user = data_access.getUser(USER_LOGGEDIN); //Get a user that is currently logged in the system

        //If a user that is logged in into the system is not found then start a new LoginActivity
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        //User that is currently logged in is found
        else {
            setContentView(R.layout.activity_wasteful_regions);
            ArrayList<WastefulRegionListParent> wasteful_regions_list = getWastefulRegions();

            ListView listview_wasteful_regions = (ListView) findViewById(R.id.wasteful_regions);

            listview_wasteful_regions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                }
            });

            WastefulRegionListAdapter wastefulRegionsListAdapter = new WastefulRegionListAdapter(this);
            wastefulRegionsListAdapter.setParents(wasteful_regions_list);
            listview_wasteful_regions.setAdapter(wastefulRegionsListAdapter);
        }
    }

private ArrayList<WastefulRegionListParent> getWastefulRegions(){
    Log.i(LOG_TAG, "Starting WastefulRegionsActivity");
    List<NameValuePair> emptyarr = new ArrayList<>(1);

    String res = "";
    ArrayList<WastefulRegionListParent> wasteful_regions_list = new ArrayList<WastefulRegionListParent>();

    //Get the regions that are currently wasteful based on plugload and lights on without occupants
    try {
        res = new ExternalDatabaseController((ArrayList<NameValuePair>) emptyarr, GETWasteRegions_PHP).send();

        Log.i(LOG_TAG, "Result: " + res);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    Log.i(LOG_TAG, "res: " + res);
    System.out.println("printout: " + res);
    if (res != null) {
        JSONObject obj;

        try {
            obj = new JSONObject(res);
            JSONObject myobj;
            int j = 0;
            while (obj.has(j + "") && j < obj.length()) {
                myobj = obj.getJSONObject(j + "");
                System.out.println("length " + obj.length());

                String name = myobj.getString("region_name");
                String light_description = myobj.getString("description");

                double plugload = 0;

                if(!myobj.isNull("plugload")){
                    plugload = myobj.getDouble("plugload");
                }

                WastefulRegionListParent record = new WastefulRegionListParent();
                record.setName(name);
                record.setLightDescription(light_description);
                record.setPlugload(plugload + "");
                wasteful_regions_list.add(record);

                j++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    return wasteful_regions_list;
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wasteful_regions, menu);
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
