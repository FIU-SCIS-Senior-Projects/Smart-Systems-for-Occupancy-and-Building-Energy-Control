package fiu.ssobec.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;

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
public class DataAccessUser {

    //Database fields
    private static SQLiteDatabase db;
    private UserSQLiteDatabase dbHelp;

    private static String[] USER_COLS = {   UserSQLiteDatabase.COLUMN_EMAIL,
                                            UserSQLiteDatabase.COLUMN_ID,
                                            UserSQLiteDatabase.COLUMN_NAME,
                                            UserSQLiteDatabase.COLUMN_LOGGEDIN};

    private static String[] ZONE_COLS = {   UserSQLiteDatabase.ZONES_COLUMN_ID,
                                            UserSQLiteDatabase.ZONES_COLUMN_NAME};

    private static String[] TEMP_COLS = {   UserSQLiteDatabase.TEMP_COLUMN_ID,
                                            UserSQLiteDatabase.TEMP_COLUMN_DATETIME,
                                            UserSQLiteDatabase.TEMP_COLUMN_TEMPERATURE};

    private static String[] OCC_COLS = {    UserSQLiteDatabase.OCC_COLUMN_ID,
                                            UserSQLiteDatabase.OCC_COLUMN_DATETIME,
                                            UserSQLiteDatabase.OCC_COLUMN_OCCUPANCY};

    private static String[] LIGHT_COLS = {  UserSQLiteDatabase.LIGHT_COLUMN_ID,
                                            UserSQLiteDatabase.LIGHT_COLUMN_DATETIME,
                                            UserSQLiteDatabase.LIGHT_COLUMN_STATE,
                                            UserSQLiteDatabase.LIGHT_COLUMN_ENERGY};

    private static String[] PLUG_COLS = {   UserSQLiteDatabase.PLUG_COLUMN_ID,
                                            UserSQLiteDatabase.PLUG_COLUMN_DATETIME,
                                            UserSQLiteDatabase.PLUG_COLUMN_STATE,
                                            UserSQLiteDatabase.PLUG_COLUMN_APPNAME,
                                            UserSQLiteDatabase.PLUG_COLUMN_APPTYPE,
                                            UserSQLiteDatabase.PLUG_COLUMN_APPENERGY};

    private static String[] OW_COLS = {     UserSQLiteDatabase.OW_DATETIME,
                                            UserSQLiteDatabase.OW_CLOUDPERCENTAGE,
                                            UserSQLiteDatabase.OW_TEMPERATURE};

    private static String TIME_STAMP_FORMAT = "0000-00-00 00:00:00";

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

    public static User createUser(String name,  int id, String email)
    {
        int loggedIn = 1;
        ContentValues vals = new ContentValues();
        vals.put(UserSQLiteDatabase.COLUMN_NAME, name);
        vals.put(UserSQLiteDatabase.COLUMN_ID, id);
        vals.put(UserSQLiteDatabase.COLUMN_EMAIL, email);
        vals.put(UserSQLiteDatabase.COLUMN_LOGGEDIN, loggedIn);

        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_USER,
                USER_COLS,
                                UserSQLiteDatabase.COLUMN_ID+" = "+id,
                                null, null, null, null);

        cursor.moveToFirst();
        User nUser = getUserFromCursor(cursor);

        cursor.close();
        return nUser;
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
        String strFilter = "_id=" + UserId;
        ContentValues args = new ContentValues();
        args.put(UserSQLiteDatabase.COLUMN_LOGGEDIN, 0);
        db.update(UserSQLiteDatabase.TABLE_USER, args, UserSQLiteDatabase.COLUMN_ID+" = "+UserId,
                null);
        System.out.println("Table column updated, user logout");
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
        System.out.println("create my temperature data!");
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

    /*  zone_id
        datetime
       temperature
    * */
    private static Temperature getTemperatureFromCursor(Cursor cursor) {
        Temperature temp = new Temperature( cursor.getInt(0),    //zone_id
                cursor.getString(1),       //datetime
                cursor.getInt(2));      //temperature
        return temp;
    }

    /****************************** PLUGLOAD ************************************/

    public static void createPlugLoad(int zone_id,  String date_time, String state, String app_name, String app_type, int energy_usage_kwh)
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

    /*  int zone_id,
       String datetime,
       String status,
       String app_type,
       String app_name,
       int energy_usage_kwh
    * */
    private static PlugLoad getPlugLoadFromCursor(Cursor cursor) {
        return new PlugLoad(    cursor.getInt(0),          //zone_id
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3),
                                cursor.getString(4),
                                cursor.getInt(5));
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

    private static Occupancy getOccupancyFromCursor(Cursor cursor) {
        Occupancy occp = new Occupancy( cursor.getString(1),    //Datetime
                                        cursor.getInt(0),       //Zone_ID
                                        cursor.getInt(2));      //Occupancy
        return occp;
    }

    /****************************** LIGHTING ************************************/

    public static void createLighting(int zone_id,  String date_time, String lighting, int energy_usage)
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

    //ID
    //STATE
    //Datetime
    private static Lighting getLightingFromCursor(Cursor cursor) {
        Lighting light = new Lighting( cursor.getInt(0),            //Datetime
                                        cursor.getString(1),       //Zone_ID
                                        cursor.getString(2),       //Lighting_state
                                        cursor.getInt(3));         //Energy
        return light;
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
        OutsideWeather ow = new OutsideWeather( cursor.getString(0),    //Datetime
                                                cursor.getInt(1),       //cloud
                                                cursor.getInt(2));      //temp
        return ow;
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

    public String getTemperature() {

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

    public String getLastTimeStamp(int zone_id, String table_name) {
        String last_time_stamp = TIME_STAMP_FORMAT;

        Cursor cursor;
        switch(table_name)
        {
            case UserSQLiteDatabase.TABLE_OCCUPANCY:
                cursor = db.query(UserSQLiteDatabase.TABLE_OCCUPANCY,
                    OCC_COLS, UserSQLiteDatabase.OCC_COLUMN_ID + " = " + zone_id, null, null, null, null);
                if (cursor.moveToLast())
                    last_time_stamp = getOccupancyFromCursor(cursor).getDate_time();
                cursor.close();
                break;
            case UserSQLiteDatabase.TABLE_LIGHTING:
                cursor = db.query(UserSQLiteDatabase.TABLE_LIGHTING,
                        LIGHT_COLS, UserSQLiteDatabase.LIGHT_COLUMN_ID + " = " + zone_id, null, null, null, null);
                if (cursor.moveToLast())
                    last_time_stamp = getLightingFromCursor(cursor).getDatetime();
                cursor.close();
                break;
            case UserSQLiteDatabase.TABLE_PLUGLOAD:
                cursor = db.query(UserSQLiteDatabase.TABLE_PLUGLOAD,
                        PLUG_COLS, UserSQLiteDatabase.PLUG_COLUMN_ID + " = " + zone_id, null, null, null, null);
                if (cursor.moveToLast())
                    last_time_stamp = getPlugLoadFromCursor(cursor).getDatetime();
                cursor.close();
                break;
            case UserSQLiteDatabase.TABLE_TEMPERATURE:
                cursor = db.query(UserSQLiteDatabase.TABLE_TEMPERATURE,
                        TEMP_COLS, UserSQLiteDatabase.TEMP_COLUMN_ID + " = " + zone_id, null, null, null, null);
                if (cursor.moveToLast())
                    last_time_stamp = getTemperatureFromCursor(cursor).getDatetime();
                cursor.close();
                break;
        }
        return last_time_stamp;
    }
    /*
    get Inside Temperature(int min, int max){
    SQL- get me the inside temperature such that (min<=ac_energy_usage<=max)
    cursor
    return ArrayList<double>


    }*/
}
