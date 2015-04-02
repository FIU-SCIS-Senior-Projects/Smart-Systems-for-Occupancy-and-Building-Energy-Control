package fiu.ssobec.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

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

        db.insert(UserSQLiteDatabase.TABLE_USER,null ,vals);
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
        db.update(UserSQLiteDatabase.TABLE_USER, args, UserSQLiteDatabase.COLUMN_ID+" = "+UserId,
                null);
        System.out.println("Table column updated, user login");
    }

    // Table column updated user logout
    public static void userLogout (int UserId){
        ContentValues args = new ContentValues();
        args.put(UserSQLiteDatabase.COLUMN_LOGGEDIN, 0);
        db.update(UserSQLiteDatabase.TABLE_USER, args, UserSQLiteDatabase.COLUMN_ID+" = "+UserId,
                null);
        System.out.println("Table column updated, user logout");
        db.delete(UserSQLiteDatabase.TABLE_ZONES,null,null);
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
                UserSQLiteDatabase.ZONES_COLUMN_ID+" = "+ zone_id,
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

    public ArrayList<Double> getInsideTemperatureByZone(int zone_id, int ac_energy_lower, int ac_energy_upper)
    {
        ArrayList<Double> myList = new ArrayList<>();

        String [] itemperature = {UserSQLiteDatabase.STAT_INSIDE_TEMP_AVG};
        Cursor cursor;

        if (ac_energy_upper == 0){
            cursor = db.query(UserSQLiteDatabase.TABLE_STAT,
                itemperature,
                UserSQLiteDatabase.STAT_ID + " = " + zone_id+
                " AND date("+UserSQLiteDatabase.TEMP_COLUMN_DATETIME+") < date('now', '-30 days')"
                +" AND "+UserSQLiteDatabase.STAT_AC_ENERGYUSAGE+" >= "+ac_energy_lower ,
                null, null, null, null);
        }else {

            cursor = db.query(UserSQLiteDatabase.TABLE_STAT,
                    itemperature,
                    UserSQLiteDatabase.STAT_ID + " = " + zone_id +
                            " AND date(" + UserSQLiteDatabase.TEMP_COLUMN_DATETIME + ") < date('now', '-30 days')"
                            + " AND " + UserSQLiteDatabase.STAT_AC_ENERGYUSAGE + " <= " + ac_energy_upper
                            + " AND " + UserSQLiteDatabase.STAT_AC_ENERGYUSAGE + " >= " + ac_energy_lower,
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

    public ArrayList<Double> getOutsideTemperatureByZone(int zone_id, int ac_energy_lower, int ac_energy_upper)
    {
        ArrayList<Double> myList = new ArrayList<>();

        String [] otemperature = {UserSQLiteDatabase.STAT_OUTSIDE_TEMP_AVG};
        Cursor cursor;

        if (ac_energy_upper == 0){
            cursor = db.query(UserSQLiteDatabase.TABLE_STAT,
                    otemperature,
                    UserSQLiteDatabase.STAT_ID + " = " + zone_id+
                            " AND date("+UserSQLiteDatabase.TEMP_COLUMN_DATETIME+") < date('now', '-30 days')"
                            +" AND "+UserSQLiteDatabase.STAT_AC_ENERGYUSAGE+" >= "+ac_energy_lower ,
                    null, null, null, null);
        }else {
            cursor = db.query(UserSQLiteDatabase.TABLE_STAT,
                    otemperature,
                    UserSQLiteDatabase.STAT_ID + " = " + zone_id +
                            " AND date(" + UserSQLiteDatabase.TEMP_COLUMN_DATETIME + ") < date('now', '-30 days')"
                            + " AND " + UserSQLiteDatabase.STAT_AC_ENERGYUSAGE + " <= " + ac_energy_upper
                            + " AND " + UserSQLiteDatabase.STAT_AC_ENERGYUSAGE + " >= " + ac_energy_lower,
                    null, null, null, null);
        }

            cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            //myList.add(cursor.getDouble(cursor.getColumnIndex(UserSQLiteDatabase.STAT_OUTSIDE_TEMP_AVG)));
            myList.add(cursor.getDouble(0));
            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();
        return myList;
    }

    public ArrayList<Double> getACEnergyUsage()
    {
        ArrayList<Double> myList = new ArrayList<>();
        Cursor cursor;

        cursor = db.query(UserSQLiteDatabase.TABLE_STAT,
                new String[]{UserSQLiteDatabase.STAT_AC_ENERGYUSAGE}, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            //myList.add(cursor.getDouble(cursor.getColumnIndex(UserSQLiteDatabase.STAT_OUTSIDE_TEMP_AVG)));
            myList.add(cursor.getDouble(0));
            cursor.moveToNext();
        }

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

        db.insert(UserSQLiteDatabase.TABLE_PLUGLOAD,null ,vals);
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
                                cursor.getInt(5));
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

    public ArrayList<Double> getAllPlugLoadEnergyBefore(int zone_id, String date)
    {
        ArrayList<Double> myList = new ArrayList<>();

        //Get only information for the AC
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_PLUGLOAD,
                PLUG_COLS,
                UserSQLiteDatabase.PLUG_COLUMN_ID + " = " + zone_id
                +" AND "+UserSQLiteDatabase.PLUG_COLUMN_STATE+ " = 'ON'"
                +" AND "+UserSQLiteDatabase.PLUG_COLUMN_DATETIME+" <= Datetime('"+date+"')",
                null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            PlugLoad plug = getPlugLoadFromCursor(cursor);

            myList.add(plug.getEnergy_usage_kwh());
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return myList;
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
                System.out.println("PlugLoad: "+plug.getApp_name());
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

        db.insert(UserSQLiteDatabase.TABLE_OCCUPANCY,null ,vals);
    }

    public ArrayList<String> getLatestOccupancy(int zone_id)
    {
        ArrayList<String> occup_info = new ArrayList<>();
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_OCCUPANCY,
                OCC_COLS,
                UserSQLiteDatabase.OCC_COLUMN_ID + " = " + zone_id,
                null, null, null, null);

        if (cursor.moveToLast())
        {
            Occupancy occupancy = getOccupancyFromCursor(cursor);
            occup_info.add(occupancy.getDate_time());
            occup_info.add(occupancy.getOccupancy()+"");

            return occup_info;
        }
        else
            return null;
    }

    public String getFirstTimeStamp()
    {
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_OCCUPANCY,
                OCC_COLS,
                null,
                null, null, null, null);

        if (cursor.moveToFirst())
        {
            Occupancy occupancy = getOccupancyFromCursor(cursor);
            return occupancy.getDate_time();
        }
        else
            return null;
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

        db.insert(UserSQLiteDatabase.TABLE_LIGHTING,null ,vals);
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


    public int getLightingWaste(int zone_id, String datearr)
    {
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_LIGHTING,
                LIGHT_COLS,
                UserSQLiteDatabase.LIGHT_COLUMN_ID + " = " + zone_id
                        +" AND "+UserSQLiteDatabase.LIGHT_COLUMN_STATE+ " = 'ON'"
                        +" AND "+UserSQLiteDatabase.LIGHT_COLUMN_DATETIME+" IN "+datearr+" ",
                null, null, null, null);

        cursor.moveToFirst();
        int count = cursor.getCount();
        while (!cursor.isAfterLast()) {
            Lighting light = getLightingFromCursor(cursor);
            Log.i(LOG_TAG, "getLightingWaste, Light: " + light.toString());
            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();
        return count;
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

        db.insert(UserSQLiteDatabase.TABLE_OW, null ,vals);
    }

    //String dataTime, int cloudPercentage, int maxTemperature, int minTemperature
    private static OutsideWeather getOWFromCursor(Cursor cursor) {
        return new OutsideWeather( cursor.getString(0),    //Datetime
                                                cursor.getInt(1),       //cloud
                                                cursor.getInt(2));
    }

    public String getCloudPercentage() {

        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_OW,
                OW_COLS,
                null,
                null, null, null, null);

        if (cursor.moveToLast())
        {
            OutsideWeather ow = getOWFromCursor(cursor);
            return ow.getCloudPercentage()+"";
        }
        else
            return "No Data";
    }

    public String getOutsideTemperature() {

        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_OW,
                OW_COLS,
                null,
                null, null, null, null);

        if (cursor.moveToLast())
        {
            OutsideWeather ow = getOWFromCursor(cursor);
            return ow.getTemperature()+"";
        }
        else
            return "No Data";
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

    /****************************** TABLE_STAT ************************************/
    public static void createStat(int id, String date, double inside_temp_avg, double lighting_time_avg,
                                  int lighting_energyusage, int lighting_energywaste, int plugload_energyusage,
                                  int plugload_energywaste, int ac_energyusage, double occup_time_avg, double outside_temp_avg)
    {
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

        db.insert(UserSQLiteDatabase.TABLE_STAT, null ,vals);
    }

}
