package fiu.ssobec.DataAccess;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import net.aksingh.owmjapis.CurrentWeather;
import net.aksingh.owmjapis.DailyForecast;
import net.aksingh.owmjapis.OpenWeatherMap;

import org.json.JSONException;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Maria on 2/26/2015.
 */
public class DataAccessOwm {


    public static final String LOG_TAG = "DataAccessOwm";

    private OpenWeatherMap owm;
    private CurrentWeather cwd;
    private Context context;
    public static final String myweatherpreferences = "myprefs";
    public static final String temperature_forecast = "temp_forecast";
    static SharedPreferences sharedpreferences;


    public DataAccessOwm(Context context) throws JSONException
    {
        this.context = context;
        owm = new OpenWeatherMap("");
        try {
            cwd = owm.currentWeatherByCityName("Miami");

        } catch (IOException e) {
            e.printStackTrace();
        }

        sharedpreferences = context.getSharedPreferences(myweatherpreferences, context.MODE_PRIVATE);

    }

    public void saveWeatherData()
    {
        DataAccessUser data_access = new DataAccessUser(context);
        float Clouds = 0;
        float Temp = 0;

        try {
            data_access.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(cwd.isValid())
        {
            if(cwd.hasCloudsInstance())
            {
                Clouds = cwd.getCloudsInstance().getPercentageOfClouds();
            }
            if(cwd.getMainInstance().hasTemperature())
            {
                Temp = cwd.getMainInstance().getTemperature();
            }

            if(Clouds != 0 && Temp != 0)
            {
                Log.i(LOG_TAG, "Clouds: "+Clouds+", Temperature: "+Temp);
                data_access.createOutsideWeather((int) Clouds, (int) Temp);
            }
        }
        data_access.close();
    }

    private float getForeCastWeather()  throws JSONException
    {
        byte mByte = 1;
        float forecast_temperature = 0;

        try {
            DailyForecast fr = owm.dailyForecastByCityName("Miami", mByte);
            if(fr.getForecastInstance(0).getTemperatureInstance().hasDayTemperature())
            {
                forecast_temperature = fr.getForecastInstance(0).getTemperatureInstance().getDayTemperature();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(temperature_forecast, forecast_temperature+"");
                editor.commit();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return forecast_temperature;
    }

    public static String getMyForecast()
    {
        if(sharedpreferences.contains(temperature_forecast))
        {
            return sharedpreferences.getString(temperature_forecast, "");
        }
        else
            return null;
    }

}
