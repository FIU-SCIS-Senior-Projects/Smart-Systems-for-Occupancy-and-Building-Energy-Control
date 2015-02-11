package fiu.ssobec.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import fiu.ssobec.Model.User;
import fiu.ssobec.UserSQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dalaidis on 2/10/2015.
 *
 *  This class is useful to maintain our User database
 *  and support adding new users and updating the
 *  information of users.
 */
public class DataAccessUser {

    //Database fields
    private static SQLiteDatabase db;
    private UserSQLiteDatabase dbHelp;

    private static String[] allCols = {     UserSQLiteDatabase.COLUMN_EMAIL,
                                            UserSQLiteDatabase.COLUMN_ID,
                                            UserSQLiteDatabase.COLUMN_NAME,
                                            UserSQLiteDatabase.COLUMN_LOGGEDIN};

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


    public static User createUser(String name,  int id, String email, int loggedIn)
    {
        System.out.println("createUser: Creating new user on my database!!!");
        ContentValues vals = new ContentValues();
        vals.put(UserSQLiteDatabase.COLUMN_NAME, name);
        vals.put(UserSQLiteDatabase.COLUMN_ID, id);
        vals.put(UserSQLiteDatabase.COLUMN_EMAIL, email);
        vals.put(UserSQLiteDatabase.COLUMN_LOGGEDIN, loggedIn);

        System.out.println("Email in vals: "+vals.getAsString(email));

        db.insert(UserSQLiteDatabase.TABLE_USER ,null ,vals);
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_USER,
                                allCols,
                                UserSQLiteDatabase.COLUMN_ID+" =? "+id,
                                null, null, null, null);

        cursor.moveToFirst();
        User nUser = getUserFromCursor(cursor);

        cursor.close();
        return nUser;
    }

    public User getUser (int loggedIn){
        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_USER,
                        allCols,
                         UserSQLiteDatabase.COLUMN_LOGGEDIN+" =? "+ loggedIn,
                          null, null, null, null);

        cursor.moveToFirst();
        User nUser = getUserFromCursor(cursor);

        cursor.close();
        return nUser;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<User>();

        Cursor cursor = db.query(UserSQLiteDatabase.TABLE_USER,
                allCols, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            User user = getUserFromCursor(cursor);
            userList.add(user);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return userList;
    }

    private static User getUserFromCursor(Cursor cursor) {
        User user = new User(cursor.getString(0),  //Name
                cursor.getInt(1),     //ID
                cursor.getString(2),  //Email
                cursor.getInt(3));    //LoggedIn
        return user;
    }

    public boolean doesTableExists()
    {
        if(dbHelp == null)
        {
            System.out.println("Table does not exist");
            return false;
        }
        else
        {
            return true;
        }
    }

}
