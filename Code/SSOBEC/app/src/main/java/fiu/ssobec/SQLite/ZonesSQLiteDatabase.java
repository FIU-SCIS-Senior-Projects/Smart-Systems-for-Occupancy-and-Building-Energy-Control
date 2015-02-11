package fiu.ssobec.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Maria on 2/9/2015.
 */
public class ZonesSQLiteDatabase extends SQLiteOpenHelper {


    public static final String TABLE_ZONES_DESCRIPTION = "zone_description";
    public static final String COLUMN_ID = "zone_id";
    public static final String COLUMN_NAME = "zone_name";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_ZONES_DESCRIPTION + " ("
            + COLUMN_ID  + " int NOT NULL PRIMARY KEY, "
            + COLUMN_NAME + " varchar(255) NOT NULL "
            +
            ");";

    public ZonesSQLiteDatabase(Context context) {
        super(context, SQLiteCommon.DATABASE_NAME, null, SQLiteCommon.DATABASE_VERSION);
    }

    public ZonesSQLiteDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
