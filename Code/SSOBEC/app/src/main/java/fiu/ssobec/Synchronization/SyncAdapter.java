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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    public static final String PLUGLOAD_PHP = "http://smartsystems-dev.cs.fiu.edu/plugloadpost2.php";

    public static final String DB_NODATA = "No Data";
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

    //Name of the JSON Objects
    public static final String OCCUPANCY_OBJ = "occupancy_obj";
    public static final String TEMPERATURE_OBJ = "temperature_arr";
    public static final String PLUGLOAD_OBJ = "plugload_obj";
    public static final String LIGHTING_OBJ = "lighting_obj";

    private Context mcontext;
    private DataAccessUser data_access;
    private StatisticalCalculation sc;
    String sqlRegionIdArr;

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

        sqlRegionIdArr = sqlArrayFormat(region_id);
        Log.i(LOG_TAG, "Region ID: "+sqlRegionIdArr);

        getDatabaseData(1, UserSQLiteDatabase.TABLE_OCCUPANCY, OCCUPANCY_PHP, OCCUPANCY_OBJ);
        getDatabaseData(1, UserSQLiteDatabase.TABLE_LIGHTING, LIGHTING_PHP, LIGHTING_OBJ);
        getDatabaseData(1, UserSQLiteDatabase.TABLE_TEMPERATURE, TEMPERATURE_PHP, TEMPERATURE_OBJ);
        getDatabaseData(1, UserSQLiteDatabase.TABLE_PLUGLOAD, PLUGLOAD_PHP, PLUGLOAD_OBJ);

        try {
            DataAccessOwm dataAccessOwm = new DataAccessOwm(mcontext);
            dataAccessOwm.saveWeatherData(); //TEST
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(sc.getNewest_timestamp() != null)
        {
            Log.i(LOG_TAG, "Calculate Data");
            //sc.calculateData();
        }

        data_access.close();
        Log.i(LOG_TAG, "Finishing network synchronization");

    }

    private void getDatabaseData(int region_id, String table_name, String php_file_name, String json_obj_name)
    {
        //"0000-00-00 00:00:00"
        //String last_time_stamp = data_access.getLastTimeStamp(region_id, table_name);
        String last_time_stamp = "0000-00-00 00:00:00";

        List<NameValuePair> id_and_timestamp = new ArrayList<>(2);

        id_and_timestamp.add(new BasicNameValuePair(ZONE_COLUMN_ID, (sqlRegionIdArr).trim()));
        id_and_timestamp.add(new BasicNameValuePair(LAST_TIME_STAMP, (last_time_stamp).trim()));

        try {
            String res = new ExternalDatabaseController((ArrayList<NameValuePair>) id_and_timestamp, php_file_name).send();
            Log.i(LOG_TAG, "Response from Database for table "+table_name+": "+res);
            Log.i(LOG_TAG, "Last Time-Stamp: "+last_time_stamp);

            saveDataOnInternalDB(region_id, table_name, res);

        } catch (InterruptedException | JSONException e) {
            //e.printStackTrace();
        }
    }

    private void saveDataOnInternalDB(int region_id, String table_name, String db_res) throws JSONException {

        JSONObject obj;
        int j;
        JSONObject myobj;

        switch(table_name)
        {
            case UserSQLiteDatabase.TABLE_OCCUPANCY:
                obj = new JSONObject(db_res);
                j=0;
                while(obj.has(j+""))
                {
                    myobj = obj.getJSONObject(j+"");
                    System.out.println("zone_id: "+myobj.getInt(ZONEID_COLUMN));
                    System.out.println("occ: "+myobj.getInt(OCCUPANCY_COLUMN));
                    System.out.println("time: "+myobj.getString(TIME_STAMP));

                    data_access.createOccupancy(myobj.getInt(ZONEID_COLUMN),myobj.getString(TIME_STAMP),
                                                myobj.getInt(OCCUPANCY_COLUMN));
                    j++;
                }

                break;
            case UserSQLiteDatabase.TABLE_LIGHTING:
                obj = new JSONObject(db_res);
                j=0;

                while(obj.has(j+""))
                {
                    myobj = obj.getJSONObject(j+"");
                    System.out.println("zone_id: "+myobj.getInt(ZONEID_COLUMN));
                    System.out.println("light stat: "+myobj.getString(LIGHTSTATUS_COLUMN));
                    System.out.println("time: "+myobj.getString(TIME_STAMP));
                    System.out.println("light_energy: "+myobj.getInt(ENERGY_USAGE));

                    data_access.createLighting( myobj.getInt(ZONEID_COLUMN), myobj.getString(TIME_STAMP),
                                                myobj.getString(LIGHTSTATUS_COLUMN), myobj.getInt(ENERGY_USAGE));
                    j++;
                }

                break;
            case UserSQLiteDatabase.TABLE_PLUGLOAD:
                obj = new JSONObject(db_res);
                j=0;
                while(obj.has(j+""))
                {
                    myobj = obj.getJSONObject(j+"");
                    System.out.println("zone_id: "+myobj.getInt(ZONEID_COLUMN));
                    System.out.println("light stat: "+myobj.getString(PLUG_APPLIANCE_TYPE));
                    System.out.println("time: "+myobj.getString(TIME_STAMP));
                    System.out.println("light_energy: "+myobj.getInt(ENERGY_USAGE));

                    data_access.createPlugLoad(region_id, myobj.getString(TIME_STAMP), myobj.getString(PLUG_STATUS),
                            myobj.getString(PLUG_APPLIANCE_NAME),
                            myobj.getString(PLUG_APPLIANCE_TYPE),
                            myobj.getInt(ENERGY_USAGE));
                    j++;
                }

                break;
            case UserSQLiteDatabase.TABLE_TEMPERATURE:
                obj = new JSONObject(db_res);
                j=0;
                while(obj.has(j+""))
                {
                    myobj = obj.getJSONObject(j+"");
                    System.out.println("zone_id: "+myobj.getInt(ZONEID_COLUMN));
                    System.out.println("time: "+myobj.getString(TIME_STAMP));
                    System.out.println("temperature: "+myobj.getInt(TEMPERATURE_COLUMN));

                    data_access.createTemperature(myobj.getInt(ZONEID_COLUMN), myobj.getString(TIME_STAMP), myobj.getInt(TEMPERATURE_COLUMN));

                    j++;
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
                    sc.setNewest_timestamp(mTimeStamp);
                }
            }
            else
                sc.setNewest_timestamp(mTimeStamp);

        } catch (ParseException e) {
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
