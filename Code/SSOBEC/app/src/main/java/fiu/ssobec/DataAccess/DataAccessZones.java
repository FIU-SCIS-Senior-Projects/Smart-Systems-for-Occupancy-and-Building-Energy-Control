package fiu.ssobec.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

import fiu.ssobec.Model.User;
import fiu.ssobec.Model.Zones;
import fiu.ssobec.SQLite.ZonesSQLiteDatabase;

/**
 * Created by Maria on 2/11/2015.
 */
public class DataAccessZones {

    //Database fields
    private static SQLiteDatabase db;
    private ZonesSQLiteDatabase dbHelp;

    private static String[] allCols = {   ZonesSQLiteDatabase.COLUMN_ID,
                                          ZonesSQLiteDatabase.COLUMN_NAME};

    public DataAccessZones(Context context)
    {
        dbHelp = new ZonesSQLiteDatabase(context);
    }

    public void open() throws SQLException {
        db = dbHelp.getWritableDatabase();
    }

    public void close() {
        dbHelp.close();
    }

    public static Zones createZones(String zone_name,  int id)
    {
        System.out.println("create zone: Creating new zone on my database!");
        ContentValues vals = new ContentValues();
        vals.put(ZonesSQLiteDatabase.COLUMN_ID, id);
        vals.put(ZonesSQLiteDatabase.COLUMN_NAME, zone_name);

        System.out.println("Name in vals: "+vals.getAsString(zone_name));

        db.insert(ZonesSQLiteDatabase.TABLE_ZONES_DESCRIPTION ,null ,vals);
        Cursor cursor = db.query(ZonesSQLiteDatabase.TABLE_ZONES_DESCRIPTION,
                allCols,
                ZonesSQLiteDatabase.COLUMN_ID+" = "+id,
                null, null, null, null);

        cursor.moveToFirst();
        Zones zones = getZoneFromCursor(cursor);

        cursor.close();
        return zones;
    }

    public Zones getZone (int zone_id){

        Cursor cursor = db.query(ZonesSQLiteDatabase.TABLE_ZONES_DESCRIPTION,
                allCols,
                ZonesSQLiteDatabase.COLUMN_ID+" = "+ zone_id,
                null, null, null, null);

        if (cursor.moveToFirst()) {
            Zones zone = getZoneFromCursor(cursor);
            cursor.close();
            return zone;
        }
        else
        {
            return null;
        }

    }

    private static Zones getZoneFromCursor(Cursor cursor) {
        Zones zones = new Zones(cursor.getInt(0),         //ID
                                cursor.getString(1));     //Name
        return zones;
    }
}
