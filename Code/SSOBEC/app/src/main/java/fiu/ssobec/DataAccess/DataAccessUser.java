package fiu.ssobec.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import fiu.ssobec.Model.User;
import fiu.ssobec.Model.Zones;
import fiu.ssobec.SQLite.UserSQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public static User createUser(String name,  int id, String email, int loggedIn)
    {
        System.out.println("createUser: Creating new user on my database!!!");
        ContentValues vals = new ContentValues();
        vals.put(UserSQLiteDatabase.COLUMN_NAME, name);
        vals.put(UserSQLiteDatabase.COLUMN_ID, id);
        vals.put(UserSQLiteDatabase.COLUMN_EMAIL, email);
        vals.put(UserSQLiteDatabase.COLUMN_LOGGEDIN, loggedIn);


        long rowid = db.insert(UserSQLiteDatabase.TABLE_USER ,null ,vals);

        System.out.println("ROWID: "+rowid);
        System.out.println("Name: "+name+", logged in"+loggedIn);

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

    public boolean userExist (int userId){

        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_USER,
                USER_COLS,
                UserSQLiteDatabase.COLUMN_ID+" = "+ userId+"",
                null, null, null, null);

        if (cursor.moveToFirst()) {
            //User nUser = getUserFromCursor(cursor);
            cursor.close();
            return true;
        }
        else
        {
            return false;
        }
    }
    private static User getUserFromCursor(Cursor cursor) {
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

        db.insert(UserSQLiteDatabase.TABLE_ZONES_DESCRIPTION ,null ,vals);
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_ZONES_DESCRIPTION,
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

        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_ZONES_DESCRIPTION,
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


    private static Zones getZoneFromCursor(Cursor cursor) {
        Zones zones = new Zones(cursor.getInt(0),       //ID
                cursor.getString(1));                   //Name
        return zones;
    }

      public boolean doesTableExists()
    {
        if(dbHelp == null)
        {
            System.out.println("Table does not exist");
            return false;
        }
        else
        {
            return true;
        }
    }

    /****************************** TEMPERATURE ************************************/

}
