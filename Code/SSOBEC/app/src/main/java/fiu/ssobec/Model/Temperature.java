package fiu.ssobec.Model;

/**
 * Created by Dalaidis on 2/11/2015.
 */
public class Temperature {
    private int zone_id;
    private int temperature;
    private String datetime;

    public Temperature(int zone_id, String datetime, int temperature) {
        this.zone_id = zone_id;
        this.datetime = datetime;
        this.temperature = temperature;

    }


    public int getZone_id() {
        return zone_id;
    }

    public void setZone_id(int zone_id) {
        this.zone_id = zone_id;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}

