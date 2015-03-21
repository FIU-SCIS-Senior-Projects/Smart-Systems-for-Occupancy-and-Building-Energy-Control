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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fiu.ssobec.DataAccess.DataAccessOwm;
import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.DataAccess.ExternalDatabaseController;
import fiu.ssobec.SQLite.UserSQLiteDatabase;
import fiu.ssobec.StatisticalCalculation;

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

    //Name of the JSON Objects
    public static final String OCCUPANCY_OBJ = "occupancy_obj";
    public static final String TEMPERATURE_OBJ = "temperature_arr";
    public static final String PLUGLOAD_OBJ = "plugload_obj";
    public static final String LIGHTING_OBJ = "lighting_obj";

    private Context mcontext;
    private DataAccessUser data_access;
    private StatisticalCalculation sc;

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
        sc = new StatisticalCalculation(mcontext, data_access);
        Log.i(LOG_TAG, "SC: "+sc.getNewest_timestamp());

        List<Integer> region_id = data_access.getAllZoneID();
        /*TEST
        for (Integer id : region_id) {
            getDatabaseData(id, UserSQLiteDatabase.TABLE_LIGHTING, LIGHTING_PHP, LIGHTING_OBJ);
            getDatabaseData(id, UserSQLiteDatabase.TABLE_OCCUPANCY, OCCUPANCY_PHP, OCCUPANCY_OBJ);
            getDatabaseData(id, UserSQLiteDatabase.TABLE_TEMPERATURE, TEMPERATURE_PHP, TEMPERATURE_OBJ);
            getDatabaseData(id, UserSQLiteDatabase.TABLE_PLUGLOAD, PLUGLOAD_PHP, PLUGLOAD_OBJ);
        }*/
        int id = 1;
            getDatabaseData(id, UserSQLiteDatabase.TABLE_LIGHTING, LIGHTING_PHP, LIGHTING_OBJ);
            getDatabaseData(id, UserSQLiteDatabase.TABLE_OCCUPANCY, OCCUPANCY_PHP, OCCUPANCY_OBJ);
            getDatabaseData(id, UserSQLiteDatabase.TABLE_TEMPERATURE, TEMPERATURE_PHP, TEMPERATURE_OBJ);
            getDatabaseData(id, UserSQLiteDatabase.TABLE_PLUGLOAD, PLUGLOAD_PHP, PLUGLOAD_OBJ);

        try {
            DataAccessOwm dataAccessOwm = new DataAccessOwm(mcontext);
            //dataAccessOwm.saveWeatherData(); TEST
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(LOG_TAG, "Finishing network synchronization");

        if(sc.getNewest_timestamp() != null)
        {
            Log.i(LOG_TAG, "Calculate Data");
            sc.calculateData();
        }

        data_access.close();



        //TODO: If getNewest_timestamp is not null, then
        //do stat calculations

        //StatisticalCalculation.calculateData(mcontext);
    }

    private void getDatabaseData(int region_id, String table_name, String php_file_name, String json_obj_name)
    {
        String last_time_stamp = data_access.getLastTimeStamp(region_id, table_name);

        List<NameValuePair> id_and_timestamp = new ArrayList<>(2);

        id_and_timestamp.add(new BasicNameValuePair(ZONE_COLUMN_ID, (region_id + "").trim()));
        id_and_timestamp.add(new BasicNameValuePair(LAST_TIME_STAMP, (last_time_stamp).trim()));

        try {
            String res = new ExternalDatabaseController((ArrayList<NameValuePair>) id_and_timestamp, php_file_name).send();
            Log.i(LOG_TAG, "Response from Database for table "+table_name+": "+res);
            Log.i(LOG_TAG, "Last Time-Stamp: "+last_time_stamp);

            JSONObject obj = new JSONObject(res);
            JSONArray arr = obj.getJSONArray(json_obj_name);

            saveDataOnInternalDB(region_id, arr, table_name);

        } catch (InterruptedException | JSONException e) {
            //e.printStackTrace();
        }
    }

    private void saveDataOnInternalDB(int region_id, JSONArray arr, String table_name) throws JSONException {

        switch(table_name)
        {
            case UserSQLiteDatabase.TABLE_OCCUPANCY:
                for (int i = 0; i < arr.length(); i++) {
                    int occup = arr.getJSONObject(i).getInt("occupancy");
                    String time_stamp = arr.getJSONObject(i).getString(TIME_STAMP);
                    if (!time_stamp.equalsIgnoreCase("null"))
                        data_access.createOccupancy(region_id, time_stamp, occup);

                    if(i == 0)
                    {
                        Log.i(LOG_TAG, "checkTimeStamp");
                        checkTimeStamp(time_stamp);
                    }
                }

                break;
            case UserSQLiteDatabase.TABLE_LIGHTING:
                for (int i = 0; i < arr.length(); i++) {
                    String lighting = arr.getJSONObject(i).getString("status");
                    String time_stamp = arr.getJSONObject(i).getString(TIME_STAMP);
                    System.out.println("Lighting: " + lighting + ", Time Stamp: " + time_stamp
                                        + ", Energy Usage: " +arr.getJSONObject(i).getInt(ENERGY_USAGE));

                    if (!time_stamp.equalsIgnoreCase("null"))
                        data_access.createLighting(region_id, time_stamp, lighting,
                                            arr.getJSONObject(i).getInt(ENERGY_USAGE));
                    if(i == 0)
                    {
                        Log.i(LOG_TAG, "checkTimeStamp");
                        checkTimeStamp(time_stamp);
                    }
                }

                break;
            case UserSQLiteDatabase.TABLE_PLUGLOAD:
                for (int i = 0; i < arr.length(); i++) {
                    String plugLoad = arr.getJSONObject(i).getString("status");
                    String time_stamp = arr.getJSONObject(i).getString(TIME_STAMP);

                    // String app_name, String app_type, int energy_usage_kwh
                    if (!time_stamp.equalsIgnoreCase("null"))
                        data_access.createPlugLoad(region_id, time_stamp, plugLoad,
                                arr.getJSONObject(i).getString(PLUG_APPLIANCE_NAME),
                                arr.getJSONObject(i).getString(PLUG_APPLIANCE_TYPE),
                                arr.getJSONObject(i).getInt(ENERGY_USAGE));
                    if(i == 0)
                    {
                        Log.i(LOG_TAG, "checkTimeStamp");
                        checkTimeStamp(time_stamp);
                    }
                }

                break;
            case UserSQLiteDatabase.TABLE_TEMPERATURE:
                for (int i = 0; i < arr.length(); i++) {
                    int temp = arr.getJSONObject(i).getInt("temperature");
                    String time_stamp = arr.getJSONObject(i).getString(TIME_STAMP);
                    if (!time_stamp.equalsIgnoreCase("null"))
                        data_access.createTemperature(region_id, time_stamp, temp);
                    if(i == 0)
                    {
                        Log.i(LOG_TAG, "checkTimeStamp");
                        checkTimeStamp(time_stamp);
                    }
                }
                break;
        }
    }

    private void checkTimeStamp(String mTimeStamp)
    {
        try
        {
            if(sc.getNewest_timestamp() != null)
            {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date result =  df.parse(mTimeStamp);
                Date result2 =  df.parse(sc.getNewest_timestamp());

                //If TimeStamp < NewestTimeStamp
                if(result.compareTo(result2)<0){
                    System.out.println("result is before result2");
                    sc.setNewest_timestamp(mTimeStamp);
                }
            }
            else
                sc.setNewest_timestamp(mTimeStamp);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
