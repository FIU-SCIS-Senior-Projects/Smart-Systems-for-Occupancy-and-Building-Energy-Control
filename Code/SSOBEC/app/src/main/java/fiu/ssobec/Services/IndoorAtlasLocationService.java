package fiu.ssobec.Services;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;
import com.indooratlas.android.sdk.resources.IALocationListenerSupport;
import com.indooratlas.android.sdk.resources.IAResourceManager;

import java.util.Locale;

import fiu.ssobec.Activity.MyZonesActivity;
import fiu.ssobec.R;


/**
 *  This background service class will be in charge of keeping track of the user's current indoor location when started
 */
public class IndoorAtlasLocationService extends Service implements IALocationListener, IARegion.Listener{

    private final IBinder myBinder = new LocalBinder();
    public static boolean isRunning = false;
    private String floorID;
    //private MyZonesActivity.mapLoader mapLoader;
    private String TAG = "IndoorAtlasLocationService";


    //IndoorAtlas variables used in obtaining map coordinates

    private IALocationManager mIALocationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mIALocationManager = IALocationManager.create(this);
    }

    private boolean mCameraPositionNeedsUpdating;
    //private static final float HUE_IABLUE = 200.0f;
    public double latitude,longitude;
    private boolean fetch;
    private boolean removeMark;

    /**
     * Listener that handles location change events.
     */
    private IALocationListener mListener = new IALocationListenerSupport() {

        /**
         * Location changed, move marker and camera position.
         */
        @Override
        public void onLocationChanged(IALocation location) {

            Log.d(TAG, "new location received with coordinates: " + location.getLatitude() + "," + location.getLongitude());
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            latitude = latLng.latitude;
            longitude = latLng.longitude;
            Log.d(TAG,"What are the new latitude and longitude? "+latitude+", "+longitude);

        }
    };

    /**
     * Region listener that when:
     * <ul>
     * <li>region has entered; marks need to move camera and starts
     * loading floor plan bitmap</li>
     * <li>region has existed; clears marker</li>
     * </ul>.
     */
    private IARegion.Listener mRegionListener = new IARegion.Listener() {

        @Override
        public void onEnterRegion(IARegion region) {

            if (region.getType() == IARegion.TYPE_UNKNOWN) {
                Log.d(TAG,"We are in unknown region so nothing is happening ");
                //Toast.makeText(MapsOverlayActivity.this, "Moved out of map",Toast.LENGTH_LONG).show();
                return;
            }
            // entering new region, mark need to move camera
            Log.d(TAG,"New region entered so we are moving the camera");
            mCameraPositionNeedsUpdating = true;
            final String newId = region.getId();
            fetch = true;
            floorID = region.getId();

        }

        @Override
        public void onExitRegion(IARegion region) {
            removeMark = true;
            /*if(mapLoader != null)
            {
                if (mapLoader.getMarker() != null) {
                    mapLoader.removeMarker();
                }
            }*/

        }

    };

    public boolean dofetch()
    {
        return fetch;
    }

    public void setFetch(boolean fetch)
    {
        this.fetch = fetch;
    }

    public boolean removeMark()
    {
        return removeMark;
    }

    public void setMark(boolean mark)
    {
        removeMark = mark;
    }

    public String getFloorID()
    {
        return floorID;
    }

    public boolean getCameraNeedUpdate()
    {
        return mCameraPositionNeedsUpdating;
    }

    public void setCameraUpdate(boolean update)
    {
        mCameraPositionNeedsUpdating = update;
    }

    /*public class LocalBinder extends Binder {
        public IndoorAtlasLocationService getService(MyZonesActivity.mapLoader loader) {
            IndoorAtlasLocationService indoorAtlas = new IndoorAtlasLocationService(loader);
            return indoorAtlas;
        }
    }*/
    public class LocalBinder extends Binder {
        public IndoorAtlasLocationService getService() {
            return IndoorAtlasLocationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return myBinder;
    }

    @Override
    public void onLocationChanged(IALocation location) {
        Log.d(TAG, String.format(Locale.US, "%f,%f, accuracy: %.2f", location.getLatitude(), location.getLongitude(), location.getAccuracy()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged: " + status);
    }

    @Override
    public void onEnterRegion(IARegion region) {
        Log.d(TAG, "onEnterRegion: " + region.getType() + ", " + region.getId());
    }

    @Override
    public void onExitRegion(IARegion region) {
        Log.d(TAG, "onExitRegion: " + region.getType() + ", " + region.getId());
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Perform your long running operations here.
        isRunning = true;
        SharedPreferences.Editor editor = this.getSharedPreferences("fiu.ssobec", this.MODE_PRIVATE).edit();
        editor.putBoolean("fiu.ssobec.running", true);
        editor.apply();
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.smartbuildingicon);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(getText(R.string.notificaiontitle))
                .setContentText(getText(R.string.notificationmsg))
                .setSmallIcon(R.drawable.smartbuildingicon)
                .setLargeIcon(icon)
                .setWhen(System.currentTimeMillis())
                .build();
        startForeground(5, notification);
        initIndoorAtlas();
        return START_STICKY;

    }


    @Override
    public void onDestroy() {
        // Officially Declare this Service as "Not Running"
        isRunning = false;
        Log.d(TAG, "We are onDestroy");
        //mapLoader.destroy();
        mIALocationManager.destroy();
        SharedPreferences.Editor editor = this.getSharedPreferences("fiu.ssobec", this.MODE_PRIVATE).edit();
        editor.putBoolean("fiu.ssobec.running", false);
        editor.apply();

        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }

    public void initIndoorAtlas()
    {
        Log.d(TAG,"We are initializing the indoorAtlas API");
        // start receiving location updates & monitor region changes
        mIALocationManager.requestLocationUpdates(IALocationRequest.create(), mListener);
        mIALocationManager.registerRegionListener(mRegionListener);
        //String floorPlanId = "b4195361-c401-4147-be70-e040efaf8a0c";
        //mIALocationManager.setLocation(IALocation.from(IARegion.floorPlan(floorPlanId)));
        //fetch = true;
        //Log.d(TAG,"We are getting a "+request+" for requesting location and a "+register+" for registering Region Listener");
    }

    public double getPointX()
    {
        return longitude;
    }
    public double getPointY()
    {
        return latitude;
    }
    public LatLng getLatLng()
    {
        return new LatLng(latitude, longitude);
    }

}
