package fiu.ssobec.Synchronization;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fiu.ssobec.Activity.ZonesDescriptionActivity;
import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.DataAccess.Database;

/**
 * Created by Dalaidis on 2/17/2015.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {


    public static final String LOG_TAG = "SyncAdapter";

    public static final String OCCUPANCY_PHP = "http://smartsystems-dev.cs.fiu.edu/occupancypost.php";

    private DataAccessUser data_access;


    /**
     * Creates an {@link android.content.AbstractThreadedSyncAdapter}.
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        data_access = new DataAccessUser(context);
    }

    /**
     * Perform a sync for this account. SyncAdapter-specific parameters may
     * be specified in extras, which is guaranteed to not be null. Invocations
     * of this method are guaranteed to be serialized.
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.i(LOG_TAG, "Beginning network synchronization");

        try {
            data_access.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<Integer> region_id;
        region_id = data_access.getAllZoneID();
        Iterator<Integer> region_id_itr = region_id.iterator();

        while (region_id_itr.hasNext())
        {
            int id = region_id_itr.next();
            getOccupancyData(id);
        }

        Log.i(LOG_TAG, "Finishing network synchronization");

        data_access.close();
    }

    private void getOccupancyData(int RegionID)
    {
        System.out.println("Get Occupancy from ID: "+RegionID);
        String last_time_stamp = data_access.getLastTimeStamp(RegionID);

        //put a small database code
        //Add the region id to the NameValuePair ArrayList;
        List<NameValuePair> id_and_timestamp = new ArrayList<>(2);

        id_and_timestamp.add(new BasicNameValuePair("region_id", (RegionID + "").toString().trim()));
        id_and_timestamp.add(new BasicNameValuePair("last_time_stamp", (last_time_stamp).toString().trim()));

        try {
            String res = new Database((ArrayList<NameValuePair>) id_and_timestamp, OCCUPANCY_PHP).send();
            System.out.println("Sync Occupancy Response is: "+res);

            JSONObject obj = new JSONObject(res);
            JSONArray arr = obj.getJSONArray("occupancy_obj");

            for (int i = 0; i < arr.length(); i++)
            {
                int occup = arr.getJSONObject(i).getInt("occupancy");
                String time_stamp = arr.getJSONObject(i).getString("time_stamp");
                System.out.println("Occupancy: "+occup+", Time Stamp: "+time_stamp);

                if(!time_stamp.equalsIgnoreCase("null"))
                    data_access.createOccupancy(RegionID, time_stamp, occup);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
