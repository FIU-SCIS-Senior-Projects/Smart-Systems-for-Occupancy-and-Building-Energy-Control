package fiu.ssobec.Model;

import java.sql.Date;

/**
 * Created by Maria on 2/19/2015.
 */
public class Occupancy {

    private String date_time;
    private int zone_id;
    private int occupancy;

    public Occupancy(String date_time, int zone_id, int occupancy) {
        this.date_time = date_time;
        this.zone_id = zone_id;
        this.occupancy = occupancy;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public int getZone_id() {
        return zone_id;
    }

    public void setZone_id(int zone_id) {
        this.zone_id = zone_id;
    }

    public int getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(int occupancy) {
        this.occupancy = occupancy;
    }
}
