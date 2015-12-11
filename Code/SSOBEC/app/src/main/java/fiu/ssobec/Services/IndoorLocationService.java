package fiu.ssobec.Services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/*import com.indooratlas.android.CalibrationState;
import com.indooratlas.android.FloorPlan;
import com.indooratlas.android.FutureResult;
import com.indooratlas.android.IndoorAtlas;
import com.indooratlas.android.IndoorAtlasException;
import com.indooratlas.android.IndoorAtlasFactory;
import com.indooratlas.android.IndoorAtlasListener;
import com.indooratlas.android.ResultCallback;
import com.indooratlas.android.ServiceState;

import java.io.IOException;
import fiu.ssobec.R;*/



/**
 *  This background service class will be in charge of keeping track of the user's current indoor location when started
 */
public class IndoorLocationService {//} extends Service implements IndoorAtlasListener{

    /*private final IBinder myBinder = new LocalBinder();
    public static boolean isRunning = false;
    private String TAG = "IndoorLocationService";

    //IndoorAtlas variables used in obtaining map coordinates
    private IndoorAtlas mIndoorAtlas;
    private boolean mIsPositioning = false;
    private StringBuilder mSharedBuilder = new StringBuilder();
    private String apikey = "90f7edc9-1ede-4484-bb87-e73e6d2aad99";
    private String secretkey = "9FsHifk56VoFhw07iVASCQJBHWVtu(1cKFDFIk%5WmbRdztB8t3s3&6!x2B&%UzWm(&z5oPTZId2gJ(Zvpe)BGx4&O8vaM5pQ(VLVfzC972R&AnTRvxEyzrdBT6UZsCB";
    private String mVenueId;
    private String mFloorId;
    private String mFloorPlanId;
    private FloorPlan mFloorPlan;
    private boolean isReady;

    private long roundTrip;
    private double latitude;
    private double longitude;
    private double x;
    private double y;
    private double i;
    private double j;
    private double headingDegree;
    private double uncertainty;

    private Bitmap map;


    @Override
    public IBinder onBind(Intent arg0) {
        return myBinder;
    }

    public class LocalBinder extends Binder {
        public IndoorLocationService getService() {
            return IndoorLocationService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Perform your long running operations here.
        isRunning = true;
        isReady = false;
        Bundle extras = intent.getExtras();
        if(extras == null && isRunning == false){
            Log.d("Service","null");
            Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.smartbuildingicon);
            Notification notification = new Notification.Builder(this)
                    .setContentTitle(getText(R.string.notificaiontitle))
                    .setContentText(getText(R.string.notifcationmsgfailed))
                    .setSmallIcon(R.drawable.smartbuildingicon)
                    .setLargeIcon(icon)
                    .setWhen(System.currentTimeMillis())
                    .build();
            startForeground(5, notification);
        }
        else
        {

            if(extras != null)
            {
                Log.d("Service", "not null");
                String[] apikeys = (String[]) extras.get("apikeys");
                for(int i =0; i<apikeys.length;i++)
                {
                    Log.d("Service","we have "+apikeys[i]);
                }
                mFloorPlanId = apikeys[0];
                mFloorId = apikeys[1];
                mVenueId = apikeys[2];
            }

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
            isRunning = true;
            initIndoorAtlas();
        }
        return START_STICKY;

    }


    @Override
    public void onDestroy() {
        // Officially Declare this Service as "Not Running"
        isRunning = false;
        stopPositioning();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }

    /*
    `   IndoorAtlas methods
     */

    /*
    private void initIndoorAtlas() {

        try {
            Log.d(TAG, "Connecting with IndoorAtlas, apiKey: " + apikey);

            // obtain instance to positioning service, note that calibrating might begin instantly
            mIndoorAtlas = IndoorAtlasFactory.createIndoorAtlas( getApplicationContext(),this, apikey,secretkey);
            FutureResult<FloorPlan> result = mIndoorAtlas.fetchFloorPlan(mFloorPlanId);
            result.setCallback(new ResultCallback<FloorPlan>() {
                @Override
                public void onResult(final FloorPlan result) {
                    mFloorPlan = result;
                    loadFloorPlanImage(result);
                }

                @Override
                public void onSystemError(IOException e) {
                        Log.d(TAG,e.getMessage());
                }

                @Override
                public void onApplicationError(IndoorAtlasException e) {
                    Log.d(TAG,e.getMessage());
                }
                // handle error conditions too
            });
            Log.d(TAG,"IndoorAtlas instance created");
            isReady = true;
            togglePositioning();

        } catch (IndoorAtlasException ex) {
            Log.e("IndoorAtlas", "init failed", ex);
            Log.d(TAG, "init IndoorAtlas failed, " + ex.toString());
        }

    }

    void loadFloorPlanImage(FloorPlan floorPlan) {
        BitmapFactory.Options options = createBitmapOptions(floorPlan);
        FutureResult<Bitmap> result = mIndoorAtlas.fetchFloorPlanImage(floorPlan, options);
        result.setCallback(new ResultCallback<Bitmap>() {
            @Override
            public void onResult(final Bitmap result) {
                // now you have floor plan bitmap, do something with it
                map = result;
            }

            @Override
            public void onSystemError(IOException e) {
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onApplicationError(IndoorAtlasException e) {
                Log.d(TAG, e.getMessage());
            }
            // handle error conditions too
        });
    }

    private BitmapFactory.Options createBitmapOptions(FloorPlan floorPlan) {

        int reqWidth = 2048;
        int reqHeight = 2048;
        BitmapFactory.Options options = new BitmapFactory.Options();
        final int width = (int) floorPlan.dimensions[0];
        final int height = (int) floorPlan.dimensions[1];
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }

        }

        options.inSampleSize = inSampleSize;
        return options;

    }

    public Bitmap getMap()
    {
        return map;
    }

    private void togglePositioning() {
        if (mIsPositioning) {
            Log.d(TAG,"We are stopping positioning");
            stopPositioning();
        } else {
            Log.d(TAG,"We are starting positioning");
            startPositioning();
        }
    }

    private void stopPositioning() {
        mIsPositioning = false;
        if (mIndoorAtlas != null) {
            Log.d(TAG, "Stop positioning");
            mIndoorAtlas.stopPositioning();
        }
    }

    public int getFloorplanX()
    {
        return (int) mFloorPlan.dimensions[0];
    }

    public int getFloorplanY()
    {
        return (int) mFloorPlan.dimensions[1];
    }

    public double getPointX()
    {
        return i;
    }

    public double getPointY()
    {
        return j;
    }

    public float getLatitude()
    {
        return (float)latitude;
    }
    public float getLongitude()
    {
        return (float)longitude;
    }

    public double getUncertainty() { return uncertainty;}

    private void startPositioning() {
        if (mIndoorAtlas != null) {
            Log.d(TAG, String.format("startPositioning, venueId: %s, floorId: %s, floorPlanId: %s",mVenueId, mFloorId, mFloorPlanId));
            try {
                mIndoorAtlas.startPositioning(mVenueId, mFloorId, mFloorPlanId);
                Log.d(TAG,"We started indooratlas positioning");
                mIsPositioning = true;
            } catch (IndoorAtlasException e) {
                Log.d(TAG, "startPositioning failed: " + e);
            }
        } else {
            Log.d(TAG, "calibration not ready, cannot start positioning");
        }
    }

    @Override
    public void onServiceUpdate(ServiceState state) {
        Log.d(TAG,"We are onServiceUpdate");
        mSharedBuilder.setLength(0);
        roundTrip = state.getRoundtrip();
        latitude = state.getGeoPoint().getLatitude();
        longitude = state.getGeoPoint().getLongitude();
        x = state.getMetricPoint().getX();
        y = state.getMetricPoint().getY();
        i = state.getImagePoint().getI();
        j = state.getImagePoint().getJ();

        headingDegree = state.getHeadingDegrees();
        uncertainty = state.getUncertainty();
        mSharedBuilder.append("Location: ")
                .append("\n\troundtrip : ").append(state.getRoundtrip()).append("ms")
                .append("\n\tlat : ").append(state.getGeoPoint().getLatitude())
                .append("\n\tlon : ").append(state.getGeoPoint().getLongitude())
                .append("\n\tX [meter] : ").append(state.getMetricPoint().getX())
                .append("\n\tY [meter] : ").append(state.getMetricPoint().getY())
                .append("\n\tI [pixel] : ").append(state.getImagePoint().getI())
                .append("\n\tJ [pixel] : ").append(state.getImagePoint().getJ())
                .append("\n\theading : ").append(state.getHeadingDegrees())
                .append("\n\tuncertainty: ").append(state.getUncertainty());

        Log.d(TAG, mSharedBuilder.toString());
    }

    @Override
    public void onServiceFailure(int i, String reason) {
        Log.d(TAG, "onServiceFailure: reason : " + reason);
    }

    @Override
    public void onServiceInitializing() {
        Log.d(TAG, "onServiceInitializing");
    }

    @Override
    public void onServiceInitialized() {
        Log.d(TAG, "onServiceInitialized");
    }

    @Override
    public void onInitializationFailed(String reason) {
        Log.d(TAG, "onInitializationFailed: " + reason);
    }

    @Override
    public void onServiceStopped() {
        Log.d(TAG, "onServiceStopped");
    }

    @Override
    public void onCalibrationReady() {
        Log.d(TAG, "onCalibrationReady");
    }

    @Override
    public void onCalibrationInvalid() {

    }

    @Override
    public void onCalibrationFailed(String s) {
        Log.d(TAG,s);
    }

    @Override
    public void onCalibrationStatus(CalibrationState calibrationState) {
        Log.d(TAG, "onCalibrationStatus, percentage: " + calibrationState.getPercentage());
    }

    @Override
    public void onNetworkChangeComplete(boolean b) {

    }
    public boolean isReady()
    {
        return isReady && mFloorPlan != null;
    }
*/
}
