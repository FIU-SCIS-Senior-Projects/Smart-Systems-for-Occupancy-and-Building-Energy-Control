package fiu.ssobec;

/**
 * Created by Maria on 2/10/2015.
 */
public class Zones {

    private int zone_id;
    private String zone_name;

    public Zones(int zone_id, String zone_name) {
        this.zone_id = zone_id;
        this.zone_name = zone_name;
    }

    public int getZone_id() {
        return zone_id;
    }

    public void setZone_id(int zone_id) {
        this.zone_id = zone_id;
    }

    public String getZone_name() {
        return zone_name;
    }

    public void setZone_name(String zone_name) {
        this.zone_name = zone_name;
    }
}
