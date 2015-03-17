package fiu.ssobec.Model;

/**
 * Created by Dalaidis on 2/24/2015.
 */
public class PlugLoad {
    private int zone_id;
    private String status;
    private String datetime;
    private String app_name;
    private String app_type;
    private int energy_usage_kwh;

    public PlugLoad(int zone_id, String datetime, String status, String app_name, String app_type, int energy_usage_kwh) {
        this.zone_id = zone_id;
        this.status = status;
        this.datetime = datetime;
        this.app_type = app_type;
        this.app_name = app_name;
        this.energy_usage_kwh = energy_usage_kwh;
    }




    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getApp_type() {
        return app_type;
    }

    public void setApp_type(String app_type) {
        this.app_type = app_type;
    }

    public int getEnergy_usage_kwh() {
        return energy_usage_kwh;
    }

    public void setEnergy_usage_kwh(int energy_usage_kwh) {
        this.energy_usage_kwh = energy_usage_kwh;
    }

    public int getZone_id() {
        return zone_id;
    }

    public void setZone_id(int zone_id) {
        this.zone_id = zone_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}


