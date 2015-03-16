package fiu.ssobec.Model;

/**
 * Created by Dalaidis on 2/26/2015.
 */
public class OutsideWeather {

    private String dataTime;
    private int Temperature;
    private int cloudPercentage;

    public OutsideWeather(String dataTime, int cloudPercentage, int Temperature) {
        this.dataTime = dataTime;
        this.cloudPercentage = cloudPercentage;
        this.Temperature = Temperature;
    }

    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    public int getTemperature() {
        return Temperature;
    }

    public void setTemperature(int temperature) {
        this.Temperature = temperature;
    }

    public int getCloudPercentage() {
        return cloudPercentage;
    }

    public void setCloudPercentage(int cloudPercentage) {
        this.cloudPercentage = cloudPercentage;
    }
}
