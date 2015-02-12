package fiu.ssobec.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Maria on 2/9/2015.
 */
public class ZonesSQLiteDatabase extends SQLiteOpenHelper {


    public static final String TABLE_ZONES_DESCRIPTION = "zone_description";
    public static final String COLUMN_ID = "zone_id";
    public static final String COLUMN_NAME = "zone_name";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_ZONES_DESCRIPTION + "("
            + COLUMN_ID  + " int NOT NULL PRIMARY KEY, "
            + COLUMN_NAME + " varchar(255) NOT NULL "
            +
            ");";

    public ZonesSQLiteDatabase(Context context) {
        super(context, SQLiteCommon.DATABASE_NAME, null, SQLiteCommon.DATABASE_VERSION);

        System.out.println("ZonesSQLiteDatabase: Create my Zone Database!: "+DATABASE_CREATE);
    }

    public ZonesSQLiteDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        System.out.println("onCreate: Create my Zone Database!: "+DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        System.out.println("onUpgrade: Upgrade my Zone Database!");
        Log.w(ZonesSQLiteDatabase.class.getName(),
                "Upgrading database from version"
                        + oldVersion + " to " + newVersion
        );
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_ZONES_DESCRIPTION);
        onCreate(db);
    }
}
