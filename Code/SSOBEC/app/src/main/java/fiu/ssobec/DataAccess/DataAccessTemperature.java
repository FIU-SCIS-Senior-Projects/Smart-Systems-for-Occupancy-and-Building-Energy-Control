package fiu.ssobec.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

import fiu.ssobec.Model.Temperature;
import fiu.ssobec.Model.ZoneData;
import fiu.ssobec.SQLite.TemperatureSQLiteDatabase;
import fiu.ssobec.SQLite.ZoneDataSQLiteDatabase;

/**
 * Created by Dalaidis on 2/10/2015.
 */
public class DataAccessTemperature {

    //Create Database fields
    private SQLiteDatabase db;
    private ZoneDataSQLiteDatabase dbHelp;
    private String[]allCol = {ZoneDataSQLiteDatabase.COLUMN_ID,
            ZoneDataSQLiteDatabase.COLUMN_DATETIME,
            ZoneDataSQLiteDatabase.COLUMN_TEMPERATURE}
            ;

    public DataAccessTemperature(Context context) {
        dbHelp = new ZoneDataSQLiteDatabase(context);
    }

    public void open() throws SQLException {
        db = dbHelp.getWritableDatabase();
    }

    public void close() {
        dbHelp.close();
    }

    public Temperature createTemperature(int id, String dateTime, int lighting, int occupancy,
                                   int plugLoad, int temperature) {

        ContentValues vals = new ContentValues();
        vals.put(ZoneDataSQLiteDatabase.COLUMN_ID, id); //0
        vals.put(ZoneDataSQLiteDatabase.COLUMN_DATETIME, dateTime); //1
        vals.put(ZoneDataSQLiteDatabase.COLUMN_TEMPERATURE, temperature); //2


        db.insert(TemperatureSQLiteDatabase.TABLE_TEMPERATURE_DESCRIPTION, null, vals);
        Cursor cursor = db.query(TemperatureSQLiteDatabase.TABLE_TEMPERATURE_DESCRIPTION,
                allCol,
                null, null, null, null, null, null);

        cursor.moveToFirst();
        Temperature zDate= new Temperature(cursor.getInt(0), cursor.getString(1),
                                    cursor.getInt(2));

        cursor.close();

        return zDate;

    }

    }




