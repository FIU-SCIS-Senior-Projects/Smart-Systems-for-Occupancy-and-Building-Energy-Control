package fiu.ssobec.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Dalaidis on 2/11/2015.
 */
public class TemperatureSQLiteDatabase extends SQLiteOpenHelper {
    public static final String TABLE_TEMPERATURE_DESCRIPTION = "temperature_description";
    public static final String COLUMN_ID = "temperature_id";
    public static final String COLUMN_NAME = "temperature_name";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_TEMPERATURE_DESCRIPTION + " ("
            + COLUMN_ID  + " int NOT NULL PRIMARY KEY, "
            + COLUMN_NAME + " varchar(255) NOT NULL "
            +
            ");";

    public TemperatureSQLiteDatabase(Context context) {
        super(context, SQLiteCommon.DATABASE_NAME, null, SQLiteCommon.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        System.out.println("onCreate: Create my User Database!");
        db.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("onUpgrade: Upgrade my Temperature Database!");
        Log.w(UserSQLiteDatabase.class.getName(),
                "Upgrading database from version"
                        + oldVersion + "to" + newVersion);
        db.execSQL("DROP TABLE IF EXISTS" +TABLE_TEMPERATURE_DESCRIPTION);
        onCreate(db);

    }
}
