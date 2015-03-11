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
import java.util.List;

import fiu.ssobec.DataAccess.DataAccessOwm;
import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.DataAccess.ExternalDatabaseController;

/**
 * Created by Dalaidis on 2/17/2015.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String LOG_TAG = "SyncAdapter";

    public static final String OCCUPANCY_PHP = "http://smartsystems-dev.cs.fiu.edu/occupancypost.php";
    public static final String TEMPERATURE_PHP = "http://smartsystems-dev.cs.fiu.edu/temperaturepost.php";
    public static final String LIGHTING_PHP = "http://smartsystems-dev.cs.fiu.edu/lightingpost.php";
    public static final String PLUGLOAD_PHP = "http://smartsystems-dev.cs.fiu.edu/plugloadpost2.php";

    public static final String DB_NODATA = "No Data";
    public static final String ZONE_COLUMN_ID = "region_id";
    public static final String LAST_TIME_STAMP = "last_time_stamp";
    public static final String TIME_STAMP = "time_stamp";

    public static final String PLUG_APPLIANCE_TYPE = "appliance_type";
    public static final String PLUG_APPLIANCE_NAME = "appliance_name";
    public static final String ENERGY_USAGE = "energy_usage_kwh";

    private Context mcontext;
    private DataAccessUser data_access;

    /**
     * Creates an {@link android.content.AbstractThreadedSyncAdapter}.
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        data_access = new DataAccessUser(context);
        mcontext = context;

    }

    /**
     * Perform a sync for this account.
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.i(LOG_TAG, "Beginning network synchronization");

        try {
            data_access.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Get all the zones and synchronize all the data specific for each of them
        List<Integer> region_id = data_access.getAllZoneID();
        for (Integer id : region_id) {
            getOccupancyData(id);
            getTemperatureData(id);
            getPlugLoadData(id);
            getLightingData(id);
        }

        try {
            DataAccessOwm dataAccessOwm = new DataAccessOwm(mcontext);
            dataAccessOwm.saveWeatherData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(LOG_TAG, "Finishing network synchronization");

        data_access.close();
    }

    private void getOccupancyData(int RegionID)
    {
        String last_time_stamp = data_access.getLastTimeStamp(RegionID);

        List<NameValuePair> id_and_timestamp = new ArrayList<>(2);

        id_and_timestamp.add(new BasicNameValuePair(ZONE_COLUMN_ID, (RegionID + "").toString().trim()));
        id_and_timestamp.add(new BasicNameValuePair(LAST_TIME_STAMP, (last_time_stamp).toString().trim()));

        try {
            String res = new ExternalDatabaseController((ArrayList<NameValuePair>) id_and_timestamp, OCCUPANCY_PHP).send();
            System.out.println("Sync Occupancy Response is: "+res);

            if(!res.equalsIgnoreCase(DB_NODATA)) {
                JSONObject obj = new JSONObject(res);
                JSONArray arr = obj.getJSONArray("occupancy_obj");

                for (int i = 0; i < arr.length(); i++) {
                    int occup = arr.getJSONObject(i).getInt("occupancy");
                    String time_stamp = arr.getJSONObject(i).getString(TIME_STAMP);
                    System.out.println("Occupancy: " + occup + ", Time Stamp: " + time_stamp);

                    if (!time_stamp.equalsIgnoreCase("null"))
                        data_access.createOccupancy(RegionID, time_stamp, occup);
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getTemperatureData(int RegionID)
    {
        System.out.println("Get Temperature from ID: "+RegionID);
        String last_time_stamp = data_access.getLastTimeStamp_temp(RegionID);

        //put a small database code
        //Add the region id to the NameValuePair ArrayList;
        List<NameValuePair> id_and_timestamp = new ArrayList<>(2);

        id_and_timestamp.add(new BasicNameValuePair(ZONE_COLUMN_ID, (RegionID + "").trim()));
        id_and_timestamp.add(new BasicNameValuePair(LAST_TIME_STAMP, (last_time_stamp).trim()));

        try {
            String res = new ExternalDatabaseController((ArrayList<NameValuePair>) id_and_timestamp, TEMPERATURE_PHP).send();
            System.out.println("Sync Temperature Response is: "+res);

            if(!res.equalsIgnoreCase(DB_NODATA)) {
               // Log.i(LOG_TAG, "*-"+DB_NODATA+"="+res+"-*");
                JSONObject obj = new JSONObject(res);
                JSONArray arr = obj.getJSONArray("temperature_arr");

                for (int i = 0; i < arr.length(); i++) {
                    int temp = arr.getJSONObject(i).getInt("temperature");
                    String time_stamp = arr.getJSONObject(i).getString(TIME_STAMP);
                    System.out.println("Temperature: " + temp + ", Time Stamp: " + time_stamp);

                    if (!time_stamp.equalsIgnoreCase("null"))
                        data_access.createTemperature(RegionID, time_stamp, temp);
                }
            }

        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void getPlugLoadData(int RegionID)
    {
        System.out.println("Get PlugLoad from ID: "+RegionID);
        String last_time_stamp = data_access.getLastTimeStamp_plugLoad(RegionID);

        //put a small database code
        //Add the region id to the NameValuePair ArrayList;
        List<NameValuePair> id_and_timestamp = new ArrayList<>(2);

        id_and_timestamp.add(new BasicNameValuePair(ZONE_COLUMN_ID, (RegionID + "").trim()));
        id_and_timestamp.add(new BasicNameValuePair(LAST_TIME_STAMP, (last_time_stamp).trim()));

        try {

            String res = new ExternalDatabaseController((ArrayList<NameValuePair>) id_and_timestamp, PLUGLOAD_PHP).send();
            System.out.println("Sync Plug Load Response is: "+res);

            if(!res.equalsIgnoreCase(DB_NODATA)) {
                JSONObject obj = new JSONObject(res);
                JSONArray arr = obj.getJSONArray("plugload_obj");

                for (int i = 0; i < arr.length(); i++) {
                    String plugLoad = arr.getJSONObject(i).getString("status");
                    String time_stamp = arr.getJSONObject(i).getString(TIME_STAMP);

                    //, String app_name, String app_type, int energy_usage_kwh
                    if (!time_stamp.equalsIgnoreCase("null"))
                        data_access.createPlugLoad(RegionID, time_stamp, plugLoad,
                        arr.getJSONObject(i).getString(PLUG_APPLIANCE_NAME),
                        arr.getJSONObject(i).getString(PLUG_APPLIANCE_TYPE),
                        arr.getJSONObject(i).getInt(ENERGY_USAGE));
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getLightingData(int RegionID)
    {
        System.out.println("Get PlugLoad from ID: "+RegionID);
        String last_time_stamp = data_access.getLastLightTimeStamp(RegionID);

        //put a small database code
        //Add the region id to the NameValuePair ArrayList;
        List<NameValuePair> id_and_timestamp = new ArrayList<>(2);

        id_and_timestamp.add(new BasicNameValuePair(ZONE_COLUMN_ID, (RegionID + "").trim()));
        id_and_timestamp.add(new BasicNameValuePair(LAST_TIME_STAMP, (last_time_stamp).trim()));

        try {

            String res = new ExternalDatabaseController((ArrayList<NameValuePair>) id_and_timestamp, LIGHTING_PHP).send();
            System.out.println("Sync Lighting Response is: "+res);

            if(!res.equalsIgnoreCase(DB_NODATA)) {
                JSONObject obj = new JSONObject(res);
                JSONArray arr = obj.getJSONArray("lighting_obj");

                for (int i = 0; i < arr.length(); i++) {
                    String lighting = arr.getJSONObject(i).getString("lighting");
                    String time_stamp = arr.getJSONObject(i).getString(TIME_STAMP);
                    System.out.println("Lighting: " + lighting + ", Time Stamp: " + time_stamp);

                    if (!time_stamp.equalsIgnoreCase("null"))
                        data_access.createLighting(RegionID, time_stamp, lighting);
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
