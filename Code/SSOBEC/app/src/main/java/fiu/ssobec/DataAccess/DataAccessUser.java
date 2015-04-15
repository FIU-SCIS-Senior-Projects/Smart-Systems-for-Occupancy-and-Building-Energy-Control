package fiu.ssobec.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import fiu.ssobec.AdaptersUtil.PlugLoadListParent;
import fiu.ssobec.Calculations.StatisticalCalculation;
import fiu.ssobec.Model.Lighting;
import fiu.ssobec.Model.Occupancy;
import fiu.ssobec.Model.OutsideWeather;
import fiu.ssobec.Model.PlugLoad;
import fiu.ssobec.Model.Temperature;
import fiu.ssobec.Model.User;
import fiu.ssobec.Model.Zones;
import fiu.ssobec.SQLite.UserSQLiteDatabase;

/**
 * Created by Dalaidis on 2/10/2015.
 *
 *  This class is useful to maintain our User database
 *  and support adding new users and updating the
 *  information of users.
 */
public class DataAccessUser implements DataAccessInterface {

    //Database fields
    private static SQLiteDatabase db;
    private UserSQLiteDatabase dbHelp;
    public static final String LOG_TAG = "DataAccessUser";


    public DataAccessUser(Context context)
    {
        dbHelp = new UserSQLiteDatabase(context);
    }

    public void open() throws SQLException{
        db = dbHelp.getWritableDatabase();
    }

    public void close() {
        dbHelp.close();
    }

    /****************************** USER ************************************/

    public static void createUser(String name, int id, String email)
    {
        int loggedIn = 1;
        ContentValues vals = new ContentValues();
        vals.put(UserSQLiteDatabase.COLUMN_NAME, name);
        vals.put(UserSQLiteDatabase.COLUMN_ID, id);
        vals.put(UserSQLiteDatabase.COLUMN_EMAIL, email);
        vals.put(UserSQLiteDatabase.COLUMN_LOGGEDIN, loggedIn);

        db.insert(UserSQLiteDatabase.TABLE_USER, null, vals);
    }

    public User getUser (int loggedIn){

        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_USER,
                USER_COLS,
                UserSQLiteDatabase.COLUMN_LOGGEDIN+" = "+ loggedIn+"",
                null, null, null, null);

        if (cursor.moveToFirst()) {
            User nUser = getUserFromCursor(cursor);

            cursor.close();
            return nUser;
        }
        else
        {
            return null;
        }
    }

    public User userExist (int userid){

        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_USER,
                USER_COLS,
                UserSQLiteDatabase.COLUMN_ID+" = "+ userid+"",
                null, null, null, null);

        if (cursor.moveToFirst()) {
            User nUser = getUserFromCursor(cursor);

            cursor.close();
            return nUser;
        }
        else
        {
            return null;
        }
    }


    //Table column updated user login
    public static void userLogin (int UserId){
        ContentValues args = new ContentValues();
        args.put(UserSQLiteDatabase.COLUMN_LOGGEDIN, 1);
        db.update(UserSQLiteDatabase.TABLE_USER, args, UserSQLiteDatabase.COLUMN_ID + " = " + UserId,
                null);
        System.out.println("Table column updated, user login");
    }

    // Table column updated user logout
    public static void userLogout (int UserId){
        ContentValues args = new ContentValues();
        args.put(UserSQLiteDatabase.COLUMN_LOGGEDIN, 0);
        db.update(UserSQLiteDatabase.TABLE_USER, args, UserSQLiteDatabase.COLUMN_ID + " = " + UserId,
                null);
        System.out.println("Table column updated, user logout");
        db.delete(UserSQLiteDatabase.TABLE_ZONES, null, null);
    }

    public static User getUserFromCursor(Cursor cursor) {
        User user = new User(cursor.getString(0),  //Name
                cursor.getInt(1),     //ID
                cursor.getString(2),  //Email
                cursor.getInt(3));    //LoggedIn
        return user;
    }

    /****************************** ZONES ************************************/

    public static Zones createZones(String zone_name,  int id)
    {
        System.out.println("create zone: Creating new zone on my database!");
        ContentValues vals = new ContentValues();
        vals.put(UserSQLiteDatabase.ZONES_COLUMN_ID, id);
        vals.put(UserSQLiteDatabase.ZONES_COLUMN_NAME, zone_name);

        System.out.println("Zone Name in vals: "+vals.getAsString(zone_name));

        db.insert(UserSQLiteDatabase.TABLE_ZONES,null ,vals);
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_ZONES,
                ZONE_COLS,
                UserSQLiteDatabase.ZONES_COLUMN_ID+" = "+id,
                null, null, null, null);

        cursor.moveToFirst();
        Zones zones = getZoneFromCursor(cursor);

        cursor.close();
        return zones;
    }

    //Get me a zone that has the zone_id
    public Zones getZone (int zone_id){

        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_ZONES,
                ZONE_COLS,
                UserSQLiteDatabase.ZONES_COLUMN_ID + " = " + zone_id,
                null, null, null, null);

        if (cursor.moveToFirst()) {
            Zones zone = getZoneFromCursor(cursor);
            cursor.close();
            return zone;
        }
        else
        {
            return null;
        }
    }

    public ArrayList<Integer> getAllZoneID() {
        ArrayList<Integer> zones_id = new ArrayList<>();

        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_ZONES,
                ZONE_COLS, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Zones zone = getZoneFromCursor(cursor);
            zones_id.add(zone.getZone_id());
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return zones_id;
    }

    public ArrayList<String> getAllZoneNames() {
        ArrayList<String> zones_names = new ArrayList<>();

        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_ZONES,
                ZONE_COLS, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Zones zone = getZoneFromCursor(cursor);
            zones_names.add(zone.getZone_name());
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return zones_names;
    }

    private static Zones getZoneFromCursor(Cursor cursor) {
        return new Zones(cursor.getInt(0),       //ID
                cursor.getString(1));
    }

    /****************************** TEMPERATURE ************************************/

    public static void createTemperature(int zone_id,  String date_time, int temperature)
    {
        ContentValues vals = new ContentValues();
        vals.put(UserSQLiteDatabase.TEMP_COLUMN_ID, zone_id);
        vals.put(UserSQLiteDatabase.TEMP_COLUMN_DATETIME, date_time);
        vals.put(UserSQLiteDatabase.TEMP_COLUMN_TEMPERATURE, temperature);

        db.insert(UserSQLiteDatabase.TABLE_TEMPERATURE,null ,vals);
    }

    public ArrayList<String> getLatestTemperature(int zone_id)
    {
        ArrayList<String> temp_info = new ArrayList<>();
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_TEMPERATURE,
                TEMP_COLS,
                UserSQLiteDatabase.TEMP_COLUMN_ID + " = " + zone_id,
                null, null, null, null);

        if (cursor.moveToLast())
        {
            Temperature temperature= getTemperatureFromCursor(cursor);
            temp_info.add(temperature.getDatetime());
            temp_info.add(temperature.getTemperature()+"");

            return temp_info;
        }
        else
            return null;
    }

    private static Temperature getTemperatureFromCursor(Cursor cursor) {
        Temperature temp = new Temperature( cursor.getInt(0),    //zone_id
                cursor.getString(1),       //datetime
                cursor.getInt(2));      //temperature
        return temp;
    }

    //Get temperature of one day.
    public ArrayList<Double> getAllTemperatureOnDateInterval(int zone_id, String upperbound_date, String lowerbound_date)
    {
        ArrayList<Double> myList = new ArrayList<>();

        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_TEMPERATURE,
                TEMP_COLS,
                UserSQLiteDatabase.TEMP_COLUMN_ID + " = " + zone_id
                        +" AND "+UserSQLiteDatabase.TEMP_COLUMN_DATETIME+" >= Datetime('"+upperbound_date+"')"
                        +" AND "+UserSQLiteDatabase.TEMP_COLUMN_DATETIME+" < Datetime('"+lowerbound_date+"')",
                null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Temperature temp = getTemperatureFromCursor(cursor);
            myList.add( (double) temp.getTemperature());
            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();
        return myList;
    }


    /****************************** PLUGLOAD ************************************/

    public static void createPlugLoad(int zone_id,  String date_time, String state, String app_name,
                                      String app_type, double energy_usage_kwh)
    {
        System.out.println("create my plugLoad data!");
        ContentValues vals = new ContentValues();
        vals.put(UserSQLiteDatabase.PLUG_COLUMN_ID, zone_id);
        vals.put(UserSQLiteDatabase.PLUG_COLUMN_DATETIME, date_time);
        vals.put(UserSQLiteDatabase.PLUG_COLUMN_STATE, state);
        vals.put(UserSQLiteDatabase.PLUG_COLUMN_APPENERGY, energy_usage_kwh);
        vals.put(UserSQLiteDatabase.PLUG_COLUMN_APPNAME, app_name);
        vals.put(UserSQLiteDatabase.PLUG_COLUMN_APPTYPE, app_type);

        db.insert(UserSQLiteDatabase.TABLE_PLUGLOAD, null, vals);
    }

    public ArrayList<String> getLatestPlugLoad(int zone_id)
    {
        ArrayList<String> plugLoad_info = new ArrayList<>();
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_PLUGLOAD,
                PLUG_COLS,
                UserSQLiteDatabase.PLUG_COLUMN_ID + " = " + zone_id,
                null, null, null, null);

        if (cursor.moveToLast())
        {
            PlugLoad plugLoad = getPlugLoadFromCursor(cursor);
            plugLoad_info.add(plugLoad.getDatetime());
            plugLoad_info.add(plugLoad.getStatus()+"");

            return plugLoad_info;
        }
        else
            return null;
    }

    private static PlugLoad getPlugLoadFromCursor(Cursor cursor) {
        return new PlugLoad(    cursor.getInt(0),          //zone_id
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3),
                                cursor.getString(4),
                                cursor.getDouble(5));
    }

    //Count...
    public int getAllDateTimesACStateOnBefore(int zone_id, String date)
    {
        //Get only information for the AC
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_PLUGLOAD,
                PLUG_COLS,
                UserSQLiteDatabase.PLUG_COLUMN_ID + " = " + zone_id+
                        " AND "+UserSQLiteDatabase.PLUG_COLUMN_STATE+ " = 'ON'"
                        +" AND "+UserSQLiteDatabase.PLUG_COLUMN_APPTYPE+ " = 'AC'"
                        +" AND "+UserSQLiteDatabase.TEMP_COLUMN_DATETIME+" <= Datetime('"+date+"')",
                null, null, null, null);

        int cnt = cursor.getCount();

        // make sure to close the cursor
        cursor.close();
        return cnt;
    }

    public double getAllPlugLoadEnergyBefore(int zone_id, String upperbound_date, String lowerbound_date)
    {
        //Get only information for the AC
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_PLUGLOAD,
                PLUG_COLS,
                UserSQLiteDatabase.PLUG_COLUMN_ID + " = " + zone_id
                +" AND "+UserSQLiteDatabase.PLUG_COLUMN_STATE+" = 'ON'"
                +" AND "+UserSQLiteDatabase.PLUG_COLUMN_DATETIME+" >= Datetime('"+upperbound_date+"')"
                +" AND "+UserSQLiteDatabase.PLUG_COLUMN_DATETIME+" < Datetime('"+lowerbound_date+"')",
                null, null, null, null);

        cursor.moveToFirst();
        double res=0.0;
        while (!cursor.isAfterLast()) {
            PlugLoad plug = getPlugLoadFromCursor(cursor);

            res = res + plug.getEnergy_usage_kwh();

            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();
        return res;
    }


    /*This method will return a HashMap with the key (name of appliance)
    and the value (the energy usage of the appliance).
    * */
    public HashMap<String, Double> getAllApplianceInformation(int region_id)
    {
        HashMap<String, Double> hmap = new HashMap<>();
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_PLUGLOAD,
                PLUG_COLS,
                UserSQLiteDatabase.PLUG_COLUMN_ID + " = " + region_id,
                null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            PlugLoad plug = getPlugLoadFromCursor(cursor);

            if(!hmap.containsKey(plug.getApp_name()))
            {
                System.out.println("PlugLoad: "+plug.getApp_name()+" Energy_kwh: "+plug.getEnergy_usage_kwh());
                hmap.put(plug.getApp_name(), plug.getEnergy_usage_kwh());
            }
            cursor.moveToNext();
        }

        cursor.close();

        return hmap;
    }


    /****************************** OCCUPANCY ************************************/

    public static void createOccupancy(int zone_id,  String date_time, int occupancy)
    {
        System.out.println("create my occupancy data!");
        ContentValues vals = new ContentValues();
        vals.put(UserSQLiteDatabase.OCC_COLUMN_ID, zone_id);
        vals.put(UserSQLiteDatabase.OCC_COLUMN_DATETIME, date_time);
        vals.put(UserSQLiteDatabase.OCC_COLUMN_OCCUPANCY, occupancy);

        db.insert(UserSQLiteDatabase.TABLE_OCCUPANCY, null, vals);
    }

    public String getLastTimeStamp()
    {
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_OCCUPANCY,
                OCC_COLS,
                null,
                null, null, null, null);

        if (cursor.moveToLast())
        {
            Occupancy occupancy = getOccupancyFromCursor(cursor);
            return occupancy.getDate_time();
        }
        else
            return null;
    }

    private static Occupancy getOccupancyFromCursor(Cursor cursor) {
        return new Occupancy( cursor.getString(1),    //Datetime
                cursor.getInt(0),       //Zone_ID
                cursor.getInt(2));
    }

    //Get the occupancy imformation, when the room is empty
    public ArrayList<String> getAllTimesWhenIsRoomEmpty(int zone_id, String upperbound_date, String lowerbound_date)
    {
        ArrayList<String> myList = new ArrayList<>();

        //Get only information for the AC
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_OCCUPANCY,
                OCC_COLS,
                UserSQLiteDatabase.OCC_COLUMN_ID + " = " + zone_id+" AND "+UserSQLiteDatabase.OCC_COLUMN_OCCUPANCY+" = 0"
                        +" AND "+UserSQLiteDatabase.OCC_COLUMN_DATETIME+" >= Datetime('"+upperbound_date+"')"
                        +" AND "+UserSQLiteDatabase.OCC_COLUMN_DATETIME+" < Datetime('"+lowerbound_date+"')",
                null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Occupancy occupancy = getOccupancyFromCursor(cursor);
            myList.add(occupancy.getDate_time());
            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();
        return myList;
    }

    public ArrayList<Double> getAllOccupancyBefore(int zone_id, String upperbound_date, String lowerbound_date)
    {
        ArrayList<Double> myList = new ArrayList<>();

        //Get only information for the AC
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_OCCUPANCY,
                OCC_COLS,
                UserSQLiteDatabase.OCC_COLUMN_ID + " = " + zone_id
                        +" AND "+UserSQLiteDatabase.OCC_COLUMN_DATETIME+" >= Datetime('"+upperbound_date+"')"
                        +" AND "+UserSQLiteDatabase.OCC_COLUMN_DATETIME+" < Datetime('"+lowerbound_date+"')",
                null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Occupancy occupancy = getOccupancyFromCursor(cursor);
            myList.add((double) occupancy.getOccupancy());
            Log.i(LOG_TAG, "getAllOccupancyBefore, Light: " + occupancy.toString());
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return myList;
    }


    /****************************** LIGHTING ************************************/

    public static void createLighting(int zone_id,  String date_time, String lighting, double energy_usage)
    {
        System.out.println("create my lighting data!");
        ContentValues vals = new ContentValues();
        vals.put(UserSQLiteDatabase.LIGHT_COLUMN_ID, zone_id);
        vals.put(UserSQLiteDatabase.LIGHT_COLUMN_DATETIME, date_time);
        vals.put(UserSQLiteDatabase.LIGHT_COLUMN_STATE, lighting);
        vals.put(UserSQLiteDatabase.LIGHT_COLUMN_ENERGY, energy_usage);

        db.insert(UserSQLiteDatabase.TABLE_LIGHTING, null, vals);
    }

    public ArrayList<String> getLatestLighting(int zone_id)
    {
        ArrayList<String> light_info = new ArrayList<>();
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_LIGHTING,
                LIGHT_COLS,
                UserSQLiteDatabase.LIGHT_COLUMN_ID + " = " + zone_id,
                null, null, null, null);

        if (cursor.moveToLast())
        {
            Lighting light = getLightingFromCursor(cursor);
            light_info.add(light.getDatetime());
            light_info.add(light.getLighting_state()+"");

            return light_info;
        }
        else
            return null;
    }

    private static Lighting getLightingFromCursor(Cursor cursor) {
        Lighting light = new Lighting( cursor.getInt(0),            //Datetime
                cursor.getString(1),       //Zone_ID
                cursor.getString(2),       //Lighting_state
                cursor.getDouble(3));         //Energy
        return light;
    }


    public double getLightingWaste(int zone_id, String datearr)
    {
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_LIGHTING,
                LIGHT_COLS,
                UserSQLiteDatabase.LIGHT_COLUMN_ID + " = " + zone_id
                        +" AND "+UserSQLiteDatabase.LIGHT_COLUMN_STATE+ " = 'ON'"
                        +" AND "+UserSQLiteDatabase.LIGHT_COLUMN_DATETIME+" IN "+datearr+" ",
                null, null, null, null);

        cursor.moveToFirst();
        int count = cursor.getCount();
        Lighting light;
        double res = 0.0;
        while (!cursor.isAfterLast()) {
            light = getLightingFromCursor(cursor);
            Log.i(LOG_TAG, "getLightingWaste, Light: " + light.toString());
            cursor.moveToNext();
            res = light.getEnergy_usage_kwh();
        }
        // make sure to close the cursor
        cursor.close();
        return count*res;
    }

    public int getTotalTimeLightWasON(int zone_id, String upperbound_date, String lowerbound_date)
    {
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_LIGHTING,
                LIGHT_COLS,
                UserSQLiteDatabase.LIGHT_COLUMN_ID + " = " + zone_id
                        +" AND "+UserSQLiteDatabase.LIGHT_COLUMN_STATE+ " = 'ON'"
                        +" AND "+UserSQLiteDatabase.LIGHT_COLUMN_DATETIME+" >= Datetime('"+upperbound_date+"')"
                        +" AND "+UserSQLiteDatabase.LIGHT_COLUMN_DATETIME+" < Datetime('"+lowerbound_date+"')",
                null, null, null, null);

        cursor.moveToFirst();
        int count = cursor.getCount();
        while (!cursor.isAfterLast()) {
            Lighting light = getLightingFromCursor(cursor);
            Log.i(LOG_TAG, "getTotalTimeLightWasON, Light: " + light.toString());
            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();
        return count;
    }

    public double getAllLightingEnergyUsageBefore(int zone_id, String upperbound_date, String lowerbound_date)
    {
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_LIGHTING,
                LIGHT_COLS,
                UserSQLiteDatabase.LIGHT_COLUMN_ID + " = " + zone_id
                        +" AND "+UserSQLiteDatabase.LIGHT_COLUMN_STATE+ " = 'ON'"
                        +" AND "+UserSQLiteDatabase.LIGHT_COLUMN_DATETIME+" >= Datetime('"+upperbound_date+"')"
                        +" AND "+UserSQLiteDatabase.LIGHT_COLUMN_DATETIME+" < Datetime('"+lowerbound_date+"')",
                null, null, null, null);

        double totalEnergyUsage=0.0;

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Lighting light = getLightingFromCursor(cursor);
            totalEnergyUsage = light.getEnergy_usage_kwh() + totalEnergyUsage;

            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

        return totalEnergyUsage;
    }


    /****************************** OUTSIDE_WEATHER ************************************/

    public static void createOutsideWeather(int cloudP, int temp)
    {
        ContentValues vals = new ContentValues();
        vals.put(UserSQLiteDatabase.OW_CLOUDPERCENTAGE, cloudP);
        vals.put(UserSQLiteDatabase.OW_TEMPERATURE, temp);

        db.insert(UserSQLiteDatabase.TABLE_OW, null, vals);
    }

    //String dataTime, int cloudPercentage, int maxTemperature, int minTemperature
    private static OutsideWeather getOWFromCursor(Cursor cursor) {
        return new OutsideWeather( cursor.getString(0),    //Datetime
                cursor.getInt(1),       //cloud
                cursor.getInt(2));
    }

    public String getCloudPercentage() {

        String cloud_p;
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_OW,
                OW_COLS,
                null,
                null, null, null, null);

        if (cursor.moveToLast())
        {
            OutsideWeather ow = getOWFromCursor(cursor);
            cloud_p = ow.getCloudPercentage()+"";
        }
        else
            cloud_p = "No Data";

        cursor.close();

        return cloud_p;
    }

    public String getOutsideTemperature() {

        int outside_temperature=0;
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_OW,
                OW_COLS,
                null,
                null, null, null, null);

        if (cursor.moveToLast())
        {
            OutsideWeather ow = getOWFromCursor(cursor);
            outside_temperature =  ow.getTemperature();
        }
        cursor.close();

        if(outside_temperature == 0)
        {
            cursor = db.query(UserSQLiteDatabase.TABLE_STAT,
                    new String[]{UserSQLiteDatabase.STAT_OUTSIDE_TEMP_AVG},
                    null,
                    null, null, null, null);

            if (cursor.moveToLast())
            {
                outside_temperature = cursor.getInt(0);
            }
        }

        return outside_temperature+"";
    }

    /****************************** MISC ************************************/

    public String getLastTimeStamp(String table_name) {
        String last_time_stamp = TIME_STAMP_FORMAT;

        Cursor cursor;
        switch(table_name)
        {
            case UserSQLiteDatabase.TABLE_OCCUPANCY:
                cursor = db.rawQuery("SELECT min(" + UserSQLiteDatabase.OCC_COLUMN_DATETIME
                        + ") FROM " + UserSQLiteDatabase.TABLE_OCCUPANCY
                        + " WHERE " + UserSQLiteDatabase.OCC_COLUMN_DATETIME
                        + " IN (SELECT max (" + UserSQLiteDatabase.OCC_COLUMN_DATETIME + ") "
                        + "FROM " + UserSQLiteDatabase.TABLE_OCCUPANCY
                        + " GROUP BY " + UserSQLiteDatabase.OCC_COLUMN_ID + " );", null);

                if (cursor.moveToFirst())
                    last_time_stamp = cursor.getString(0);
                cursor.close();
                break;
            case UserSQLiteDatabase.TABLE_LIGHTING:
                cursor = db.rawQuery("SELECT min(" + UserSQLiteDatabase.LIGHT_COLUMN_DATETIME
                        + ") FROM " + UserSQLiteDatabase.TABLE_LIGHTING
                        + " WHERE " + UserSQLiteDatabase.LIGHT_COLUMN_DATETIME
                        + " IN (SELECT max (" + UserSQLiteDatabase.LIGHT_COLUMN_DATETIME + ") "
                        + "FROM " + UserSQLiteDatabase.TABLE_LIGHTING
                        + " GROUP BY " + UserSQLiteDatabase.LIGHT_COLUMN_ID + " );", null);

                if (cursor.moveToFirst())
                    last_time_stamp = cursor.getString(0);
                cursor.close();
                break;
            case UserSQLiteDatabase.TABLE_PLUGLOAD:
                cursor = db.rawQuery("SELECT min(" + UserSQLiteDatabase.PLUG_COLUMN_DATETIME
                        + ") FROM " + UserSQLiteDatabase.TABLE_PLUGLOAD
                        + " WHERE " + UserSQLiteDatabase.PLUG_COLUMN_DATETIME
                        + " IN (SELECT max (" + UserSQLiteDatabase.PLUG_COLUMN_DATETIME + ") "
                        + "FROM " + UserSQLiteDatabase.TABLE_PLUGLOAD
                        + " GROUP BY " + UserSQLiteDatabase.PLUG_COLUMN_ID + " );", null);

                if (cursor.moveToFirst())
                    last_time_stamp = cursor.getString(0);
                cursor.close();
                break;
            case UserSQLiteDatabase.TABLE_TEMPERATURE:
                cursor = db.rawQuery("SELECT min(" + UserSQLiteDatabase.TEMP_COLUMN_DATETIME
                        + ") FROM " + UserSQLiteDatabase.TABLE_TEMPERATURE
                        + " WHERE " + UserSQLiteDatabase.TEMP_COLUMN_DATETIME
                        + " IN (SELECT max (" + UserSQLiteDatabase.TEMP_COLUMN_DATETIME + ") "
                        + "FROM " + UserSQLiteDatabase.TABLE_TEMPERATURE
                        + " GROUP BY " + UserSQLiteDatabase.TEMP_COLUMN_ID + " );", null);

                if (cursor.moveToFirst())
                    last_time_stamp = cursor.getString(0);

                cursor.close();
                break;
        }
        if(last_time_stamp == null)
            last_time_stamp = TIME_STAMP_FORMAT;

        return last_time_stamp;
    }

    public String getFirstTimeStamp() {

        String first_time_stamp = TIME_STAMP_FORMAT;

        Cursor cursor;
        cursor = db.rawQuery("SELECT min(" + UserSQLiteDatabase.OCC_COLUMN_DATETIME
                + ") FROM " + UserSQLiteDatabase.TABLE_OCCUPANCY, null);
        cursor.moveToFirst();
        //if the time stamp is less than the first_time_stamp, then first_time_stamp = time_stamp
        if(lessThan(cursor.getString(0), first_time_stamp))
            first_time_stamp = cursor.getString(0);
        cursor.close();

        cursor = db.rawQuery("SELECT min(" + UserSQLiteDatabase.LIGHT_COLUMN_DATETIME
                + ") FROM " + UserSQLiteDatabase.TABLE_LIGHTING, null);
        cursor.moveToFirst();
        if(lessThan(cursor.getString(0), first_time_stamp))
            first_time_stamp = cursor.getString(0);
        cursor.close();

        cursor = db.rawQuery("SELECT min(" + UserSQLiteDatabase.PLUG_COLUMN_DATETIME
                + ") FROM " + UserSQLiteDatabase.TABLE_PLUGLOAD, null);
        cursor.moveToFirst();
        if(lessThan(cursor.getString(0), first_time_stamp))
            first_time_stamp = cursor.getString(0);
        cursor.close();

        cursor = db.rawQuery("SELECT min(" + UserSQLiteDatabase.TEMP_COLUMN_DATETIME
                + ") FROM " + UserSQLiteDatabase.TABLE_TEMPERATURE, null);
        cursor.moveToFirst();
        if(lessThan(cursor.getString(0), first_time_stamp))
            first_time_stamp = cursor.getString(0);
        cursor.close();

        return first_time_stamp;
    }

    private boolean lessThan(String date1, String date2)
    {
        if(date1 == null || date2 == null)
            return false;

        if(date2 == TIME_STAMP_FORMAT)
            return true;

        DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime time1 = dateStringFormat.parseDateTime(date1);
        DateTime time2 = dateStringFormat.parseDateTime(date2);
        return time1.isBefore(time2);
    }

    public JSONArray getLastTimeStampByID(String table_name, JSONArray region_arr) {

        JSONArray region_timestamp_list = null;
        switch(table_name)
        {
            case UserSQLiteDatabase.TABLE_OCCUPANCY:
                region_timestamp_list = getLastTimeStampArray(UserSQLiteDatabase.OCC_COLUMN_DATETIME, UserSQLiteDatabase.OCC_COLUMN_ID,
                                        UserSQLiteDatabase.TABLE_OCCUPANCY, region_arr );
                break;
            case UserSQLiteDatabase.TABLE_LIGHTING:
                region_timestamp_list = getLastTimeStampArray(UserSQLiteDatabase.LIGHT_COLUMN_DATETIME, UserSQLiteDatabase.LIGHT_COLUMN_ID,
                        UserSQLiteDatabase.TABLE_LIGHTING, region_arr );
                break;
            case UserSQLiteDatabase.TABLE_PLUGLOAD:
                region_timestamp_list = getLastTimeStampArray(UserSQLiteDatabase.PLUG_COLUMN_DATETIME, UserSQLiteDatabase.PLUG_COLUMN_ID,
                        UserSQLiteDatabase.TABLE_PLUGLOAD, region_arr );
                break;
            case UserSQLiteDatabase.TABLE_TEMPERATURE:
                region_timestamp_list = getLastTimeStampArray(UserSQLiteDatabase.TEMP_COLUMN_DATETIME, UserSQLiteDatabase.TEMP_COLUMN_ID,
                        UserSQLiteDatabase.TABLE_TEMPERATURE, region_arr );
                break;
        }

        return region_timestamp_list;
    }

    public JSONArray getLastTimeStampArray(String date_time_column, String region_id_column, String table, JSONArray region_arr)
    {
        JSONArray list = new JSONArray();
        Cursor cursor;
        for(int i = 0; i < region_arr.length(); i++)
        {
            String sql = null;
            try {
                sql = "SELECT max("+date_time_column+") FROM "+table+" WHERE "+region_id_column+" = "+region_arr.get(i);
                Log.i(LOG_TAG, "SQL: "+sql);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst())
            {

                if(cursor.getString(0) != null)
                    list.put(cursor.getString(0));
                else
                    list.put(TIME_STAMP_FORMAT);
            }
            cursor.close();
        }

        Log.i(LOG_TAG, "getLastTimeStampArray: "+list);
        return list;
    }

    /****************************** TABLE_STAT ************************************/
    public static void createStat(int id, String date, double inside_temp_avg, double lighting_time_avg,
                                  double lighting_energyusage, double lighting_energywaste, double plugload_energyusage,
                                  double plugload_energywaste, double ac_energyusage, double occup_time_avg, double outside_temp_avg,
                                  double ac_setpoint)
    {
        System.out.println("Date: "+date+" InsideTemp: "+inside_temp_avg);
        System.out.println("Lighting Time: "+lighting_time_avg+" lighting_energyusage: "+lighting_energyusage);
        System.out.println("Lighting lighting_energywaste: "+lighting_energywaste+" plugload_energyusage: "+plugload_energyusage);
        System.out.println("plugload_energywaste: "+plugload_energywaste+" ac_energyusage: "+ac_energyusage);
        System.out.println("occup_time_avg: "+occup_time_avg+" outside_temp_avg: "+outside_temp_avg);

        ContentValues vals = new ContentValues();
        vals.put(UserSQLiteDatabase.STAT_ID, id);
        vals.put(UserSQLiteDatabase.STAT_DATE, date);
        vals.put(UserSQLiteDatabase.STAT_INSIDE_TEMP_AVG, inside_temp_avg);
        vals.put(UserSQLiteDatabase.STAT_LIGHTING_TIME_AVG, lighting_time_avg);
        vals.put(UserSQLiteDatabase.STAT_LIGHTING_ENERGYUSAGE, lighting_energyusage);
        vals.put(UserSQLiteDatabase.STAT_LIGHTING_ENERGYWASTE, lighting_energywaste);
        vals.put(UserSQLiteDatabase.STAT_PLUGLOAD_ENERGYWASTE, plugload_energywaste);
        vals.put(UserSQLiteDatabase.STAT_PLUGLOAD_ENERGYUSAGE, plugload_energyusage);
        vals.put(UserSQLiteDatabase.STAT_AC_ENERGYUSAGE, ac_energyusage);
        vals.put(UserSQLiteDatabase.STAT_OCCUP_TIME_AVG, occup_time_avg);
        vals.put(UserSQLiteDatabase.STAT_OUTSIDE_TEMP_AVG, outside_temp_avg);
        vals.put(UserSQLiteDatabase.STAT_AC_SETPOINT, ac_setpoint);

        db.insert(UserSQLiteDatabase.TABLE_STAT, null ,vals);
    }

    //TODO: After finish testing add option to only calculate a month
    // " AND date(" + UserSQLiteDatabase.STAT_DATE + ") < date('now', '-30 days')"
    public ArrayList<Double> getInsideTemperatureByZone(int zone_id, int ac_energy_upper, int ac_energy_lower)
    {
        ArrayList<Double> myList = new ArrayList<>();

        String [] itemperature = {UserSQLiteDatabase.STAT_AC_SETPOINT};
        Cursor cursor;

        if (ac_energy_lower == 0){
            cursor = db.query(UserSQLiteDatabase.TABLE_STAT,
                    itemperature,
                    UserSQLiteDatabase.STAT_ID + " = " + zone_id
                            +" AND "+UserSQLiteDatabase.STAT_AC_ENERGYUSAGE+" > "+ac_energy_upper
                            + " AND " + UserSQLiteDatabase.STAT_AC_SETPOINT + " != 0",
                    null, null, null, null);
        }else {
            cursor = db.query(UserSQLiteDatabase.TABLE_STAT,
                    itemperature,
                    UserSQLiteDatabase.STAT_ID + " = " + zone_id
                            + " AND " + UserSQLiteDatabase.STAT_AC_ENERGYUSAGE + " > " + ac_energy_upper
                            + " AND " + UserSQLiteDatabase.STAT_AC_ENERGYUSAGE + " <= " + ac_energy_lower
                            + " AND " + UserSQLiteDatabase.STAT_AC_SETPOINT + " != 0",
                    null, null, null, null);
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            myList.add(cursor.getDouble(0));
            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();
        return myList;
    }


    public ArrayList<Double> getOutsideTemperatureByZone(int zone_id, int ac_energy_upper, int ac_energy_lower)
    {
        ArrayList<Double> myList = new ArrayList<>();

        String [] otemperature = {UserSQLiteDatabase.STAT_OUTSIDE_TEMP_AVG};
        Cursor cursor;

        if (ac_energy_lower == 0){
            cursor = db.query(UserSQLiteDatabase.TABLE_STAT,
                    otemperature,
                    UserSQLiteDatabase.STAT_ID + " = " + zone_id
                            +" AND "+UserSQLiteDatabase.STAT_AC_ENERGYUSAGE+" > "+ac_energy_upper
                            + " AND " + UserSQLiteDatabase.STAT_OUTSIDE_TEMP_AVG + " != 0",
                    null, null, null, null);
        }else {
            cursor = db.query(UserSQLiteDatabase.TABLE_STAT,
                    otemperature,
                    UserSQLiteDatabase.STAT_ID + " = " + zone_id
                            + " AND " + UserSQLiteDatabase.STAT_AC_ENERGYUSAGE + " > " + ac_energy_upper
                            + " AND " + UserSQLiteDatabase.STAT_AC_ENERGYUSAGE + " <= " + ac_energy_lower
                            + " AND " + UserSQLiteDatabase.STAT_OUTSIDE_TEMP_AVG + " != 0",
                    null, null, null, null);
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            myList.add(cursor.getDouble(0));
            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();
        return myList;
    }

    public boolean dateExistsInStatTable(String date, int id)
    {
        Cursor cursor;
        cursor = db.query(UserSQLiteDatabase.TABLE_STAT,
                new String[]{UserSQLiteDatabase.STAT_DATE},
                UserSQLiteDatabase.STAT_DATE + " = " + date
                        + " AND " + UserSQLiteDatabase.STAT_ID + " = " + id,
                null, null, null, null);
        cursor.moveToFirst();

        if(cursor.getString(0) != null)
            return true;
        else
            return false;
    }

    public ArrayList<Integer> getLastFewHoursofOccupancy(int region_id)
    {
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_OCCUPANCY,
                OCC_COLS,
                UserSQLiteDatabase.OCC_COLUMN_ID + " = " + region_id,
                null, null, null, UserSQLiteDatabase.OCC_COLUMN_DATETIME+" DESC");

        cursor.moveToFirst();
        int counter = 7;
        ArrayList<Integer> occ_vals = new ArrayList<>();

        while (!cursor.isAfterLast()&&counter>=0) {
            Occupancy occupancy = getOccupancyFromCursor(cursor);
            System.out.println("Occupancy: " + occupancy.getDate_time() + " Datetime: " + occupancy.getOccupancy());
            occ_vals.add(occupancy.getOccupancy());

            cursor.moveToNext();
            counter--;
        }

        cursor.close();

        return occ_vals;
    }

    public ArrayList<String> getLastFewHoursofOccupancyDates(int region_id)
    {
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_OCCUPANCY,
                OCC_COLS,
                UserSQLiteDatabase.OCC_COLUMN_ID + " = " + region_id,
                null, null, null, UserSQLiteDatabase.OCC_COLUMN_DATETIME+" DESC");

        cursor.moveToFirst();
        int counter = 7;
        ArrayList<String> occ_dates = new ArrayList<>();

        while (!cursor.isAfterLast()&&counter>=0) {
            Occupancy occupancy = getOccupancyFromCursor(cursor);
            System.out.println("Occupancy: " + occupancy.getDate_time() + " Datetime: " + occupancy.getOccupancy());
            DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter dateStringFormat2 = DateTimeFormat.forPattern("hh:mm a");
            DateTime time = dateStringFormat.parseDateTime(occupancy.getDate_time());
            occ_dates.add(dateStringFormat2.print(time));

            cursor.moveToNext();
            counter--;
        }
        cursor.close();

        return occ_dates;
    }

    public ArrayList<PlugLoadListParent> getPlugLoadParentData(int regionID)
    {
        ArrayList<PlugLoadListParent> parents = new ArrayList<>();
        ArrayList<String> plugs = new ArrayList<>();

        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_PLUGLOAD,
                PLUG_COLS,
                UserSQLiteDatabase.PLUG_COLUMN_ID + " = " + regionID,
                null, null, null, UserSQLiteDatabase.PLUG_COLUMN_DATETIME+" DESC, " + UserSQLiteDatabase.PLUG_COLUMN_APPNAME  + " DESC");

        int counter = 10;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()&&counter>=0) {

            PlugLoad plugLoad = getPlugLoadFromCursor(cursor);
            PlugLoadListParent plugLoadListParent = new PlugLoadListParent();

            plugLoadListParent.setName(plugLoad.getApp_name());
            plugLoadListParent.setStatus(plugLoad.getStatus());
            plugLoadListParent.setEnergy_consumed((plugLoad.getEnergy_usage_kwh()*1000)+"");

            Log.i(LOG_TAG, plugLoadListParent.toString());

            if(plugs.contains(plugLoad.getApp_name()))
                break;
            else
            {
                plugs.add(plugLoad.getApp_name());
                parents.add(plugLoadListParent);
            }

            counter--;
            cursor.moveToNext();
        }

        return parents;
    }

    public double getLightingEnergyUsage(int regionID)
    {
        Cursor cursor;
        double energyusage = 0.0;
        cursor = db.query(UserSQLiteDatabase.TABLE_STAT,
                new String[]{UserSQLiteDatabase.STAT_LIGHTING_ENERGYUSAGE},
                UserSQLiteDatabase.STAT_ID + " = " + regionID+
                " AND "+ UserSQLiteDatabase.STAT_LIGHTING_ENERGYUSAGE + " != 0", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            energyusage = cursor.getDouble(0)+ energyusage;
            System.out.println("getLightingEnergyUsage: "+energyusage);
            cursor.moveToNext();
        }

        cursor.close();
        return energyusage;
    }

    public double getLightingEnergyWaste(int regionID)
    {
        Cursor cursor;
        double energywaste = 0.0;
        cursor = db.query(UserSQLiteDatabase.TABLE_STAT,
                new String[]{UserSQLiteDatabase.STAT_LIGHTING_ENERGYWASTE},
                UserSQLiteDatabase.STAT_ID + " = " + regionID+
                        " AND "+ UserSQLiteDatabase.STAT_LIGHTING_ENERGYWASTE + " != 0", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            energywaste = cursor.getDouble(0)+ energywaste;
            System.out.println("getLightingEnergyWaste: "+energywaste);
            cursor.moveToNext();
        }

        cursor.close();
        return energywaste;
    }

    public double getLightingAverageDay(int regionID)
    {
        ArrayList<Double> lighting_vals = new ArrayList<>();

        Cursor cursor;
        cursor = db.query(UserSQLiteDatabase.TABLE_STAT,
                new String[]{UserSQLiteDatabase.STAT_LIGHTING_TIME_AVG},
                UserSQLiteDatabase.STAT_ID + " = " + regionID+
                        " AND "+ UserSQLiteDatabase.STAT_LIGHTING_TIME_AVG + " != 0", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            lighting_vals.add(cursor.getDouble(0));
            cursor.moveToNext();
        }

        cursor.close();
        return StatisticalCalculation.avg(lighting_vals);
    }

    /*Get Latest Temperature information in a room
    * */
    public ArrayList<Integer> getLatestManyTemperature(int region_id)
    {
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_TEMPERATURE,
                TEMP_COLS,
                UserSQLiteDatabase.TEMP_COLUMN_ID + " = " + region_id,
                null, null, null, UserSQLiteDatabase.TEMP_COLUMN_DATETIME+" DESC");

        cursor.moveToFirst();
        int counter = 50;
        ArrayList<Integer> temp_vals = new ArrayList<>();

        while (!cursor.isAfterLast()&&counter>=0) {
            Temperature temp = getTemperatureFromCursor(cursor);
            System.out.println("Date: " + temp.getDatetime() + " Temperature: " + temp.getTemperature());
            temp_vals.add(temp.getTemperature());

            cursor.moveToNext();
            counter--;
        }

        cursor.close();
        Collections.reverse(temp_vals);

        return temp_vals;
    }

    /*Get total energy usage by plugLoad, air conditioning and lighting of a zone
    * */
    public HashMap<String, Double> getInfoForZonesDescription(int region_id)
    {
        double plug=0.0;
        double ac=0.0;
        double light=0.0;
        Cursor cursor;

        HashMap<String, Double> info = new HashMap<>();

        cursor = db.query(UserSQLiteDatabase.TABLE_PLUGLOAD,
                new String[]{UserSQLiteDatabase.PLUG_COLUMN_APPENERGY},
                UserSQLiteDatabase.PLUG_COLUMN_ID + " = " + region_id
                        + " AND " + UserSQLiteDatabase.PLUG_COLUMN_STATE + " = 'PLUGGED'",
                null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            plug = cursor.getDouble(0) + plug;
            cursor.moveToNext();
        }
        cursor.close();

        cursor = db.query(UserSQLiteDatabase.TABLE_STAT,
                new String[]{UserSQLiteDatabase.STAT_AC_ENERGYUSAGE},
                UserSQLiteDatabase.STAT_ID + " = " + region_id,
                null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ac = cursor.getDouble(0) + ac;
            cursor.moveToNext();
        }
        //TODO: get rid of the '30' when finished testing
        ac = ac/30;
        cursor.close();

        cursor = db.query(UserSQLiteDatabase.TABLE_STAT,
                new String[]{UserSQLiteDatabase.STAT_LIGHTING_ENERGYUSAGE},
                UserSQLiteDatabase.STAT_ID + " = " + region_id,
                null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            light = cursor.getDouble(0) + light;
            cursor.moveToNext();
        }
        cursor.close();

        info.put("plugload", plug);
        info.put("ac", ac);
        info.put("light", light);
        return info;
    }



    public HashMap<String, Integer> getRowCount()
    {
        HashMap<String, Integer> info = new HashMap<>();
        Cursor cursor;

        cursor = db.query(UserSQLiteDatabase.TABLE_PLUGLOAD,
                PLUG_COLS, null, null, null, null, null);

        cursor.moveToFirst();
        info.put("zone_plugload", cursor.getCount());
        cursor.close();

        cursor = db.query(UserSQLiteDatabase.TABLE_LIGHTING,
                LIGHT_COLS, null, null, null, null, null);

        cursor.moveToFirst();
        info.put("zone_lighting", cursor.getCount());
        cursor.close();

        cursor = db.query(UserSQLiteDatabase.TABLE_TEMPERATURE,
                TEMP_COLS, null, null, null, null, null);

        cursor.moveToFirst();
        info.put("zone_temperature", cursor.getCount());
        cursor.close();

        cursor = db.query(UserSQLiteDatabase.TABLE_OCCUPANCY,
                OCC_COLS, null, null, null, null, null);

        cursor.moveToFirst();
        info.put("zone_occupancy", cursor.getCount());
        cursor.close();

        return info;
    }

}
