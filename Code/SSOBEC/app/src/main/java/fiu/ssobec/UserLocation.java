package fiu.ssobec;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Maria on 2/28/2015.
 */
public class UserLocation {

    public static final String LOG_TAG = "UserLocation";
    private LocationManager mlocManager;
    private LocationListener mlocListener;
    private double longitude;
    private double latitude;
    private  Location mLastLocation;

    public UserLocation(Context context) {
        mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        mlocManager.getProviders(true);

        Log.i(LOG_TAG, "Providers: "+ mlocManager.getAllProviders());

        mlocListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //Called when a new location is found by the network location provider.
                longitude =  location.getLongitude();
                latitude =  location.getLatitude();
                Log.i(LOG_TAG, "longitude: "+longitude+", latitude: "+latitude);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        Location myloc;

        // Register the listener with the Location Manager to receive location updates

        if (mlocManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
            Log.i(LOG_TAG, "Network Provider");
            mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
            myloc = mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(myloc != null)
            {
                latitude = myloc.getLatitude();
                longitude = myloc.getLongitude();
            }
        }

        if (mlocManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
            Log.i(LOG_TAG, "GPS Provider");
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
            myloc = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(myloc != null)
            {
                latitude = myloc.getLatitude();
                longitude = myloc.getLongitude();
            }
        }

        if (mlocManager.getAllProviders().contains(LocationManager.PASSIVE_PROVIDER)) {
            Log.i(LOG_TAG, "Passive Provider");
            mlocManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, mlocListener);
            myloc = mlocManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if(myloc != null)
            {
                latitude = myloc.getLatitude();
                longitude = myloc.getLongitude();
            }
        }


        Log.i(LOG_TAG, "first= longitude: "+longitude+", latitude: "+latitude);

    }

}
