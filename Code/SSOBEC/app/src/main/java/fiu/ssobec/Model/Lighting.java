package fiu.ssobec.Model;

/**
 * Created by Maria on 2/23/2015.
 */
public class Lighting {


    private int zone_id;
    private int lighting_state;
    private String datetime;

    public Lighting(int zone_id, int lighting_state, String datetime) {
        this.zone_id = zone_id;
        this.lighting_state = lighting_state;
        this.datetime = datetime;
    }

    public int getZone_id() {
        return zone_id;
    }

    public void setZone_id(int zone_id) {
        this.zone_id = zone_id;
    }

    public int getLighting_state() {
        return lighting_state;
    }

    public void setLighting_state(int lighting_state) {
        this.lighting_state = lighting_state;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
