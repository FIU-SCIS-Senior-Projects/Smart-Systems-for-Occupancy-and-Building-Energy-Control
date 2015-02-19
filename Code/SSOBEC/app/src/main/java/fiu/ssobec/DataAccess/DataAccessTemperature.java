package fiu.ssobec.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

import fiu.ssobec.Model.Temperature;
import fiu.ssobec.Model.ZoneData;
import fiu.ssobec.SQLite.TemperatureSQLiteDatabase;

/**
 * Created by Dalaidis on 2/10/2015.
 */
public class DataAccessTemperature {

    //Create Database fields
    private SQLiteDatabase db;
    private TemperatureSQLiteDatabase dbHelp;
    private String[]allCol = {TemperatureSQLiteDatabase.COLUMN_ID,
            TemperatureSQLiteDatabase.COLUMN_NAME,
            TemperatureSQLiteDatabase.TABLE_TEMPERATURE_DESCRIPTION}
            ;

    public DataAccessTemperature(Context context) {
        dbHelp = new TemperatureSQLiteDatabase(context);
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
        vals.put(TemperatureSQLiteDatabase.COLUMN_ID, id); //0
        vals.put(TemperatureSQLiteDatabase.COLUMN_NAME, dateTime); //1
        vals.put(TemperatureSQLiteDatabase.TABLE_TEMPERATURE_DESCRIPTION, temperature); //2


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




