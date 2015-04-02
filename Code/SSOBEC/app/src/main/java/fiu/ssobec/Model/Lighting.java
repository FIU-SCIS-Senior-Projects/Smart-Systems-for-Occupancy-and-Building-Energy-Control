package fiu.ssobec.Model;

/**
 * Created by Maria on 2/23/2015.
 */
public class Lighting {


    private int zone_id;
    private String lighting_state; //'ON' - 'OFF'
    private String datetime;
    private double energy_usage_kwh;

    public Lighting(int zone_id, String datetime, String lighting_state, double energy_usage_kwh) {
        this.zone_id = zone_id;
        this.lighting_state = lighting_state;
        this.datetime = datetime;
        this.energy_usage_kwh = energy_usage_kwh;
    }

    public int getZone_id() {
        return zone_id;
    }

    public double getEnergy_usage_kwh() {
        return energy_usage_kwh;
    }

    public void setEnergy_usage_kwh(double energy_usage_kwh) {
        this.energy_usage_kwh = energy_usage_kwh;
    }

    public void setZone_id(int zone_id) {
        this.zone_id = zone_id;
    }

    public String getLighting_state() {
        return lighting_state;
    }

    public void setLighting_state(String lighting_state) {
        this.lighting_state = lighting_state;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "Lighting{" +
                "zone_id=" + zone_id +
                ", lighting_state='" + lighting_state + '\'' +
                ", datetime='" + datetime + '\'' +
                ", energy_usage_kwh=" + energy_usage_kwh +
                '}';
    }
}
