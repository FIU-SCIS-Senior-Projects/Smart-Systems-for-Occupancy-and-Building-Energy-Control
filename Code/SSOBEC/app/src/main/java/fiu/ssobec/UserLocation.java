package fiu.ssobec;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by Maria on 2/28/2015.
 */
public class UserLocation {


    private LocationManager mlocManager;
    private LocationListener mlocListener;
    private float longitude;
    private float latitude;

    public UserLocation(Context context) {
        mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

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
    }
}
