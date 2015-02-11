package fiu.ssobec;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Dalaidis on 2/7/2015.
 */
public class UserSQLiteDatabase extends SQLiteOpenHelper {

    public static final String TABLE_USER = "user";
    public static final String COLUMN_ID = "user_id";
    public static final String COLUMN_NAME = "user_name";
    public static final String COLUMN_EMAIL = "user_email";
    public static final String COLUMN_LOGGEDIN = "user_loggedin";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ssobec_internal.db";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_USER + " ("
            + COLUMN_ID  + " int NOT NULL PRIMARY KEY, "
            + COLUMN_NAME + " varchar(50) NOT NULL, "
            + COLUMN_EMAIL + " varchar(100) NOT NULL, "
            + COLUMN_LOGGEDIN + "int NOT NULL"
            +
            " );";

    public UserSQLiteDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        System.out.println("onCreate: Create my User Database!");
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(UserSQLiteDatabase.class.getName(),
                "Upgrading database from version"
                + oldVersion + " to " + newVersion
                );
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_USER);
        onCreate(db);
    }
}
