package fiu.ssobec.AdaptersUtil;

/**
 * Created by Maria on 4/14/2015.
 */
public class ZoneListParent {

    private String zone_name;
    private int zone_id;
    private boolean zone_added;

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
