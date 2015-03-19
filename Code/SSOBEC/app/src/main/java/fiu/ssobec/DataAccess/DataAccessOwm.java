package fiu.ssobec.DataAccess;

import android.content.Context;
import android.util.Log;

import net.aksingh.owmjapis.CurrentWeather;
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

    public DataAccessOwm(Context context) throws JSONException
    {
        this.context = context;
        owm = new OpenWeatherMap("644d19e86282524c4e9a6c92abea0e6b");
        try {
            cwd = owm.currentWeatherByCityName("Miami");

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void testingWeatherData()
    {
        Log.i(LOG_TAG, "Fake Weather Data");
    }

}
