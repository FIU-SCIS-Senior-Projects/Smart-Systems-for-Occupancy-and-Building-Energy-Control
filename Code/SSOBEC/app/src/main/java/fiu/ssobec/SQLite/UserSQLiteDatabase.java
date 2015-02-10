package fiu.ssobec.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Maria on 2/7/2015.
 */
public class UserSQLiteDatabase extends SQLiteOpenHelper {

    public static final String TABLE_USER = "user";
    public static final String COLUMN_ID = "user_id";
    public static final String COLUMN_NAME = "user_name";
    public static final String COLUMN_EMAIL = "user_email";

    private static final String DATABASE_NAME = "commments.db";
    private static final int DATABASE_VERSION = 1;

    //Database creation sql database
    private static final String DATABASE_CREATE = "create table "
            +TABLE_USER + "("
            +COLUMN_ID + "int PRIMARY KEY, "
            +COLUMN_NAME + "varchar(50) NOT NULL, "
            +COLUMN_EMAIL + "varchar(50) NOT NULL "
            +
            ");";



    public UserSQLiteDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
