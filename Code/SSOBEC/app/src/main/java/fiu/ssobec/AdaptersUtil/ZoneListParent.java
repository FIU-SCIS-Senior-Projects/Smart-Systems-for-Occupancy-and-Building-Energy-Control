package fiu.ssobec.AdaptersUtil;

import java.io.Serializable;

/**
 * Created by Maria on 4/14/2015.
 * Implements Serializable so that we can pass this Object between Activities - irvincardenas
 */
public class ZoneListParent implements Serializable {

    private String zone_name;
    private int zone_id;

    private String zone_location;
    private boolean zone_added;

    private int zone_windows;


    public int getZone_windows() {
        return zone_windows;
    }

    public void setZone_windows(int zone_windows) {
        this.zone_windows = zone_windows;
    }

    public String getZone_location() {
        return zone_location;
    }

    public void setZone_location(String zone_location) {
        this.zone_location = zone_location;
    }

    public boolean isZone_added() {
        return zone_added;
    }

    public void setZone_added(boolean zone_added) {
        this.zone_added = zone_added;
    }

    public String getZone_name() {
        return zone_name;
    }

    public void setZone_name(String zone_name) {
        this.zone_name = zone_name;
    }

    public int getZone_id() {
        return zone_id;
    }

    public void setZone_id(int zone_id) {
        this.zone_id = zone_id;
    }


}
