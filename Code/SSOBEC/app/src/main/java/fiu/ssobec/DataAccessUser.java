package fiu.ssobec;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

/**
 * Created by Maria on 2/10/2015.
 *
 *  This class is useful to maintain our User database
 *  and support adding new users and updating the
 *  information of users.
 */
public class DataAccessUser {

    //Database fields
    private static SQLiteDatabase db;
    private UserSQLiteDatabase dbHelp;

    private static String[] allCols = {    UserSQLiteDatabase.COLUMN_EMAIL,
                                    UserSQLiteDatabase.COLUMN_ID,
                                    UserSQLiteDatabase.COLUMN_NAME};

    public DataAccessUser(Context context)
    {
        dbHelp = new UserSQLiteDatabase(context);
    }

    public void open() throws SQLException{
        db = dbHelp.getWritableDatabase();
    }

    public void close() {
        dbHelp.close();
    }

    public static User createUser(String name,  int id, String email)
    {
        System.out.println("createUser: Creating new user on my database!!!");
        ContentValues vals = new ContentValues();
        vals.put(UserSQLiteDatabase.COLUMN_NAME, name);
        vals.put(UserSQLiteDatabase.COLUMN_ID, id);
        vals.put(UserSQLiteDatabase.COLUMN_EMAIL, email);

        db.insert(UserSQLiteDatabase.TABLE_USER ,null ,vals);
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_USER,
                                allCols,
                                UserSQLiteDatabase.COLUMN_ID+" = "+id,
                                null, null, null, null);

        System.out.println("createUser: New user: "+name+" is inserted");

        cursor.moveToFirst();
        User nUser = new User(cursor.getString(0),  //Name
                              cursor.getInt(1),     //ID
                              cursor.getString(2)); //Email

        cursor.close();
        return nUser;
    }
}
