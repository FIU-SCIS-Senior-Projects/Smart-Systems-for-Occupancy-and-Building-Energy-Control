package fiu.ssobec.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

import fiu.ssobec.Model.ZoneData;
import fiu.ssobec.SQLite.ZoneDataSQLiteDatabase;
import fiu.ssobec.SQLite.ZonesSQLiteDatabase;

/**
 * Created by Dalaidis on 2/10/2015.
 */
public class DataAccessZoneData {

    //Create Database fields
    private SQLiteDatabase db;
    private ZoneDataSQLiteDatabase dbHelp;
    private String[]allCol = {ZoneDataSQLiteDatabase.COLUMN_ID,
            ZoneDataSQLiteDatabase.COLUMN_DATETIME,
            ZoneDataSQLiteDatabase.COLUMN_LIGHTING,
            ZoneDataSQLiteDatabase.COLUMN_OCCUPANCY,
            ZoneDataSQLiteDatabase.COLUMN_PLUGLOAD,
            ZoneDataSQLiteDatabase.COLUMN_TEMPERATURE} ;

    public DataAccessZoneData(Context context) {
        dbHelp = new ZoneDataSQLiteDatabase(context);
    }

    public void open() throws SQLException {
        db = dbHelp.getWritableDatabase();
    }

    public void close() {
        dbHelp.close();
    }

    public ZoneData createZoneData(int id, String dateTime, int lighting, int occupancy,
                                   int plugLoad, int temperature) {

        ContentValues vals = new ContentValues();
        vals.put(ZoneDataSQLiteDatabase.COLUMN_ID, id); //0
        vals.put(ZoneDataSQLiteDatabase.COLUMN_DATETIME, dateTime); //1
        vals.put(ZoneDataSQLiteDatabase.COLUMN_OCCUPANCY, occupancy); //2
        vals.put(ZoneDataSQLiteDatabase.COLUMN_TEMPERATURE, temperature); //3
        vals.put(ZoneDataSQLiteDatabase.COLUMN_PLUGLOAD,plugLoad); //4
        vals.put(ZoneDataSQLiteDatabase.COLUMN_LIGHTING, lighting); //5



        db.insert(ZoneDataSQLiteDatabase.TABLE_ZONES, null, vals);
        Cursor cursor = db.query(ZoneDataSQLiteDatabase.TABLE_ZONES,
                allCol,
                null, null, null, null, null, null);

        cursor.moveToFirst();
        ZoneData zDate= new ZoneData (cursor.getInt(0), cursor.getString(1),
                                    cursor.getInt(2), cursor.getInt(3),
                                    cursor.getInt(4), cursor.getInt(5));


        cursor.close();

        return zDate;


    }


    }




