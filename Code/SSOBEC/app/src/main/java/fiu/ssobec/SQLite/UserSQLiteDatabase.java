package fiu.ssobec;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import fiu.ssobec.SQLite.SQLiteCommon;

/**
 * Created by Dalaidis on 2/7/2015.
 */
public class UserSQLiteDatabase extends SQLiteOpenHelper {

    public static final String TABLE_USER = "user";
    public static final String COLUMN_ID = "user_id";
    public static final String COLUMN_NAME = "user_name";
    public static final String COLUMN_EMAIL = "user_email";
    public static final String COLUMN_LOGGEDIN = "user_loggedin";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_USER + "("
            + COLUMN_LOGGEDIN + " int NOT NULL, "
            + COLUMN_ID + " int NOT NULL PRIMARY KEY, "
            + COLUMN_NAME + " varchar(50) NOT NULL, "
            + COLUMN_EMAIL + " varchar(100) NOT NULL "
            +");";

    public UserSQLiteDatabase(Context context) {

        super(context, SQLiteCommon.DATABASE_NAME, null, SQLiteCommon.DATABASE_VERSION);
        System.out.println("UserSQLiteDatabase: "+context.toString());
        System.out.println("Table: "+DATABASE_CREATE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        System.out.println("onCreate: Create my User Database!");
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        System.out.println("onUpgrade: Upgrade my User Database!");
        Log.w(UserSQLiteDatabase.class.getName(),
                "Upgrading database from version"
                + oldVersion + " to " + newVersion
                );
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_USER);
        onCreate(db);
    }
}
