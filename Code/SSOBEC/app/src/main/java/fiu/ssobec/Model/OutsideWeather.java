package fiu.ssobec.Model;

/**
 * Created by Dalaidis on 2/26/2015.
 */
public class OutsideWeather {

    private String dataTime;
    private int minTemperature;
    private int maxTemperature;
    private int cloudPercentage;

    public OutsideWeather(String dataTime, int cloudPercentage, int maxTemperature, int minTemperature) {
        this.dataTime = dataTime;
        this.cloudPercentage = cloudPercentage;
        this.maxTemperature = maxTemperature;
        this.minTemperature = minTemperature;
    }

    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    public int getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(int maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public int getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(int minTemperature) {
        this.minTemperature = minTemperature;
    }

    public int getCloudPercentage() {
        return cloudPercentage;
    }

    public void setCloudPercentage(int cloudPercentage) {
        this.cloudPercentage = cloudPercentage;
    }
}
