package fiu.ssobec.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import fiu.ssobec.SQLite.SQLiteCommon;

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
    public static final String TABLE_ZONES_DESCRIPTION = "zone_description";
    public static final String ZONES_COLUMN_ID = "zone_id";
    public static final String ZONES_COLUMN_NAME = "zone_name";

    private static final String ZONE_TABLE_CREATE = "create table "
            + TABLE_ZONES_DESCRIPTION + "("
            + ZONES_COLUMN_ID  + " int NOT NULL PRIMARY KEY, "
            + ZONES_COLUMN_NAME + " varchar(255) NOT NULL "
            +
            ");";

    //Table Temperature
    public static final String TABLE_TEMPERATURE = "zone_temperature";
    public static final String TEMP_COLUMN_ID = "temperature_id";
    public static final String TEMP_COLUMN_NAME = "temperature_name";

    private static final String TEMP_TABLE_CREATE = "create table "
            + TABLE_TEMPERATURE + " ("
            + TEMP_COLUMN_ID  + " int NOT NULL PRIMARY KEY, "
            + TEMP_COLUMN_NAME + " varchar(255) NOT NULL "
            +
            ");";

    public UserSQLiteDatabase(Context context) {

        super(context, SQLiteCommon.DATABASE_NAME, null, SQLiteCommon.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USER_TABLE_CREATE);
        db.execSQL(ZONE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        System.out.println("onUpgrade: Upgrade my User Database!");
        Log.w(UserSQLiteDatabase.class.getName(),
                "Upgrading database from version"
                + oldVersion + " to " + newVersion
                );
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_ZONES_DESCRIPTION);
        onCreate(db);
    }
}
