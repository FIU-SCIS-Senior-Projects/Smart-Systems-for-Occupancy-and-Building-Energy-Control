package fiu.ssobec.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Dalaidis on 2/7/2015.
 */
public class UserSQLiteDatabase extends SQLiteOpenHelper {


    //Table User
    public static final String TABLE_USER = "user";
    public static final String COLUMN_ID = "user_id";
    public static final String COLUMN_NAME = "user_name";
    public static final String COLUMN_EMAIL = "user_email";
    public static final String COLUMN_LOGGEDIN = "user_loggedin";

    private static final String USER_TABLE_CREATE = "create table "
            + TABLE_USER + "("
            + COLUMN_LOGGEDIN + " int NOT NULL, "
            + COLUMN_ID + " int NOT NULL PRIMARY KEY, "
            + COLUMN_NAME + " varchar(50) NOT NULL, "
            + COLUMN_EMAIL + " varchar(100) NOT NULL "
            +"); ";

    //Table Zones
    public static final String TABLE_ZONES = "zone_description";
    public static final String ZONES_COLUMN_ID = "zone_id";
    public static final String ZONES_COLUMN_NAME = "zone_name";

    private static final String ZONE_TABLE_CREATE = "create table "
            + TABLE_ZONES + "("
            + ZONES_COLUMN_ID  + " int NOT NULL PRIMARY KEY, "
            + ZONES_COLUMN_NAME + " varchar(255) NOT NULL "
            +
            ");";

    //Table Temperature
    public static final String TABLE_TEMPERATURE = "zone_temperature";
    public static final String TEMP_COLUMN_ID = "zone_description_id";
    public static final String TEMP_COLUMN_DATETIME = "temperature_datetime";
    public static final String TEMP_COLUMN_TEMPERATURE = "temperature";

    private static final String TEMP_TABLE_CREATE = "create table "
            + TABLE_TEMPERATURE + " ("
            + TEMP_COLUMN_ID + " int NOT NULL, "
            + TEMP_COLUMN_DATETIME + " datetime NOT NULL, "
            + TEMP_COLUMN_TEMPERATURE + " int NOT NULL, "
            + "CONSTRAINT zone_temperature_pk PRIMARY KEY (" + TEMP_COLUMN_ID+" , "+TEMP_COLUMN_DATETIME+"), "
            + "FOREIGN KEY ("+TEMP_COLUMN_ID+") REFERENCES "+TABLE_ZONES+" ("+ZONES_COLUMN_ID+") "+
            ");";

    //Table Occupancy
    public static final String TABLE_OCCUPANCY = "zone_occupancy";
    public static final String OCC_COLUMN_ID = "zone_description_id";
    public static final String OCC_COLUMN_DATETIME = "occupancy_datetime";
    public static final String OCC_COLUMN_OCCUPANCY = "occupancy";

    private static final String OCC_TABLE_CREATE = "create table "
            + TABLE_OCCUPANCY + " ("
            + OCC_COLUMN_ID + " int NOT NULL, "
            + OCC_COLUMN_DATETIME + " datetime NOT NULL, "
            + OCC_COLUMN_OCCUPANCY + " int NOT NULL, "
            + "CONSTRAINT zone_occupancy_pk PRIMARY KEY (" + OCC_COLUMN_ID+" , "+OCC_COLUMN_DATETIME+"), "
            + "FOREIGN KEY ("+OCC_COLUMN_ID+") REFERENCES "+TABLE_ZONES+" ("+ZONES_COLUMN_ID+") "+
            ");";

    //Table Plug Load
    public static final String TABLE_PLUGLOAD = "zone_plugLoad";
    public static final String PLUG_COLUMN_ID = "zone_description_id";
    public static final String PLUG_COLUMN_DATETIME = "plugLoad_datetime";
    public static final String PLUG_COLUMN_STATE = "plugload_state";
    public static final String PLUG_COLUMN_APPNAME = "appliance_name";
    public static final String PLUG_COLUMN_APPTYPE = "appliance_type";
    public static final String PLUG_COLUMN_APPENERGY = "energy_usage_kwh";

    private static final String PLUG_TABLE_CREATE = "create table "
            + TABLE_PLUGLOAD + " ("
            + PLUG_COLUMN_ID + " int NOT NULL, "
            + PLUG_COLUMN_DATETIME + " datetime NOT NULL, "
            + PLUG_COLUMN_STATE + " varchar(3) NOT NULL, "
            + PLUG_COLUMN_APPNAME + " varchar(255) NULL, "
            + PLUG_COLUMN_APPTYPE + " varchar(255) NOT NULL, "
            + PLUG_COLUMN_APPENERGY + " int NOT NULL, "
            + "CONSTRAINT zone_plugLoad_pk PRIMARY KEY (" + PLUG_COLUMN_ID+" , "+PLUG_COLUMN_DATETIME+"), "
            + "FOREIGN KEY ("+PLUG_COLUMN_ID+") REFERENCES "+TABLE_ZONES+" ("+ZONES_COLUMN_ID+") "+
            ");";

    //Table Lighting
    public static final String TABLE_LIGHTING = "zone_lighting";
    public static final String LIGHT_COLUMN_ID = "zone_description_id";
    public static final String LIGHT_COLUMN_DATETIME = "lighting_datetime";
    public static final String LIGHT_COLUMN_STATE = "lighting_state";
    public static final String LIGHT_COLUMN_ENERGY = "energy_usage_kwh";

    private static final String LIGHT_TABLE_CREATE = "create table "
            + TABLE_LIGHTING + " ("
            + LIGHT_COLUMN_ID + " int NOT NULL, "
            + LIGHT_COLUMN_DATETIME + " datetime NOT NULL, "
            + LIGHT_COLUMN_STATE + " varchar(3) NOT NULL, "
            + LIGHT_COLUMN_ENERGY + " int NOT NULL, "
            + "CONSTRAINT zone_lighting_pk PRIMARY KEY (" + LIGHT_COLUMN_ID+" , "+LIGHT_COLUMN_DATETIME+"), "
            + "FOREIGN KEY ("+LIGHT_COLUMN_ID+") REFERENCES "+TABLE_ZONES+" ("+ZONES_COLUMN_ID+") "+
            ");";


    //Table Outside Weather
    public static final String TABLE_OW = "outside_weather";
    public static final String OW_DATETIME= "ow_time";
    public static final String OW_TEMPERATURE = "ow_max_temperature";
    public static final String OW_CLOUDPERCENTAGE = "ow_cloud_percentage";

    private static final String OW_TABLE_CREATE = "create table "
            + TABLE_OW + " ("
            + OW_DATETIME + " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP UNIQUE        , "
            + OW_CLOUDPERCENTAGE + " int NULL, "
            + OW_TEMPERATURE + " int NULL, "
            + "CONSTRAINT cloud_temp_pk PRIMARY KEY (" + OW_CLOUDPERCENTAGE+" , "+ OW_TEMPERATURE + ") "
            +
            ");";

    //Table day stat_study
    public static final String TABLE_STAT = "energy_statistics";
    public static final String STAT_ID = "zone_description_id";
    public static final String STAT_DATE = "date";
    public static final String STAT_INSIDE_TEMP_AVG = "inside_temperature_avg";
    public static final String STAT_LIGHTING_TIME_AVG = "lighting_time_avg_ON";
    public static final String STAT_LIGHTING_ENERGYUSAGE = "lighting_energy_usage";
    public static final String STAT_LIGHTING_ENERGYWASTE = "lihgting_energy_waste";
    public static final String STAT_PLUGLOAD_ENERGYWASTE = "plugload_energy_waste";
    public static final String STAT_PLUGLOAD_ENERGYUSAGE = "plugload_energy_usage";
    public static final String STAT_AC_ENERGYUSAGE = "ac_energy_usage";
    public static final String STAT_OCCUP_TIME_AVG = "occupancy_avg_time";
    public static final String STAT_OUTSIDE_TEMP_AVG = "outside_temperature_avg";

    private static final String STAT_TABLE_CREATE = "create table "
            + TABLE_STAT + " ("
            + STAT_ID + " int NOT NULL, "
            + STAT_DATE + " date NOT NULL, "
            + STAT_INSIDE_TEMP_AVG + " double NULL, "
            + STAT_LIGHTING_TIME_AVG + " double NULL, "
            + STAT_LIGHTING_ENERGYUSAGE + " int NULL, "
            + STAT_LIGHTING_ENERGYWASTE + " int NULL, "
            + STAT_PLUGLOAD_ENERGYWASTE + " int NULL, "
            + STAT_PLUGLOAD_ENERGYUSAGE + " int NULL, "
            + STAT_AC_ENERGYUSAGE + " int NULL, "
            + STAT_OCCUP_TIME_AVG + " double NULL, "
            + STAT_OUTSIDE_TEMP_AVG + " double NULL, "
            + "CONSTRAINT energy_statistics_pk PRIMARY KEY (" + STAT_ID+" , "+STAT_DATE+"), "
            + "FOREIGN KEY ("+STAT_ID+") REFERENCES "+TABLE_ZONES+" ("+ZONES_COLUMN_ID+") "+
            ");";

    public UserSQLiteDatabase(Context context) {

        super(context, SQLiteCommon.DATABASE_NAME, null, SQLiteCommon.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USER_TABLE_CREATE);
        db.execSQL(ZONE_TABLE_CREATE);
        db.execSQL(OCC_TABLE_CREATE);
        db.execSQL(TEMP_TABLE_CREATE);
        db.execSQL(LIGHT_TABLE_CREATE);
        db.execSQL(PLUG_TABLE_CREATE);
        db.execSQL(OW_TABLE_CREATE);
        db.execSQL(STAT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        System.out.println("onUpgrade: Upgrade my User Database!");
        Log.w(UserSQLiteDatabase.class.getName(),
                "Upgrading database from version"
                + oldVersion + " to " + newVersion
                );
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_ZONES);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_OCCUPANCY);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_TEMPERATURE);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_LIGHTING);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_PLUGLOAD);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_OW);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_STAT);

        onCreate(db);
    }
}
