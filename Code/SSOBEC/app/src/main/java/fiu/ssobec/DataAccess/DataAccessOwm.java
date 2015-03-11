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

    private float longitude=0;
    private float latitude=0;

    public DataAccessOwm(Context context) throws JSONException
    {
        this.context = context;
        owm = new OpenWeatherMap("");
        try {
            cwd = owm.currentWeatherByCityName("Miami");
            Log.i(LOG_TAG, "Temperature: "+cwd.getMainInstance().getTemperature());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveWeatherData()
    {
        DataAccessUser data_access = new DataAccessUser(context);

        try {
            data_access.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(cwd.isValid())
        {
            Log.i(LOG_TAG, "You are currently in City Name: "+cwd.getCityName()+
                    ", Coordinates: ("+latitude+", "+longitude+")");
        }
        Log.i(LOG_TAG, "Clouds: "+cwd.getCloudsInstance().getPercentageOfClouds());

        data_access.createOutsideWeather((int) cwd.getCloudsInstance().getPercentageOfClouds(),
                                         (int) cwd.getMainInstance().getMinTemperature(),
                                         (int) cwd.getMainInstance().getTemperature());
        data_access.getAllWeatherData();
        data_access.close();
    }


}
