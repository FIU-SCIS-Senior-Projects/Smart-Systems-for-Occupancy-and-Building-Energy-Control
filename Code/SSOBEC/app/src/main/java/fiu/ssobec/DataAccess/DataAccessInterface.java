package fiu.ssobec.DataAccess;

import fiu.ssobec.SQLite.UserSQLiteDatabase;

/**
 * Created by Maria on 3/18/2015.
 */
public interface DataAccessInterface {

     static String[] USER_COLS = {   UserSQLiteDatabase.COLUMN_EMAIL,
            UserSQLiteDatabase.COLUMN_ID,
            UserSQLiteDatabase.COLUMN_NAME,
            UserSQLiteDatabase.COLUMN_LOGGEDIN,
            UserSQLiteDatabase.COLUMN_USERTYPE
     };

     static String[] ZONE_COLS = {   UserSQLiteDatabase.ZONES_COLUMN_ID,
            UserSQLiteDatabase.ZONES_COLUMN_NAME};

     static String[] TEMP_COLS = {   UserSQLiteDatabase.TEMP_COLUMN_ID,
            UserSQLiteDatabase.TEMP_COLUMN_DATETIME,
            UserSQLiteDatabase.TEMP_COLUMN_TEMPERATURE};

     static String[] OCC_COLS = {    UserSQLiteDatabase.OCC_COLUMN_ID,
            UserSQLiteDatabase.OCC_COLUMN_DATETIME,
            UserSQLiteDatabase.OCC_COLUMN_OCCUPANCY};

     static String[] LIGHT_COLS = {  UserSQLiteDatabase.LIGHT_COLUMN_ID,
            UserSQLiteDatabase.LIGHT_COLUMN_DATETIME,
            UserSQLiteDatabase.LIGHT_COLUMN_STATE,
            UserSQLiteDatabase.LIGHT_COLUMN_ENERGY};

     static String[] PLUG_COLS = {   UserSQLiteDatabase.PLUG_COLUMN_ID,
            UserSQLiteDatabase.PLUG_COLUMN_DATETIME,
            UserSQLiteDatabase.PLUG_COLUMN_STATE,
            UserSQLiteDatabase.PLUG_COLUMN_APPNAME,
            UserSQLiteDatabase.PLUG_COLUMN_APPTYPE,
            UserSQLiteDatabase.PLUG_COLUMN_APPENERGY};

     static String[] OW_COLS = {     UserSQLiteDatabase.OW_DATETIME,
            UserSQLiteDatabase.OW_CLOUDPERCENTAGE,
            UserSQLiteDatabase.OW_TEMPERATURE};

     static String TIME_STAMP_FORMAT = "0000-00-00 00:00:00";

}
