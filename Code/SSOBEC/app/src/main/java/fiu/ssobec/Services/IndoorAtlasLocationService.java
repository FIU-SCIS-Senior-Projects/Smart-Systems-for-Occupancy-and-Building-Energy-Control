package fiu.ssobec.Services;

/*import android.app.DownloadManager;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;
import com.indooratlas.android.sdk.resources.IAFloorPlan;
import com.indooratlas.android.sdk.resources.IALatLng;
import com.indooratlas.android.sdk.resources.IALocationListenerSupport;
import com.indooratlas.android.sdk.resources.IAResourceManager;
import com.indooratlas.android.sdk.resources.IAResult;
import com.indooratlas.android.sdk.resources.IAResultCallback;
import com.indooratlas.android.sdk.resources.IATask;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import fiu.ssobec.R;*/


/**
 *  This background service class will be in charge of keeping track of the user's current indoor location when started
 */
public class IndoorAtlasLocationService {//extends Service implements IALocationListener, IARegion.Listener{

    /*private final IBinder myBinder = new LocalBinder();
    public static boolean isRunning = false;
    private String TAG = "IndoorLocationService";

    //IndoorAtlas variables used in obtaining map coordinates

    // blue dot radius in meters
    private static final float dotRadius = 1.0f;

    private IALocationManager mIALocationManager;
    private IATask<IAFloorPlan> mPendingAsyncResult;

    private IAResourceManager mFloorPlanManager;
    private long mRequestStartTime;
    private long mDownloadId;
    private DownloadManager mDownloadManager;


    private boolean mIsPositioning = false;
    private StringBuilder mSharedBuilder = new StringBuilder();
    private String apikey = "90f7edc9-1ede-4484-bb87-e73e6d2aad99";
    private String secretkey = "9FsHifk56VoFhw07iVASCQJBHWVtu(1cKFDFIk%5WmbRdztB8t3s3&6!x2B&%UzWm(&z5oPTZId2gJ(Zvpe)BGx4&O8vaM5pQ(VLVfzC972R&AnTRvxEyzrdBT6UZsCB";
    private String mVenueId;
    private String mFloorId;
    private String mFloorPlanId;
    private IAFloorPlan mFloorPlan;
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

    private IALocationListener mLocationListener = new IALocationListenerSupport() {
        @Override
        public void onLocationChanged(IALocation location) {
            Log.d(TAG, "location is: " + location.getLatitude() + "," + location.getLongitude());
            IALatLng latLng = new IALatLng(location.getLatitude(), location.getLongitude());
            PointF point = mFloorPlan.coordinateToPoint(latLng);
            /*if (mImageView != null && mImageView.isReady()) {

                mImageView.setDotCenter(point);
                mImageView.postInvalidate();
            }
        }
    };

    private IARegion.Listener mRegionListener = new IARegion.Listener() {

        @Override
        public void onEnterRegion(IARegion region) {
            if (region.getType() == IARegion.TYPE_FLOOR_PLAN) {
                String id = region.getId();
                Log.d(TAG, "floorPlan changed to " + id);
                //Toast.makeText(ImageViewActivity.this, id, Toast.LENGTH_SHORT).show();
                fetchFloorPlan(id);
            }
        }
        @Override
        public void onExitRegion(IARegion region) {
            // leaving a previously entered region
        }

    };

    public class LocalBinder extends Binder {
        public IndoorAtlasLocationService getService() {
            return IndoorAtlasLocationService.this;
        }
    }
    @Override
    public IBinder onBind(Intent arg0) {
        return myBinder;
    }

    public void requestUpdates() {
        mRequestStartTime = SystemClock.elapsedRealtime();
        mIALocationManager.requestLocationUpdates(IALocationRequest.create(), this);
        //log("requestLocationUpdates");
    }

    public void removeUpdates() {
        //log("removeLocationUpdates");
        mIALocationManager.removeLocationUpdates(this);
    }

    public void setLocation() {
        askLocation();
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
        isReady = false;
        Bundle extras = intent.getExtras();
        if(extras == null && isRunning == false){
            Log.d("Service", "null");
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
            mIALocationManager = IALocationManager.create(this);

            Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();

            //location = new LocationThread(this,ACTIVITY_SERVICE,apikeys);
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
            //location.start();
        }
        return START_STICKY;

    }


    @Override
    public void onDestroy() {
        //location.setRun(false);
        // Officially Declare this Service as "Not Running"
        isRunning = false;
        mIALocationManager.destroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }



    private void initIndoorAtlas() {

        try {
            Log.d(TAG, "Connecting with IndoorAtlas, apiKey: " + apikey);

            // obtain instance to positioning service, note that calibrating might begin instantly
            mIndoorAtlas = IndoorAtlasFactory.createIndoorAtlas(
                    getApplicationContext(),
                    this, // IndoorAtlasListener
                    apikey,
                    secretkey);
            FutureResult<FloorPlan> result = mIndoorAtlas.fetchFloorPlan(mFloorPlanId);
            result.setCallback(new ResultCallback<FloorPlan>() {
                @Override
                public void onResult(final FloorPlan result) {
                    mFloorPlan = result;
                    loadFloorPlanImage(result);
                }

                @Override
                public void onSystemError(IOException e) {

                }

                @Override
                public void onApplicationError(IndoorAtlasException e) {

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
                //updateImageViewInUiThread(result);
                map = result;
                // MyZonesActivity.updateMapView(result);
            }

            @Override
            public void onSystemError(IOException e) {

            }

            @Override
            public void onApplicationError(IndoorAtlasException e) {

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
            stopPositioning();
        } else {
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
    public boolean isReady()
    {
        return isReady && mFloorPlan != null;
    }


     // Fetches floor plan data from IndoorAtlas server. Some room for cleaning up!!

    private void fetchFloorPlan(String id) {
        cancelPendingNetworkCalls();
        final IATask<IAFloorPlan> asyncResult = mFloorPlanManager.fetchFloorPlanWithId(id);
        mPendingAsyncResult = asyncResult;
        if (mPendingAsyncResult != null) {
            mPendingAsyncResult.setCallback(new IAResultCallback<IAFloorPlan>() {
                @Override
                public void onResult(IAResult<IAFloorPlan> result) {
                    Log.d(TAG, "fetch floor plan result:" + result);
                    if (result.isSuccess() && result.getResult() != null) {
                        mFloorPlan = result.getResult();
                        String fileName = mFloorPlan.getId() + ".img";
                        String filePath = Environment.getExternalStorageDirectory() + "/"
                                + Environment.DIRECTORY_DOWNLOADS + "/" + fileName;
                        File file = new File(filePath);
                        if (!file.exists()) {
                            DownloadManager.Request request =
                                    new DownloadManager.Request(Uri.parse(mFloorPlan.getUrl()));
                            request.setDescription("IndoorAtlas floor plan");
                            request.setTitle("Floor plan");
                            // requires android 3.2 or later to compile
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                request.allowScanningByMediaScanner();
                                request.setNotificationVisibility(DownloadManager.
                                        Request.VISIBILITY_HIDDEN);
                            }
                            request.setDestinationInExternalPublicDir(Environment.
                                    DIRECTORY_DOWNLOADS, fileName);

                            mDownloadId = mDownloadManager.enqueue(request);
                        } else {
                            showFloorPlanImage(filePath);
                        }
                    } else {
                        // do something with error
                        if (!asyncResult.isCancelled()) {
                           // Toast.makeText(ImageViewActivity.this,
                                    (result.getError() != null
                                            ? "error loading floor plan: " + result.getError()
                                            : "access to floor plan denied"), Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                }
            }, Looper.getMainLooper()); // deliver callbacks in main thread
        }
    }

    private void cancelPendingNetworkCalls() {
        if (mPendingAsyncResult != null && !mPendingAsyncResult.isCancelled()) {
            mPendingAsyncResult.cancel();
        }
    }
    private void showFloorPlanImage(String filePath) {
        Log.w(TAG, "showFloorPlanImage: " + filePath);
        mImageView.setRadius(mFloorPlan.getMetersToPixels() * dotRadius);
        mImageView.setImage(ImageSource.uri(filePath));
    }

*/
}
