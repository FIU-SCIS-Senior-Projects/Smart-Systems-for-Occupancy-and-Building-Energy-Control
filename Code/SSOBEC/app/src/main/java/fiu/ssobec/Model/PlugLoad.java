package fiu.ssobec.Model;

/**
 * Created by Dalaidis on 2/24/2015.
 */
public class PlugLoad {
    private int zone_id;
    private int plugLoad;
    private String datetime;

    public PlugLoad(int zone_id, String datetime, int plugLoad) {
        this.zone_id = zone_id;
        this.datetime = datetime;
        this.plugLoad = plugLoad;

    }


    public int getZone_id() {
        return zone_id;
    }

    public void setZone_id(int zone_id) {
        this.zone_id = zone_id;
    }

    public int getPlugLoad() {
        return plugLoad;
    }

    public void setPlugLoad(int plugLoad) {
        this.plugLoad = plugLoad;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}


