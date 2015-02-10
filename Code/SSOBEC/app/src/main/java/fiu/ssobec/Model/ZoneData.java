package fiu.ssobec.Model;

/**
 * Created by Dalaidis on 2/10/2015.
 */
public class ZoneData {

    private int zone_id;
    private int occupancy;
    private int temperature;
    private int plugload;
    private int lightting;
    private String datetime;

    public ZoneData(int zone_id, String datetime, int occupancy, int temperature, int plugload, int lightting) {
        this.zone_id = zone_id;
        this.datetime = datetime;
        this.occupancy = occupancy;
        this.temperature = temperature;
        this.plugload = plugload;
        this.lightting = lightting;
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

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getPlugload() {
        return plugload;
    }

    public void setPlugload(int plugload) {
        this.plugload = plugload;
    }

    public int getLightting() {
        return lightting;
    }

    public void setLightting(int lightting) {
        this.lightting = lightting;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
