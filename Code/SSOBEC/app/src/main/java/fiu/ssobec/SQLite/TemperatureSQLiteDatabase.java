package fiu.ssobec.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dalaidis on 2/11/2015.
 */
public class TemperatureSQLiteDatabase extends SQLiteOpenHelper {
    public static final String TABLE_TEMPERATURE_DESCRIPTION = "temperature_description";
    public static final String COLUMN_ID = "temperature_id";
    public static final String COLUMN_NAME = "temperature_name";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ssobec_internal.db";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_TEMPERATURE_DESCRIPTION + " ("
            + COLUMN_ID  + " int NOT NULL PRIMARY KEY, "
            + COLUMN_NAME + " varchar(255) NOT NULL "
            +
            ");";

    public TemperatureSQLiteDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
