package fiu.ssobec.SQLite;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Dalaidis on 2/9/2015.
 */
public class ZoneDataSQLiteDatabase extends SQLiteOpenHelper {

    public static final String TABLE_ZONES = "zones_data";
    public static final String COLUMN_ID = "zone_description_id";
    public static final String COLUMN_OCCUPANCY = "occupancy";
    public static final String COLUMN_TEMPERATURE = "temperature";
    public static final String COLUMN_PLUGLOAD = "plug_load";
    public static final String COLUMN_LIGHTING = "lighting";
    public static final String COLUMN_DATETIME = "date_time";

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_ZONES + "("
            + COLUMN_ID  + " int PRIMARY KEY, "
            + COLUMN_OCCUPANCY + " int NOT NULL, "
            + COLUMN_TEMPERATURE + " int NOT NULL, "
            + COLUMN_PLUGLOAD + " int NOT NULL, "
            + COLUMN_LIGHTING + " int NOT NULL, "
            + COLUMN_DATETIME + " datetime NOT NULL PRIMARY KEY"
            + " FOREIGN KEY ( " +COLUMN_ID +" ) REFERENCES "+ ZonesSQLiteDatabase.TABLE_ZONES_DESCRIPTION
                                                            +" ( "+ZonesSQLiteDatabase.COLUMN_ID+" )"
            +
            ");";

    public ZoneDataSQLiteDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public ZoneDataSQLiteDatabase(Context context) {
        super(context, SQLiteCommon.DATABASE_NAME, null, SQLiteCommon.DATABASE_VERSION);
    }

    public ZoneDataSQLiteDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ZoneDataSQLiteDatabase.class.getName(),
                "Upgrading database from version "+oldVersion+"to "
                +newVersion+".");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_ZONES);
        onCreate(db);
    }
}
