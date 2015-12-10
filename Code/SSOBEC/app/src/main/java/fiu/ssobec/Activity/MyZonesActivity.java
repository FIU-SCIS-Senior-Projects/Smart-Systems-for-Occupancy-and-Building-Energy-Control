package fiu.ssobec.Activity;

import android.accounts.Account;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SyncStatusObserver;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.indooratlas.android.sdk.resources.IAFloorPlan;
import com.indooratlas.android.sdk.resources.IALatLng;
import com.indooratlas.android.sdk.resources.IAResourceManager;
import com.indooratlas.android.sdk.resources.IAResult;
import com.indooratlas.android.sdk.resources.IAResultCallback;
import com.indooratlas.android.sdk.resources.IATask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import fiu.ssobec.Adapters.GridViewAdapter;
import fiu.ssobec.Adapters.MyRewardListAdapter;
import fiu.ssobec.AdaptersUtil.RewardListParent;
import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.DataAccess.ExternalDatabaseController;
import fiu.ssobec.Model.Room;
import fiu.ssobec.Model.User;
import fiu.ssobec.Model.Zones;
import fiu.ssobec.R;
import fiu.ssobec.Services.IndoorAtlasLocationService;
import fiu.ssobec.Synchronization.DataSync.AuthenticatorService;
import fiu.ssobec.Synchronization.SyncConstants;
import fiu.ssobec.Synchronization.SyncUtils;



/*
    MyZonesActivity is our Main and Launcher Activity
    Our application will only need that the user log in once.
    A column in the UserSQLiteDatabase 'loggedIn'
* */
public class MyZonesActivity extends AppCompatActivity{

    public static final int USER_LOGGEDIN = 1;
    public static final String LOG_TAG = "MyZonesActivity";
    public static final String GETZONES_PHP = "http://smartsystems-dev.cs.fiu.edu/zonepost.php";
    public static final String UPDATEUSERLOCATION_PHP = "http://smartsystems-dev.cs.fiu.edu/updateuserlocation.php";
    public static final String UPDATEROOMLOCATION_PHP = "http://smartsystems-dev.cs.fiu.edu/updateroomlocation.php";
    public static final String LOCATIONS_PHP = "http://smartsystems-dev.cs.fiu.edu/getUserLocations.php";
    public static final String GETROOMS_PHP = "http://smartsystems-dev.cs.fiu.edu/getfloorrooms.php";
    //public static final String GETBUILDINGS_PHP = "http://smartsystems-dev.cs.fiu.edu/getbuildings.php";
    public static final String plugload_award_descrp="Reward for little consumption of energy in plugload";
    public static final String lighting_award_descrp="Reward for turning off the lights before leaving the room";
    zoneLoader loader;
    private static DataAccessUser data_access;
    private final Handler handler = new Handler();
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private IndoorAtlasLocationService mService;
    private boolean isBound;
    private Context context;
    private mapLoader mapper;
    String currentFloorPlan;
    String currentFloor;
    MapView mapView;
    private Menu settingMenu;

    ArrayList<Room> rooms = new ArrayList<Room>();
    private ServiceConnection myConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,IBinder service) {
            Log.d("Service","We are re binded");
            IndoorAtlasLocationService.LocalBinder binder = (IndoorAtlasLocationService.LocalBinder) service;
            mapper = new mapLoader(context, email, mapView);
            mService = binder.getService();
            //mService = binder.getService(mapper);
            isBound = true;
            mapper.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
            mService = null;
            //mapper.cancel(false);
        }
    };

    private GestureDetector gesturedetector = null;
    private FrameLayout flContainer;
    //private LinearLayout ivLayer1;
    private MapView imgLayer2;

    private Object mSyncObserverHandle;
    public static int user_id;
    private String email;



    Dialog roomDialog;
    //Dialog variables to get user rating on zone conditions
    Dialog rankDialog;
    int [] ratings;
    TextView Dialogtext;

    //Dialog variables to choose indoor location to lock onto
    Dialog locationDialog;

    private String [] rewardNames = {"First", "Second", "Third", "Fourth", "Fifth"};

    private boolean isFacilityManager = false;

    //////////////////////////////////
    private ListView mListView;
    private Context mContext = this;

    private GridView gridViewButtons;
    private GridViewAdapter gridAdapter;
    private Bundle saved;

    /**
     *  Initialize Activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        saved = savedInstanceState;
        context = this.getApplicationContext();

        data_access = new DataAccessUser(this);
        //Retrieve the email that we stored when we logged in
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        SharedPreferences prefs = this.getSharedPreferences("fiu.ssobec", Context.MODE_PRIVATE);//getPreferences(MODE_PRIVATE);
        email = prefs.getString("fiu.ssobec.username", null);
        //email = sharedPref.getString(getString(R.string.emailpref),"");
        Log.d("SharedPref","What is the email reading? "+email);
        //Open the data access to the tables
        try {
            data_access.open();
        }
        catch (SQLException e) {
            System.err.println(LOG_TAG + ": " + e.toString());
            e.printStackTrace();
        }

        //Synchronize Data
        SyncUtils.CreateSyncAccount(this);
        SyncUtils.TriggerRefresh();


    }

    /*
        Override onDestroy method to stop location service if this application gets destroyed since this application is the only one that will be using the location service
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService();
    }

    /*
        AsyncTask mapLoader class to run old zone loading code in background thread to stop obstruction of UI thread in android
    */
    public class mapLoader extends AsyncTask<String, Integer, String> {

        Context context;
        boolean run;
        String email;
        String users;
        ArrayList<PointF> adminPoints;
        private GoogleMap mMap; // Might be null if Google Play services APK is not available.
        private Marker mMarker;
        private ArrayList<Marker> userPoints;
        private GroundOverlay mGroundOverlay;
        private IATask<IAFloorPlan> mFetchFloorPlanTask;
        private Target mLoadTarget;
        //private boolean mCameraPositionNeedsUpdating;
        private static final float HUE_IABLUE = 200.0f;
        IAFloorPlan floorPlan;
        Bitmap bitmap;
        LatLng coordinates;
        /* used to decide when bitmap should be downscaled */
        private static final int MAX_DIMENSION = 2048;
        MapView localMapView;
        boolean init;
        boolean downloading;
        boolean override;
        boolean retry;
        long wait;
        long startWait;

        private IAResourceManager mResourceManager;
        private String TAG = "mapLoader";

        public mapLoader( Context context, String email, MapView map ) {
            this.context = context;
            run = true;
            userPoints = new ArrayList<Marker>();
            this.email = email;
            localMapView = map;
            mMap = localMapView.getMap();
            init = true;
            downloading = false;
            override = false;
            retry = false;
            startWait = 200;
            wait = 200;
        }

        public void override(boolean override)
        {
            this.override = override;
        }
        public void setRunning(boolean run)
        {
            this.run = run;
        }
        public GoogleMap getMap()
        {
            return mMap;
        }
        public Marker getMarker()
        {
            return mMarker;
        }
        public void addMarker(MarkerOptions options)
        {
            mMarker = mMap.addMarker(options);
        }
        public void addUserMarker(MarkerOptions options)
        {
            userPoints.add(mMap.addMarker(options));
            Log.d("UserPoints", "The size is now " + userPoints.size());
        }
        public void moveMarker(LatLng latLng)
        {
            if(!mMarker.isVisible())
            {
                mMarker.setVisible(true);
            }
            mMarker.setPosition(latLng);
        }
        public void moveCamera(CameraUpdate update)
        {
            mMap.animateCamera(update);
        }
        @Override
        protected void onCancelled() {
            run = false;
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d("mapLoader", "We are running in the background");

            mResourceManager = IAResourceManager.create(context);
            publishProgress(0);

            while(run)
            {
                //Check if the service is even running, otherwise this async task is useless so stop it
                if(mService == null )
                {
                    Log.d(TAG, "The service is null");
                    publishProgress(7);
                    updateUserLocation(0, 0);
                    run = false;
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                else
                {
                    Log.d(TAG,"The service is not null the current mMap is "+mMap+" and the current floorPlan is "+floorPlan);
                    if(mMap != null && floorPlan != null)
                    {
                        retry = false;
                        Log.d(TAG,"Map is not null and neither is floorPlan");
                        //If this is the first run of maploader to load the rooms on current floor, wait until floorplan is downloaded and then initialize the rooms
                        //This code only runs once
                        if(init)
                        {
                            adminPoints = new ArrayList<PointF>();
                            initRooms();
                            if(isFacilityManager)
                            {
                                Log.d("Admin","We are an admin");
                                initAdminPoints();
                            }
                            publishProgress(10);
                            init = false;
                        }
                        coordinates = new LatLng(mService.getLatitude(),mService.getLongitude());
                        updateUserLocation((float) coordinates.latitude,(float) coordinates.longitude);
                        publishProgress(1);
                        getAdminPoints();
                        //Check if the marker that represents the user is null and make one if it's null through publishprogress, else don't make a new marker
                        if (mMarker == null) {
                            // first location, add marker
                            Log.d(TAG, "We are adding a new marker in onLocationChanged");
                            publishProgress(4);
                        } else {
                            // move existing markers position to received location
                            Log.d(TAG, "We are moving the marker in onLocationChanged");
                            publishProgress(5);
                        }

                        // our camera position needs updating if location has significantly changed
                        if (mService != null && mService.getCameraNeedUpdate()) {
                            Log.d(TAG, "We are repositioning new camera");
                            publishProgress(6);
                        }
                        //Check the rooms to see if they contain the current user
                        checkRooms(coordinates.latitude, coordinates.longitude);
                    }
                    else
                    {
                        retry = true;
                    }
                    //Check if we need to download the floorplan for the current location inside the building (like changing floors)
                    //Log.d(TAG,"What does dofetch say? "+mService.dofetch()+" what's downloading? "+downloading+" what about override? "+override);
                    //Disabled currently for showcase, enable this code to download floorplan from indoorAtlas website, otherwise leave it to use default google map rendering
                    if(mService != null && ((mService.dofetch() && !downloading) || override))
                    {
                        Log.d(TAG,"We are doing fetch");
                        publishProgress(3);
                        if(override)
                        {
                            override = false;
                        }
                    }
                }
                //Simple Thread.sleep to put background thread to sleep so we don't overload the phone with commands every millisecond which would cause battery loss
                try {
                    Thread.sleep(wait);
                    if(retry && wait < 5000)
                        wait*=2;
                    else
                        wait=startWait;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "We are out of the loop and finishing up the publishing");
            //Clear all user point information upon stopping location service
            userPoints.clear();



            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            //Reset user location upon stopping the location service
            Log.d("loadp","We are in onPostExecute");
            updateUserLocation(0, 0);
            mMap.clear();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Integer... condition) {
            Log.d("Background", "We are publishing background work");
            if(mService != null)
            {
                if(condition[0] == 0)
                {
                    LatLng coordinates = mService.getLatLng();
                    mMap.addMarker(new MarkerOptions().position(coordinates).visible(false));
                }
                else if(condition[0] == 1)
                {
                    Log.d(TAG,"We are publishing progress by moving camera");
                    Log.d(TAG, "The coordinates are "+coordinates.latitude+", "+coordinates.longitude);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 19));
                }
                else if(condition[0] == 2)
                {
                    Log.d(TAG,"We are doing the groundoverlay on top of the google map");
                    if (mGroundOverlay != null) {
                        mGroundOverlay.remove();
                    }
                    if (mMap != null) {
                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                        IALatLng iaLatLng = floorPlan.getCenter();
                        LatLng center = new LatLng(iaLatLng.latitude, iaLatLng.longitude);
                        GroundOverlayOptions fpOverlay = new GroundOverlayOptions()
                                .image(bitmapDescriptor)
                                .position(center, floorPlan.getWidthMeters(), floorPlan.getHeightMeters())
                                .bearing(floorPlan.getBearing());
                        mGroundOverlay = mMap.addGroundOverlay(fpOverlay);
                    }
                }
                else if(condition[0] == 3)
                {
                    //Log.d("fetch","What is the floorid? "+mService.getFloorID());
                    fetchFloorPlan(mService.getFloorID());
                    mService.setFetch(false);
                }
                else if(condition[0] == 4)
                {
                    addMarker(new MarkerOptions().position(coordinates).icon(BitmapDescriptorFactory.defaultMarker(HUE_IABLUE)));
                }
                else if(condition[0] == 5)
                {
                    moveMarker(coordinates);
                }
                else if(condition[0] == 6)
                {
                    moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 17.5f));
                    mService.setCameraUpdate(false);
                }
                else if(condition[0] == 7)
                {
                    //Log.d(TAG, "We are doing map stuff");
                    mMap.clear();
                }
                else if(condition[0] == 8)
                {
                    userLocations(users);
                }
                else if(condition[0] == 9)
                {
                    moveUserLocations(users);
                }
                else if(condition[0] == 10)
                {
                    //Log.d("Rooms","We are about to draw the rooms");
                    for(Room r:rooms)
                    {
                        //Log.d("Rooms","The current room's location is "+r.getLatitude()+", "+r.getLongitude());
                        Polygon poly = mMap.addPolygon(r.getPolyOptions());
                        r.setPoly(poly);
                    }
                }
                else if(condition[0] == 11)
                {
                    for(int i=0;i<rooms.size();i++)
                    {
                        rooms.get(i).occupied();
                    }
                }
            }
        }

        public void initRooms()
        {
            rooms.clear();
            try {
                //Retrives list of rooms available from selected floor plan
                ArrayList<NameValuePair> floorplan_post = new ArrayList<NameValuePair>(2);
                floorplan_post.add(new BasicNameValuePair("floorplan", String.valueOf(floorPlan.getId())));
                floorplan_post.add(new BasicNameValuePair("floor", String.valueOf(floorPlan.getFloorLevel())));
                String res = new ExternalDatabaseController((ArrayList<NameValuePair>) floorplan_post, GETROOMS_PHP).send();
                Log.d("Rooms",res);
                if(!res.contains("No rooms found"))
                {
                    Log.d("Rooms","We found rooms");
                    roomLocations(res);
                }
                else
                {
                    Log.d("Rooms","We didn't find rooms");
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void initAdminPoints()
        {
            try {
                //declare an arraylist that holds email and password
                ArrayList<NameValuePair> username_pass = new ArrayList<NameValuePair>(1);
                username_pass.add(new BasicNameValuePair("login_email", "getinfo"));
                users = new ExternalDatabaseController((ArrayList<NameValuePair>) username_pass, LOCATIONS_PHP).send();
                publishProgress(8);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void getAdminPoints()
        {
            try {
                //declare an arraylist that holds email and password
                ArrayList<NameValuePair> username_pass = new ArrayList<NameValuePair>(1);
                username_pass.add(new BasicNameValuePair("login_email", "getinfo"));
                users = new ExternalDatabaseController((ArrayList<NameValuePair>) username_pass, LOCATIONS_PHP).send();
                publishProgress(9);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //check all rooms to see if they are occupied
        public void checkRooms(double latitude, double longitude)
        {
            if(rooms != null && !rooms.isEmpty())
            {
                for(int i=0;i<rooms.size();i++)
                {
                    if(rooms.get(i).contains(new PointF((float) latitude,(float) longitude)) || checkUsersAgainstRoom(rooms.get(i)))
                    {
                        rooms.get(i).setOccupied(true);
                    }
                    else
                    {
                        rooms.get(i).setOccupied(false);
                    }
                }
                publishProgress(11);
            }
        }

        //Check all users logged into system against current room to check if it's occupied
        public boolean checkUsersAgainstRoom(Room room)
        {
            for(PointF p: adminPoints)
            {
                if(room.contains(new PointF(p.x,p.y)))
                {
                    return true;
                }
            }
            return false;
        }

        public void roomLocations(String response)
        {
            String roominfo[] = response.split("\\+");
            Log.d("Rooms", response);
            for(int i = 0; i < roominfo.length; i++)
            {
                Log.d("Rooms", "We are adding a room");
                String room[] = roominfo[i].split(":");
                Log.d("Debug",roominfo[i]);
                Room temp = new Room(room[1],Double.parseDouble(room[3]), Double.parseDouble(room[5]),Double.parseDouble(room[9]), Double.parseDouble(room[11].substring(0,room[11].length()-1)));
                rooms.add(temp);
            }
        }

        public void userLocations(String response)
        {
            ArrayList<Marker> users = new ArrayList<Marker>();
            String userinfo[] = response.split(":");
            Log.d("Admin",response);
            for(int i = 5; i < userinfo.length; i+=9)
            {
                if(Float.parseFloat(userinfo[i]) != 0 || Float.parseFloat(userinfo[i+2]) != 0)
                {
                    LatLng temp = new LatLng(Float.parseFloat(userinfo[i]),Float.parseFloat(userinfo[i+2]));
                    adminPoints.add(new PointF(Float.parseFloat(userinfo[i]),Float.parseFloat(userinfo[i+2])));
                    if(!temp.equals(new LatLng(0,0)) && !temp.equals(mService.getLatLng()))
                    {
                        Log.d("Admin","We are adding user");
                        addUserMarker(new MarkerOptions().position(temp).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(userinfo[i - 2]));
                    }
                }
            }
        }

        public void moveUserLocations(String response)
        {
            ArrayList<Marker> users = new ArrayList<Marker>();
            String userinfo[] = response.split(":");
            for(int i = 5; i < userinfo.length; i+=9)
            {
                if(Float.parseFloat(userinfo[i]) != 0 || Float.parseFloat(userinfo[i+2]) != 0)
                {
                    LatLng temp = new LatLng(Float.parseFloat(userinfo[i]),Float.parseFloat(userinfo[i+2]));
                    if(mMarker != null && !temp.equals(mMarker.getPosition()))
                    {
                        moveUser(temp, userinfo[i-2]);
                    }
                }
            }
        }

        private void moveUser(LatLng coordinates, String name)
        {
            Log.d("Admin","We are moving user "+name+" to location "+coordinates.latitude+" ,"+coordinates.longitude);
            if(userPoints != null && !userPoints.isEmpty())
            {
                for(Marker mark:userPoints)
                {
                    if(mark.getTitle().equals(name))
                    {
                        mark.setPosition(coordinates);
                        return;
                    }
                }
            }

        }

        protected void updateUserLocation(float x, float y)
        {
            List<NameValuePair> user_location_info = new ArrayList<>(3);
            String login_email = email;

            if(!login_email.isEmpty())
            {
                user_location_info.add(new BasicNameValuePair("login_email", String.valueOf(login_email)));
                user_location_info.add(new BasicNameValuePair("latitude", String.valueOf(x)));
                user_location_info.add(new BasicNameValuePair("longitude", String.valueOf(y)));
                Log.d("updateUserLocation","The pair we are sending is latitude = "+String.valueOf(x)+" with longitude = "+String.valueOf(y));

                String res = "";
                //Create a new Zone
                try {
                    res = new ExternalDatabaseController((ArrayList<NameValuePair>) user_location_info,UPDATEUSERLOCATION_PHP).send();

                    Log.i(LOG_TAG, "Insert DB Result: " + res);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(res!= null && res.equalsIgnoreCase("successful")){
                    Log.d("updateuserlocation","was successful, result was "+res);
                }
                else{
                    Log.d("updateuserlocation", res);
                }
            }
        }

        /*
        IndoorAtlas Helper methods
     */

        /**
         * Sets bitmap of floor plan as ground overlay on Google Maps
         */
        private void setupGroundOverlay(IAFloorPlan floorPlan, Bitmap bitmap) {
            this.floorPlan = floorPlan;
            this.bitmap = bitmap;
            this.publishProgress(2);
        }
        /**
         * Download floor plan using Picasso library.
         */
        private void fetchFloorPlanBitmap(final IAFloorPlan floorPlan) {

            final String url = floorPlan.getUrl();

            if (mLoadTarget == null) {
                mLoadTarget = new Target() {

                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Log.d(TAG, "onBitmap loaded with dimensions: " + bitmap.getWidth() + "x" + bitmap.getHeight());
                        setupGroundOverlay(floorPlan, bitmap);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        // N/A
                    }

                    @Override
                    public void onBitmapFailed(Drawable placeHolderDraweble) {
                        // Toast.makeText(MapsOverlayActivity.this, "Failed to load bitmap",Toast.LENGTH_SHORT).show();
                    }
                };
            }

            RequestCreator request = Picasso.with(context).load(url);

            final int bitmapWidth = floorPlan.getBitmapWidth();
            final int bitmapHeight = floorPlan.getBitmapHeight();

            if (bitmapHeight > MAX_DIMENSION) {
                request.resize(0, MAX_DIMENSION);
            } else if (bitmapWidth > MAX_DIMENSION) {
                request.resize(MAX_DIMENSION, 0);
            }
            request.into(mLoadTarget);
        }
        /**
         * Fetches floor plan data from IndoorAtlas server.
         */
        public void fetchFloorPlan(String id) {

            if(id!=null)
            {
                downloading = true;
                // if there is already running task, cancel it
                //cancelPendingNetworkCalls();

                Log.d("fetch","what is the id? "+id);
                final IATask<IAFloorPlan> task = mResourceManager.fetchFloorPlanWithId(id);

                task.setCallback(new IAResultCallback<IAFloorPlan>() {

                    @Override
                    public void onResult(IAResult<IAFloorPlan> result) {

                        if (result.isSuccess() && result.getResult() != null) {
                            // retrieve bitmap for this floor plan metadata
                            fetchFloorPlanBitmap(result.getResult());
                            downloading = false;
                        } else {
                            // ignore errors if this task was already canceled
                            if (!task.isCancelled()) {
                                // do something with error
                                //Toast.makeText(MapsOverlayActivity.this,"loading floor plan failed: " + result.getError(), Toast.LENGTH_LONG)
                                //      .show();
                                // remove current ground overlay
                                if (mGroundOverlay != null) {
                                    mGroundOverlay.remove();
                                    mGroundOverlay = null;
                                }
                            }
                            downloading = false;
                        }
                    }
                }, Looper.getMainLooper()); // deliver callbacks using main looper

                // keep reference to task so that it can be canceled if needed
                mFetchFloorPlanTask = task;
            }
        }

        /**
         * Helper method to cancel current task if any.
         */
        private void cancelPendingNetworkCalls() {
            if (mFetchFloorPlanTask != null && !mFetchFloorPlanTask.isCancelled()) {
                mFetchFloorPlanTask.cancel();
            }
        }
    }
    //END OF MAP LOADER CLASS ----------------------------------------------------------------------------------------------------------------------------------------
    /*
        AsyncTask zoneLoader class to run old zone loading code in background thread to stop obstruction of UI thread in android
     */
    private class zoneLoader extends AsyncTask<String, Void, String> {

        MyRewardListAdapter myrewards;
        ListView listView;
        ArrayList<RewardListParent> parents;
        Context context;
        public zoneLoader( Context context, MyRewardListAdapter myrewards, ListView listView ) {
            this.myrewards = myrewards;
            this.listView = listView;
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d("Background","We are running in the background");
            parents  = getZones();
            myrewards.setParents(parents);
            List<NameValuePair> userId = new ArrayList<>(1);
            String res = null;
            userId.add(new BasicNameValuePair("user_id", (user_id+"").trim()));

            //send the user_id to zonepost.php and get the zones
            try {
                res = new ExternalDatabaseController((ArrayList<NameValuePair>) userId, GETZONES_PHP).send();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(res != null) {
                System.out.println("what is res? "+res);
                JSONObject obj;
                JSONArray arr;
                if(!res.equals("No Regions Found")) {
                    try {
                        obj = new JSONObject(res);
                        arr = obj.getJSONArray("zone_obj");

                        for (int i = 0; i < arr.length(); i++) {
                            int region_id = arr.getJSONObject(i).getInt("region_id");
                            String region_name = arr.getJSONObject(i).getString("region_name");
                            if (!region_name.equalsIgnoreCase("null") && (data_access.getZone(region_id) == null))
                                data_access.createZones(region_name, region_id);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }


            Log.d("Background","We are about to publish progress");
            publishProgress();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.d("Background", "We are publishing background work");
            if(listView != null)
            {
                listView.setAdapter(myrewards);
            }
            gridViewButtons = (GridView) findViewById(R.id.grid_view_buttons);
            gridAdapter = new GridViewAdapter(context, data_access.getAllZoneNames(), data_access.getAllZoneID());
            gridAdapter.setMode(Attributes.Mode.Single);
            Log.d("MyZonesActivity", "What is gridAdapter? " + gridAdapter);
            if(gridViewButtons != null)
            {
                gridViewButtons.setAdapter(gridAdapter);
                gridViewButtons.setSelected(false);
                gridViewButtons.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.e("onItemLongClick", "onItemLongClick:" + position);
                        ((SwipeLayout) (gridViewButtons.getChildAt(position - gridViewButtons.getFirstVisiblePosition()))).open(true);
                        return false;
                    }
                });

                gridViewButtons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.e("onItemClick", "onItemClick:" + position);

                        Intent intent = new Intent(mContext, ZonesDescriptionActivity.class);

                        //send the region_id or button_id to the ZonesDescriptionActivity
                        Zones zone = (Zones) gridAdapter.getItem(position);
                        intent.putExtra("button_id", zone.getZone_id());
                        mContext.startActivity(intent);
                    }
                });

                gridViewButtons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Log.e("onItemSelected", "onItemSelected:" + position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {


                    }
                });
            }

            Log.d("Background", "We are finished with publishing");
        }
    }

    /**
     * Set the view for the zone activity
     */
    private void setTheContentViewContent()
    {
        User user = data_access.getUser(USER_LOGGEDIN); //Get me a User that is currently logged in the system

        //If a user that is logged in into the system is not found then start a new LoginActivity
        if(user == null)
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        //User that is currently logged in is found
        else
        {
            if(user.getUsertype().equalsIgnoreCase("admin")){  //Load Facility Manager Layout
                System.out.println("Loading Facility Manager view.");
                isFacilityManager = true;
                setContentView(R.layout.activity_admin_zones);
            }
            else{  //Load general user layout
                isFacilityManager = false;
                setContentView(R.layout.activity_my_zones);

                //Set the rewards list from the user's rewards

                //ArrayList<RewardListParent> parents = getZones();
                //System.out.println("The number of zones is currently: "+parents.size());
                //myRewardListAdapter.setParents(parents);
                //mListView.setAdapter(myRewardListAdapter);
            }

            mapView = (MapView)findViewById(R.id.map);
            mapView.onCreate(saved);
            mapView.getMapAsync(new OnMapReadyCallback() {

                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mapView.onResume();
                }
            });
            if(mapper!=null && !mapper.isCancelled())
            {
                Log.d("maploader","We are re running the mapper");
                mapper.setRunning(false);
                mapper = new mapLoader(context, email, mapView);
                mapper.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                mapper.override(true);
            }
            flContainer = (FrameLayout) findViewById(R.id.mainframe);
            //ivLayer1 = (LinearLayout) findViewById(R.id.zonegrid);
            imgLayer2 = ((MapView) findViewById(R.id.map));
            //(android.app.Fragment) findViewById(R.id.map);

            gesturedetector = new GestureDetector(getApplicationContext(),new MyGestureListener());

            flContainer.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gesturedetector.onTouchEvent(event);
                    return true;
                }
            });
            /*
                Added a new AsyncTask inner class to offload the network code to a background thread to stop further obstruction of UI thread in android
             */
            Log.d("Background","We are about to run the AsyncTask");
            user_id = user.getId(); //Get the ID of the user
            ListView mListView = (ListView) findViewById(R.id.list_view_userrewards);
            MyRewardListAdapter myRewardListAdapter = new MyRewardListAdapter(this);
            loader = new zoneLoader(this.getApplicationContext(),myRewardListAdapter,mListView);
            loader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    /**
     * Get rewards for each zone
     * @return ArrayList of zone rewards
     */
    public ArrayList<RewardListParent> getZones(){
        ArrayList<RewardListParent> parents = new ArrayList<>();

        List<NameValuePair> emptyarr = new ArrayList<>(1);
        String res=null;

        //send the user_id to zonepost.php and get the zones
        try {
            res = new ExternalDatabaseController((ArrayList<NameValuePair>) emptyarr,
                    "http://smartsystems-dev.cs.fiu.edu/getroomlightingwaste.php").send();

            Log.i(LOG_TAG, "Result: " + res);
            JSONObject obj =  new JSONObject(res);
            JSONObject myobj;
            int j=0;
            while (obj.has(j + "")&&(j<5)) {
                myobj = obj.getJSONObject(j + "");
                int id = myobj.getInt("zone_description_region_id");
                myobj.getDouble("lighting_waste_kw");

                if(data_access.getZone(id)!=null)
                {
                    Zones zones = data_access.getZone(id);
                    Log.i(LOG_TAG, "Get Zone: "+id);

                    RewardListParent parent = new RewardListParent();
                    parent.setName(rewardNames[j]+" Place");
                    parent.setDescription(lighting_award_descrp);
                    parent.setZone_name(zones.getZone_name());
                    parent.setPoints("+"+(1000-j*100));
                    parents.add(parent);
                }

                j++;
            }

            res = new ExternalDatabaseController((ArrayList<NameValuePair>) emptyarr,
                    "http://smartsystems-dev.cs.fiu.edu/getroomplugloadconsumption.php").send();

            obj =  new JSONObject(res);
            j=0;
            while (obj.has(j + "")&&(j<5)) {
                myobj = obj.getJSONObject(j + "");
                int id = myobj.getInt("zone_description_region_id");
                myobj.getDouble("appliance_time_plugged");

                if(data_access.getZone(id)!=null)
                {
                    Zones zones = data_access.getZone(id);
                    RewardListParent parent = new RewardListParent();
                    parent.setName(rewardNames[j]+" Place");
                    parent.setDescription(getResources().getString(R.string.plugload_award_descrp));
                    parent.setZone_name(zones.getZone_name());
                    parent.setPoints("+"+(1000-j*100));
                    parents.add(parent);
                }

                j++;
            }


        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }


        return parents;
    }


    /**
     *  Initialize Activity Action Bar Menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_zones, menu);
        this.settingMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_editusers);

        if (item != null) {
            item.setVisible (isFacilityManager);

        }

        return true;
    }

    /**
     *  On Menu Item Selected
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent intent;
        switch(item.getItemId()) {
            case R.id.action_logout:
                intent = new Intent(this,LoginActivity.class);

                //Clean the Activity Stack
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                //Logout the user from the system by declaring the
                data_access.userLogout(user_id);

                //when the user logs out, take him/her to the login activity
                startActivity(intent);
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_addzone:
                intent = new Intent(this,AddZoneActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_editusers:
                intent = new Intent(this,EditZoneUsersActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_location:
                if (item.isChecked()) {
                    item.setChecked(false);
                    stopService();
                } else {
                    item.setChecked(true);
                    //getLocationDialog();
                    startLocationService();
                }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //When an Activity is resumed, open the SQLite
    //connection
    @Override
    protected void onResume() {
        super.onResume();

        try {
            data_access.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mSyncStatusObserver.onStatusChanged(0);

        // Watch for sync state changes
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING |
                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);



        setTheContentViewContent();
        SharedPreferences prefs = this.getSharedPreferences("fiu.ssobec", Context.MODE_PRIVATE);//getPreferences(MODE_PRIVATE);
        boolean running = prefs.getBoolean("fiu.ssobec.running", false);
        if(settingMenu != null && running)
        {
            Log.d("Menu", "We are setting it to checked");
            MenuItem locationItem = settingMenu.findItem(R.id.action_location);
            locationItem.setChecked(true);
        }

    }

    //When an Activity is left, close the
    //SQLite connection
    @Override
    protected void onPause() {
        super.onPause();
        data_access.close();

        if (mSyncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
            mSyncObserverHandle = null;
        }
    }

    /**
     * Observe the synchronization status of the Sync Adapter class
     */
    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
        /** Callback invoked with the sync adapter status changes. */
        @Override
        public void onStatusChanged(int which) {
            // Create a handle to the account that was created by
            // SyncService.CreateSyncAccount(). This will be used to query the system to
            // see how the sync status has changed.
            final Account account = AuthenticatorService.GetAccount();
            if (account == null) {
                // GetAccount() returned an invalid value. This shouldn't happen, but
                // we'll set the status to "not refreshing".
                setRefreshActionButtonState(false);
                return;
            }
            runOnUiThread(new Runnable() {
                /**
                 * The SyncAdapter runs on a background thread. To update the UI, onStatusChanged()
                 * runs on the UI thread.
                 */
                @Override
                public void run() {

                    /*if (account == null) {
                        // GetAccount() returned an invalid value. This shouldn't happen, but
                        // we'll set the status to "not refreshing".
                        setRefreshActionButtonState(false);
                        return;
                    }*/
                    // Test the ContentResolver to see if the sync adapter is active or pending.
                    // Set the state of the refresh button accordingly.
                    boolean syncActive = ContentResolver.isSyncActive(
                            account, SyncConstants.AUTHORITY);
                    boolean syncPending = ContentResolver.isSyncPending(
                            account, SyncConstants.AUTHORITY);
                    setRefreshActionButtonState(syncActive || syncPending);
                }
            });
        }
    };

    /**
     * Set refresh button according to sync status
     * @param refreshing
     */
    public void setRefreshActionButtonState(boolean refreshing) {
        if (refreshing) {
            setContentView(R.layout.activity_loading);
        } else {
            setTheContentViewContent();
        }
    }

    //Created by Diana June 2015
    public void getMyRewards(View view){
        //This method is intended to allow a user to see his/her account reward points
        Intent intent = new Intent(this,MyRewardsActivity.class);
        startActivity(intent);
    }

    public void getReports(View view){
        //This method is intended to generate reports
        Intent intent = new Intent(this,GetReportsActivity.class);
        startActivity(intent);
    }

    public void createZone(View view){
        //This method is intended to allow a facility manager to create a zone
        Intent intent = new Intent(this,CreateZoneActivity.class);
        startActivity(intent);
    }

    public void getWastefulRegions(View view){
        //This method is intended to allow users to view wasteful regions
        Intent intent = new Intent(this,WastefulRegionsActivity.class);
        startActivity(intent);
    }

    public void uploadCurrentRoom(View view)
    {
        if(mService != null)
        {
            //lksfdglk
            roomDialog = new Dialog(MyZonesActivity.this, R.style.FullHeightDialog);
            roomDialog.setContentView(R.layout.room_dialog);
            roomDialog.setCancelable(true);

            TextView location = (TextView) roomDialog.findViewById(R.id.currentCoordinates);
            final double lat = mService.getLatitude();
            final double lng = mService.getLongitude();
            //final String floorString = currentFloor.substring(0, currentFloor.length() - 1);
            location.setText("Current location: "+lat+", "+lng);

            final updateDialogLocation asyncLoader = new updateDialogLocation(roomDialog);
            asyncLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            final Button updateButton = (Button) roomDialog.findViewById(R.id.room_dialog_button);
            final Button updateHeightButton = (Button) roomDialog.findViewById(R.id.update_height_button);
            final Button updateWidthButton = (Button) roomDialog.findViewById(R.id.update_width_button);
            updateHeightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  asyncLoader.setHeight(false);
                    Toast.makeText(getApplicationContext(), "Height has been set!", Toast.LENGTH_SHORT).show();
                }
            });
            updateWidthButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    asyncLoader.setWidth(false);
                    Toast.makeText(getApplicationContext(), "Width has been set!", Toast.LENGTH_SHORT).show();
                }
            });
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String roomNum = ((EditText) roomDialog.findViewById(R.id.roomInput)).getText().toString();
                    String shape = ((EditText) roomDialog.findViewById(R.id.shapeInput)).getText().toString();
                    //String width = ((EditText) roomDialog.findViewById(R.id.widthInput)).getText().toString();
                    //String height = ((EditText) roomDialog.findViewById(R.id.heightInput)).getText().toString();
                    if (!roomNum.isEmpty() && !shape.isEmpty() && !asyncLoader.isEmpty() && mService != null) {
                            List<NameValuePair> room_location_info = new ArrayList<>(8);

                            room_location_info.add(new BasicNameValuePair("room_number", String.valueOf(roomNum)));
                            room_location_info.add(new BasicNameValuePair("floorplan", String.valueOf(mService.getFloorID())));
                            room_location_info.add(new BasicNameValuePair("floor", String.valueOf(mService.getCurrentFooor())));
                            room_location_info.add(new BasicNameValuePair("x", String.valueOf(lat)));
                            room_location_info.add(new BasicNameValuePair("y", String.valueOf(lng)));
                            room_location_info.add(new BasicNameValuePair("shape", String.valueOf(shape)));
                            double width = Math.abs(lat - asyncLoader.getLat());
                            double height = Math.abs(lng - asyncLoader.getLng());
                            room_location_info.add(new BasicNameValuePair("width", String.valueOf(width)));
                            room_location_info.add(new BasicNameValuePair("height", String.valueOf(height)));
                            asyncLoader.setRun(false);
                            String res = "";
                            Log.d("Update","The update button is sending: "+roomNum+", "+currentFloorPlan+", "+mService.getFloorID()+", "+lat+", "+lng+", "+shape+", "+width+", "+height);
                            //Create a new Zone
                            try {
                                res = new ExternalDatabaseController((ArrayList<NameValuePair>) room_location_info, UPDATEROOMLOCATION_PHP).send();
                                Log.i(LOG_TAG, "Insert DB Result: " + res);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (res != null && res.equalsIgnoreCase("successful")) {
                                Log.d("updateroomlocation", "was successful, result was " + res);
                            } else {
                                Log.d("updateroomlocation", res);
                            }
                        roomDialog.dismiss();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "You don't have latitude or longitude selected!", Toast.LENGTH_LONG).show();
                    }

                }
            });
            //now that the dialog is set up, it's time to show it
            roomDialog.show();
        }

    }

    class updateDialogLocation extends AsyncTask<String, Integer, String> {
        private boolean run;
        private boolean width,height;
        private double lat;
        private double lng;
        private Dialog dialog;

        updateDialogLocation(Dialog dialog)
        {
            this.dialog = dialog;
            run = true;
            height = true;
            width = true;
        }

        public boolean isEmpty()
        {
            if(lat != 0 && lng != 0 && !width && !height)
            {
                return false;
            }
            return true;
        }
        public void setRun(boolean run)
        {
            this.run = run;
        }
        public void setWidth(boolean width)
        {
            this.width = width;
        }
        public void setHeight(boolean height)
        {
            this.height = height;
        }
        public double getLat()
        {
            return lat;
        }
        public double getLng()
        {
            return lng;
        }
        @Override
        protected String doInBackground(String... params) {
            while(run)
            {
                if(mService != null)
                {
                    if(width)
                    {
                        lat = mService.getLatitude();
                        publishProgress(0);
                    }
                    if(height)
                    {
                        lng = mService.getLongitude();
                        publishProgress(1);
                    }
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Integer... values) {
            TextView heightText = (TextView) roomDialog.findViewById(R.id.heightText);
            TextView widthText = (TextView) roomDialog.findViewById(R.id.widthText);
            if(values[0] == 0)
            {
                widthText.setText("Current width is "+lat);
            }
            else if(values[0] == 1)
            {
                heightText.setText("Current height is"+lng);
            }
        }
    }

    public void getRatingDialogs(View view){

        ratings = new int[2];
        rankDialog = new Dialog(MyZonesActivity.this, R.style.FullHeightDialog);
        rankDialog.setContentView(R.layout.rank_dialog);
        rankDialog.setCancelable(true);
        final RatingBar ratingBar = (RatingBar)rankDialog.findViewById(R.id.dialog_ratingbar);
        ratingBar.setRating(3);

        Dialogtext = (TextView) rankDialog.findViewById(R.id.rank_dialog_text1);
        Dialogtext.setText("Is the temperature too hot or too cold? (More stars means too hot, three stars means perfect)");


        final Button nextButton = (Button) rankDialog.findViewById(R.id.next);
        final Button updateButton = (Button) rankDialog.findViewById(R.id.rank_dialog_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratings[0] = ratingBar.getNumStars();
                Dialogtext.setText("Is the light level too high or too low? (More stars means too high, three stars means perfect)");
                ratingBar.setRating(3);
                nextButton.setVisibility(View.GONE);
                updateButton.setVisibility(View.VISIBLE);
                //rankDialog.dismiss();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratings[1] = ratingBar.getNumStars();
                ratingBar.setRating(3);
                nextButton.setVisibility(View.VISIBLE);
                updateButton.setVisibility(View.GONE);
                rankDialog.dismiss();
            }
        });
        //now that the dialog is set up, it's time to show it
        rankDialog.show();

    }



    /*public void getLocationDialog() {

        locationLoader load = new locationLoader(this.getApplicationContext());
        load.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }*/

    /*
        AsyncTask locationLoader class to retrieve buildings from database
    */
    /*private class locationLoader extends AsyncTask<String, Void, String> {

        Context context;
        ArrayList<String[]> buildingLocations;
        String[] dialogChoices;
        String[] apikeys;
        boolean loading;
        AlertDialog loadingScreen;
        AlertDialog DialogScreen;
        public locationLoader( Context context ) {
            this.context = context;
            apikeys = new String[3];
            loading = true;
        }

        @Override
        protected void onCancelled() {

        }


        @Override
        protected String doInBackground(String... params) {
            Log.d("locationLoader", "We are running in the background");

            List<NameValuePair> user_location_info = new ArrayList<>(1);
            String login_email = email;
            publishProgress();
            user_location_info.add(new BasicNameValuePair("login_email", String.valueOf(login_email)));
            String res = "";
            try {
                res = new ExternalDatabaseController((ArrayList<NameValuePair>) user_location_info,GETBUILDINGS_PHP).send();
                while(res == null)
                {
                    Log.d("locationLoader","We are in an infinite loop waiting for response from server");
                    Thread.sleep(100);
                    continue;
                }
                buildingLocations = buildingLocations(res);
                dialogChoices = new String[buildingLocations.size()];
                for(int i = 0; i < dialogChoices.length; i++)
                {
                    dialogChoices[i] = buildingLocations.get(i)[1] +", Floor "+ buildingLocations.get(i)[21];

                }
                loading = false;
                publishProgress();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Executed";
        }

        public ArrayList<String[]> buildingLocations(String response)
        {
            ArrayList<String[]> buildings = new ArrayList<String[]>();
            String buildinginfo[] = response.split("/+");
            for(int i = 0; i < buildinginfo.length; i++)
            {
                buildings.add(buildinginfo[i].split(":"));
            }

            return buildings;
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.d("Background", "We are publishing background work");
            if(loading)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyZonesActivity.this);
                builder.setTitle("We are loading locations in background, please wait...");
                builder.setCancelable(false);
                loadingScreen = builder.create();
                //The above line didn't show the dialog i added this line:
                loadingScreen.show();
            }
            else
            {
                if(loadingScreen != null)
                {
                    loadingScreen.dismiss();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MyZonesActivity.this);
                builder.setTitle("Pick a location");
                builder.setItems(dialogChoices, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Toast.makeText(getApplicationContext(), dialogChoices[item] + " was selected", Toast.LENGTH_SHORT).show();
                        String[] temp = dialogChoices[item].split(", Floor ");
                        currentFloorPlan = temp[0];
                        currentFloor = temp[1];
                        apikeys[0] = buildingLocations.get(item)[15];
                        apikeys[1] = buildingLocations.get(item)[17];
                        apikeys[2] = buildingLocations.get(item)[19];
                        startLocationService(item, apikeys);
                    }
                });
                DialogScreen = builder.create();
                //The above line didn't show the dialog i added this line:
                DialogScreen.show();
            }

            Log.d("Background", "We are finished with publishing");
        }
    }*/

    /*public void startLocationService(int index, String[] apikeys)
    {
        Intent location = new Intent(MyZonesActivity.this,IndoorAtlasLocationService.class);
        location.putExtra("apikeys",apikeys);
        bindService(location, myConnection, this.BIND_AUTO_CREATE);
        startService(location);
    }*/

    public void startLocationService()
    {
        Intent location = new Intent(MyZonesActivity.this,IndoorAtlasLocationService.class);
        bindService(location, myConnection, this.BIND_AUTO_CREATE);
        startService(location);
    }

    // Start the service
    /*public void bindService() {
        Intent intent = new Intent(MyZonesActivity.this, IndoorAtlasLocationService.class);
        bindService(intent, myConnection, this.BIND_AUTO_CREATE);
        if(IndoorAtlasLocationService.isRunning)
        {
            startService(intent);
        }
    }*/

    // Stop the service
    public void stopService() {
        //this.mapper.cancel(false);
        Intent intent = new Intent(this, IndoorAtlasLocationService.class);
        stopService(intent);
        unbindService();

    }

    public void unbindService()
    {
        if(mService != null)
        {
            unbindService(myConnection);
            mService = null;
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return gesturedetector.onTouchEvent(ev);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_MIN_DISTANCE = 20;

        private static final int SWIPE_MAX_OFF_PATH = 100;

        private static final int SWIPE_THRESHOLD_VELOCITY = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            float dX = e2.getX() - e1.getX();
            float dY = e1.getY() - e2.getY();
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            float height = (float)displaymetrics.heightPixels;
            float width = (float) displaymetrics.widthPixels;

            if (Math.abs(dY) < SWIPE_MAX_OFF_PATH && Math.abs(velocityX) >= SWIPE_THRESHOLD_VELOCITY && Math.abs(dX) >= SWIPE_MIN_DISTANCE && (e1.getY() > (height-(height/4)))) {

                if (dX > 0) {

                    Toast.makeText(getApplicationContext(), "Right Swipe", Toast.LENGTH_SHORT).show();
                    //Now Set your animation
                    if(imgLayer2.getVisibility()==View.GONE)
                    {
                        Animation slideInAnimation = AnimationUtils.loadAnimation(MyZonesActivity.this, R.anim.slide_right_in);
                        imgLayer2.startAnimation(slideInAnimation);
                        imgLayer2.setVisibility(View.VISIBLE);

                    }
                } else {

                    Toast.makeText(getApplicationContext(), "Left Swipe",Toast.LENGTH_SHORT).show();
                    if(imgLayer2.getVisibility()==View.VISIBLE)
                    {
                        Animation slideInAnimation = AnimationUtils.loadAnimation(MyZonesActivity.this, R.anim.slide_left_out);
                        imgLayer2.startAnimation(slideInAnimation);
                        imgLayer2.setVisibility(View.GONE);
                    }

                }
                return true;
            } else if (Math.abs(dX) < SWIPE_MAX_OFF_PATH && Math.abs(velocityY) >= SWIPE_THRESHOLD_VELOCITY && Math.abs(dY) >= SWIPE_MIN_DISTANCE) {
                if (dY > 0) {
                    //Toast.makeText(getApplicationContext(), "Up Swipe",Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getApplicationContext(), "Down Swipe",Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        }
    }
}

