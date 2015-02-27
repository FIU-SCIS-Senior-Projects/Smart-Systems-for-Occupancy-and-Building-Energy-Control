package fiu.ssobec.DataAccess;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
    private LocationManager mlocManager;
    private LocationListener mlocListener;
    private Context context;

    private float longitude=0;
    private float latitude=0;

    public DataAccessOwm(Context context) throws JSONException
    {
        mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        this.context = context;
        // Define a listener that responds to location updates
        mlocListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //Called when a new location is found by the network location provider.
                longitude = (float) location.getLongitude();
                latitude = (float) location.getLatitude();
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        //mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
        owm = new OpenWeatherMap("");
        try {
            cwd = owm.currentWeatherByCityName("Miami");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveWeatherData()
    {
        //cwd = owm.currentWeatherByCoordinates(latitude, longitude);

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

        cwd.getMainInstance().getMaxTemperature();
        cwd.getMainInstance().getMinTemperature();

        data_access.createOutsideWeather((int) cwd.getCloudsInstance().getPercentageOfClouds(),
                                         (int) cwd.getMainInstance().getMinTemperature(),
                                         (int) cwd.getMainInstance().getMaxTemperature());

        System.out.println("Weather Data");
        data_access.getAllWeatherData();

        data_access.close();
    }


}
