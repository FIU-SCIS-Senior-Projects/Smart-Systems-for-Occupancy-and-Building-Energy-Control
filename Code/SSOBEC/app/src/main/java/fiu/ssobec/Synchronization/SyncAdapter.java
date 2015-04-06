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
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
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
    public static final String PLUGLOAD_PHP = "http://smartsystems-dev.cs.fiu.edu/plugloadpost.php";

    public static final String ZONE_COLUMN_ID = "region_id";
    public static final String LAST_TIME_STAMP = "last_time_stamp";
    public static final String TIME_STAMP = "time_stamp";

    public static final String PLUG_APPLIANCE_TYPE = "appliance_type";
    public static final String PLUG_STATUS = "status";
    public static final String PLUG_APPLIANCE_NAME = "appliance_name";
    public static final String ENERGY_USAGE = "energy_usage_kwh";
    public static final String OCCUPANCY_COLUMN = "occupancy";
    public static final String TEMPERATURE_COLUMN = "temperature";
    public static final String ZONEID_COLUMN = "zone_description_region_id";
    public static final String LIGHTSTATUS_COLUMN = "status";

    private Context mcontext;
    private DataAccessUser data_access;
    private StatisticalCalculation sc;
    String sqlRegionIdArr;
    private ArrayList<Integer> zones_with_new_data;

    /**
     * Creates an {@link android.content.AbstractThreadedSyncAdapter}.
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        data_access = new DataAccessUser(context);
        mcontext = context;
        zones_with_new_data = new ArrayList<>();

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

        List<Integer> region_id = data_access.getAllZoneID();
        sqlRegionIdArr = sqlArrayFormat(region_id);
        Log.i(LOG_TAG, "Region ID: "+sqlRegionIdArr);

        getDatabaseData(UserSQLiteDatabase.TABLE_OCCUPANCY, OCCUPANCY_PHP);
        getDatabaseData(UserSQLiteDatabase.TABLE_LIGHTING, LIGHTING_PHP);
        getDatabaseData(UserSQLiteDatabase.TABLE_TEMPERATURE, TEMPERATURE_PHP);
        getDatabaseData(UserSQLiteDatabase.TABLE_PLUGLOAD, PLUGLOAD_PHP);

        try {
            DataAccessOwm dataAccessOwm = new DataAccessOwm(mcontext);
            dataAccessOwm.saveWeatherData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        data_access.close();
        Log.i(LOG_TAG, "Finishing network synchronization");

        sc = new StatisticalCalculation(mcontext, zones_with_new_data);
        if(sc.getNewest_timestamp() != null)
        {
            Log.i(LOG_TAG, "Calculate Data");
            sc.calculateData();
        }

        sc.close();

    }

    private void getDatabaseData(String table_name, String php_file_name)
    {
        //"0000-00-00 00:00:00"
        String last_time_stamp = data_access.getLastTimeStamp(table_name);

        List<NameValuePair> id_and_timestamp = new ArrayList<>(2);

        id_and_timestamp.add(new BasicNameValuePair(ZONE_COLUMN_ID, (sqlRegionIdArr).trim()));
        id_and_timestamp.add(new BasicNameValuePair(LAST_TIME_STAMP, (last_time_stamp).trim()));
        Log.i(LOG_TAG, "Table name: "+table_name+", time_stamp: "+last_time_stamp);

        try {
            String res = new ExternalDatabaseController((ArrayList<NameValuePair>) id_and_timestamp, php_file_name).send();
            Log.i(LOG_TAG, "Response from Database for table: "+table_name+": "+res);

            new JSONObject(res);
            saveDataOnInternalDB(table_name, res);

        } catch (InterruptedException | JSONException e) {
            Log.e(LOG_TAG, "There was an error while getting the: "+table_name);
            e.printStackTrace();
        }
    }

    private void saveDataOnInternalDB(String table_name, String db_res){

        try {

            JSONObject obj =  new JSONObject(db_res);
            int j=0;
            JSONObject myobj;

            switch (table_name) {
                case UserSQLiteDatabase.TABLE_OCCUPANCY:
                    while (obj.has(j + "")) {
                        myobj = obj.getJSONObject(j + "");
                        data_access.createOccupancy(myobj.getInt(ZONEID_COLUMN), myobj.getString(TIME_STAMP),
                                myobj.getInt(OCCUPANCY_COLUMN));
                        if(!zones_with_new_data.contains(myobj.getInt(ZONEID_COLUMN)))
                            zones_with_new_data.add(myobj.getInt(ZONEID_COLUMN));

                        j++;
                    }

                    break;
                case UserSQLiteDatabase.TABLE_LIGHTING:
                    while (obj.has(j + "")) {
                        myobj = obj.getJSONObject(j + "");
                        data_access.createLighting(myobj.getInt(ZONEID_COLUMN), myobj.getString(TIME_STAMP),
                                myobj.getString(LIGHTSTATUS_COLUMN), myobj.getDouble(ENERGY_USAGE));
                        if(!zones_with_new_data.contains(myobj.getInt(ZONEID_COLUMN)))
                            zones_with_new_data.add(myobj.getInt(ZONEID_COLUMN));

                        j++;
                    }

                    break;
                case UserSQLiteDatabase.TABLE_PLUGLOAD:
                    while (obj.has(j + "")) {
                        myobj = obj.getJSONObject(j + "");
                        data_access.createPlugLoad(myobj.getInt(ZONEID_COLUMN), myobj.getString(TIME_STAMP),
                                myobj.getString(PLUG_STATUS),
                                myobj.getString(PLUG_APPLIANCE_NAME),
                                myobj.getString(PLUG_APPLIANCE_TYPE),
                                myobj.getDouble(ENERGY_USAGE));
                        if(!zones_with_new_data.contains(myobj.getInt(ZONEID_COLUMN)))
                            zones_with_new_data.add(myobj.getInt(ZONEID_COLUMN));

                        j++;
                    }

                    break;
                case UserSQLiteDatabase.TABLE_TEMPERATURE:
                    while (obj.has(j + "")) {
                        myobj = obj.getJSONObject(j + "");
                        data_access.createTemperature(myobj.getInt(ZONEID_COLUMN),
                                myobj.getString(TIME_STAMP),
                                myobj.getInt(TEMPERATURE_COLUMN));
                        if(!zones_with_new_data.contains(myobj.getInt(ZONEID_COLUMN)))
                            zones_with_new_data.add(myobj.getInt(ZONEID_COLUMN));

                        j++;
                    }

                    break;
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    //Format: (1, 2, 3)
    private String sqlArrayFormat(List<Integer> region_ids)
    {
        String res="(";
        int counter=0;

        Iterator itr = region_ids.iterator();
        while(itr.hasNext())
        {
            String str = itr.next().toString();
            if(counter == region_ids.size()-1)
                res = res+" "+str+")";
            else
                res = res+" "+str+",";

            counter++;
        }

        return res;
    }

}
