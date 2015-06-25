package fiu.ssobec.Activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fiu.ssobec.Adapters.WastefulRegionListAdapter;
import fiu.ssobec.AdaptersUtil.WastefulRegionListParent;
import fiu.ssobec.DataAccess.ExternalDatabaseController;
import fiu.ssobec.R;

/**
 * Created by diana on June 2015.
 */
public class WastefulRegionsActivity extends ActionBarActivity {
    public static final String LOG_TAG = "WastefulRegionsActivity";
    public static final String GETWasteRegions_PHP = "http://smartsystems-dev.cs.fiu.edu/getwastefulregions.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "Starting WastefulRegionsActivity");
        List<NameValuePair> emptyarr = new ArrayList<>(1);

        String res = null;
        ArrayList<WastefulRegionListParent> wasteful_regions_list = new ArrayList<WastefulRegionListParent>();

        //Get the regions that are currently wasteful based on plugload and lights on without occupants
        try {
            res = new ExternalDatabaseController((ArrayList<NameValuePair>) emptyarr, GETWasteRegions_PHP).send();

            Log.i(LOG_TAG, "Result: " + res);

            JSONObject obj;

            try {
                obj = new JSONObject(res);
                JSONObject myobj;
                int j=0;
                while (obj.has(j + "") && j < obj.length()) {
                    myobj = obj.getJSONObject(j + "");
                    String name = myobj.getString("region_name");
                    String light_description = myobj.getString("description");
                    double plugload = myobj.getDouble("plugload");

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



        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_wasteful_regions);
        ListView listview_wasteful_regions = (ListView) findViewById(R.id.wasteful_regions);
        WastefulRegionListAdapter wastefulRegionsListAdapter = new WastefulRegionListAdapter(this);
        wastefulRegionsListAdapter.setParents(wasteful_regions_list);
        listview_wasteful_regions.setAdapter(wastefulRegionsListAdapter);
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
