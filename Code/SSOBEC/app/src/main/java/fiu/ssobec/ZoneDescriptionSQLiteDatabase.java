package fiu.ssobec;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Maria on 2/9/2015.
 */
public class ZoneDescriptionSQLiteDatabase extends SQLiteOpenHelper {

    public static final String TABLE_ZONES = "zones_data";
    public static final String COLUMN_ID = "zone_description_id";
    public static final String COLUMN_OCCUPANCY = "occupancy";
    public static final String COLUMN_TEMPERATURE = "temperature";
    public static final String COLUMN_PLUGLOAD = "plug_load";
    public static final String COLUMN_LIGHTING = "lighting";
    public static final String COLUMN_DATETIME = "date_time";

    private static final String DATABASE_NAME = "ssobec_internaldb";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_ZONES + "("
            + COLUMN_ID  + " int PRIMARY KEY, "
            + COLUMN_OCCUPANCY + " int NOT NULL, "
            + COLUMN_TEMPERATURE + " int NOT NULL, "
            + COLUMN_PLUGLOAD + " int NOT NULL, "
            + COLUMN_LIGHTING + " int NOT NULL, "
            + COLUMN_DATETIME + " datetime NOT NULL PRIMARY KEY,"
            +
            ");";

    public ZoneDescriptionSQLiteDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public ZoneDescriptionSQLiteDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
