package fiu.ssobec.Activity;

import android.accounts.Account;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SyncStatusObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import fiu.ssobec.Services.IndoorLocationService;
import fiu.ssobec.Services.Util;
import fiu.ssobec.Synchronization.DataSync.AuthenticatorService;
import fiu.ssobec.Synchronization.SyncConstants;
import fiu.ssobec.Synchronization.SyncUtils;



/*
    MyZonesActivity is our Main and Launcher Activity
    Our application will only need that the user log in once.
    A column in the UserSQLiteDatabase 'loggedIn'
* */
public class MyZonesActivity extends ActionBarActivity{

    public static final int USER_LOGGEDIN = 1;
    public static final String LOG_TAG = "MyZonesActivity";
    public static final String GETZONES_PHP = "http://smartsystems-dev.cs.fiu.edu/zonepost.php";
    public static final String UPDATEUSERLOCATION_PHP = "http://smartsystems-dev.cs.fiu.edu/updateuserlocation.php";
    public static final String UPDATEROOMLOCATION_PHP = "http://smartsystems-dev.cs.fiu.edu/updateroomlocation.php";
    public static final String LOCATIONS_PHP = "http://smartsystems-dev.cs.fiu.edu/getUserLocations.php";
    public static final String GETROOMS_PHP = "http://smartsystems-dev.cs.fiu.edu/getfloorrooms.php";
    public static final String GETBUILDINGS_PHP = "http://smartsystems-dev.cs.fiu.edu/getbuildings.php";
    public static final String plugload_award_descrp="Reward for little consumption of energy in plugload";
    public static final String lighting_award_descrp="Reward for turning off the lights before leaving the room";
    zoneLoader loader;
    private static DataAccessUser data_access;
    private final Handler handler = new Handler();

    private IndoorLocationService mService;
    private boolean isBound;
    private Context context;
    private mapLoader mapper;
    String currentFloorPlan;
    String currentFloor;

    ArrayList<Room> rooms = new ArrayList<Room>();
    private ServiceConnection myConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,IBinder service) {
            Log.d("Service","We are re binded");
            IndoorLocationService.LocalBinder binder = (IndoorLocationService.LocalBinder) service;
            mService = binder.getService();
            isBound = true;
            mapper = new mapLoader(context, email);
            mapper.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
            mService = null;
            mapper.cancel(true);
        }
    };

    private GestureDetector gesturedetector = null;
    private FrameLayout flContainer;
    //private LinearLayout ivLayer1;
    private ImageView imgLayer2;

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
    //ArrayList<String> locations = new ArrayList<String>();

    private String [] rewardNames = {"First", "Second", "Third", "Fourth", "Fifth"};

    private boolean isFacilityManager = false;

    //////////////////////////////////
    private ListView mListView;
    private Context mContext = this;

    private GridView gridViewButtons;
    private GridViewAdapter gridAdapter;

    /**
     *  Initialize Activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        //setContentView(R.layout.activity_loading);

        //Declare the access to the SQLite table for user
//        data_access = DataAccessUser.getInstance(this);
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
        /*Intent stopService = new Intent(this, IndoorLocationService.class);
        stopService(stopService);*/
    }

    /*
        AsyncTask mapLoader class to run old zone loading code in background thread to stop obstruction of UI thread in android
    */
    private class mapLoader extends AsyncTask<String, Void, String> {

        Context context;
        Bitmap map;
        boolean run;
        boolean refreshmap;
        PointF scaledPoint;
        String email;

        ArrayList<PointF> adminPoints;
        public mapLoader( Context context, String email ) {
            this.context = context;
            run = true;
            refreshmap = true;
            scaledPoint = new PointF();
            this.email = email;
        }
        public void setRefresh(boolean refresh)
        {
            refreshmap = refresh;
        }

        @Override
        protected void onCancelled() {
            run = false;
            updateUserLocation(0,0);
        }

        public void roomLocations(String response)
        {
            String roominfo[] = response.split("/+");
            //"roomNumber:" . $row["room_number"]. ":x:" . $row["x"]. ":y:" . $row["y"]. ":shape:" .$row["shape"]. ":width:" .$row["width"]. ":height:" .$row["height"]. "";
            for(int i = 0; i < roominfo.length; i++)
            {
                String room[] = roominfo[i].split(":");
                rooms.add(new Room(room[1],Double.parseDouble(room[3]), Double.parseDouble(room[5]),Double.parseDouble(room[7]), Double.parseDouble(room[9]) ));
            }
        }

        public ArrayList<PointF> userLocations(String response)
        {
            ArrayList<PointF> users = new ArrayList<PointF>();
            String userinfo[] = response.split(":");
            for(int i = 5; i < userinfo.length; i+=9)
            {
                if(Float.parseFloat(userinfo[i]) != 0 || Float.parseFloat(userinfo[i+2]) != 0)
                {
                    users.add(new PointF(Float.parseFloat(userinfo[i]),Float.parseFloat(userinfo[i+2])));
                }

            }

            return users;
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d("mapLoader", "We are running in the background");

            rooms.clear();
            try {
                //declare an arraylist that holds email and password
                ArrayList<NameValuePair> floorplan_post = new ArrayList<NameValuePair>(2);
                floorplan_post.add(new BasicNameValuePair("floorplan", String.valueOf(currentFloorPlan)));
                floorplan_post.add(new BasicNameValuePair("floor", String.valueOf(currentFloor)));
                String res = new ExternalDatabaseController((ArrayList<NameValuePair>) floorplan_post, GETROOMS_PHP).send();
                if(!res.contains("No rooms found"))
                {
                    roomLocations(res);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while(run)
            {
                if(mService != null && mService.getMap() != null)
                {
                    this.map = mService.getMap();
                    if(isFacilityManager)
                    {
                        try {
                            //declare an arraylist that holds email and password
                            ArrayList<NameValuePair> username_pass = new ArrayList<NameValuePair>(1);
                            username_pass.add(new BasicNameValuePair("login_email", "getinfo"));
                            String res = new ExternalDatabaseController((ArrayList<NameValuePair>) username_pass, LOCATIONS_PHP).send();
                            adminPoints = userLocations(res);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    updateUserLocation((float)mService.getPointX(),(float)mService.getPointY());
                    Log.d("mapLoader", "We are about to publish progress");
                    publishProgress();
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            adminPoints.clear();
            return "Executed";
        }

        protected void updateUserLocation(float x, float y)
        {
            List<NameValuePair> user_location_info = new ArrayList<>(3);

            //int zone_id = zone.getZone_id();

            String login_email = email;


            if(!login_email.isEmpty())
            {

                user_location_info.add(new BasicNameValuePair("login_email", String.valueOf(login_email)));
                user_location_info.add(new BasicNameValuePair("latitude", String.valueOf(x)));
                user_location_info.add(new BasicNameValuePair("longitude", String.valueOf(y)));
                Log.d("updateUserLocation","The pair we are sending is latitude = "+String.valueOf(x)+" with longitude = "+String.valueOf(y));
                //new_zone_info.add(new BasicNameValuePair("region_id", String.valueOf(zone_id)));

                String res = "";
                //Create a new Zone
                try {
                    res = new ExternalDatabaseController((ArrayList<NameValuePair>) user_location_info,UPDATEUSERLOCATION_PHP).send();

                    Log.i(LOG_TAG, "Insert DB Result: " + res);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(res.equalsIgnoreCase("successful")){
                    Log.d("updateuserlocation","was successful, result was "+res);
                }
                else{
                    Log.d("updateuserlocation",res);
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.d("Background", "We are publishing background work");
            ImageView mapview = (ImageView) findViewById(R.id.mapview);
            if(mapview != null)
            {
                mapview.setImageBitmap(map);
                mapview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                if(mService!=null)
                {
                    Util.calculateScaledPoint(mService.getFloorplanX(), mService.getFloorplanY(), (int) mService.getPointX(), (int) mService.getPointY(), mapview, scaledPoint);
                    if(isFacilityManager && adminPoints!= null && !adminPoints.isEmpty())
                    {
                        for(PointF p: adminPoints)
                        {
                            Util.calculateScaledPoint(mService.getFloorplanX(), mService.getFloorplanY(), (int) p.x, (int) p.y, mapview, p);
                        }
                    }
                    if(rooms != null && !rooms.isEmpty())
                    {
                        for(int i=0;i<rooms.size();i++)
                        {
                            PointF temp = null;
                            Util.calculateScaledPoint(mService.getFloorplanX(), mService.getFloorplanY(), rooms.get(i).getX(), rooms.get(i).getY(), mapview, temp);
                            if(temp != null)
                            {
                                rooms.get(i).rescale(temp);
                            }
                        }
                    }

                    mapview.buildDrawingCache();
                    Bitmap bitmap = mapview.getDrawingCache();
                    if(bitmap != null)
                    {
                        final ImageView imageFloor = (ImageView) findViewById(R.id.mapview);
                        final Bitmap bitmapCircle = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
                        Canvas canvas = new Canvas(bitmapCircle);
                        Paint paint = new Paint();
                        paint.setAntiAlias(true);
                        paint.setColor(Color.GREEN);
                        paint.setStrokeWidth(5 + (int) mService.getUncertainty());
                        canvas.drawBitmap(bitmap, new Matrix(), null);
                        if(adminPoints != null && !adminPoints.isEmpty())
                        {
                            for(PointF p:adminPoints)
                            {
                                canvas.drawCircle(p.x,p.y,7,paint);
                            }
                        }
                        paint.setColor(Color.BLUE);
                        canvas.drawCircle(scaledPoint.x, scaledPoint.y, 10, paint);
                        if(rooms!= null && !rooms.isEmpty())
                        {
                            paint.setColor(Color.RED);
                            paint.setStrokeWidth(10);
                            paint.setStyle(Paint.Style.STROKE);
                            for(int i=0;i<rooms.size();i++)
                            {
                                canvas.drawRect(rooms.get(i).getRoom(),paint);
                            }
                        }
                        imageFloor.setImageBitmap(bitmapCircle);
                    }
                }
            }



            //Toast.makeText(getApplicationContext(), "Map has been updated!",Toast.LENGTH_SHORT).show();
            Log.d("Background", "We are finished with publishing");

        }
    }

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
            //Set buttons in a Grid View order
            //do{
            gridViewButtons = (GridView) findViewById(R.id.grid_view_buttons);
                //Log.d("onProgress","We are not progressing");
            //}
            //while(gridViewButtons==null);
//            ButtonAdapter m_badapter = new ButtonAdapter(this);
//            m_badapter.setListData(data_access.getAllZoneNames(), data_access.getAllZoneID());
//            gridViewButtons.setAdapter(m_badapter);
            gridAdapter = new GridViewAdapter(context, data_access.getAllZoneNames(), data_access.getAllZoneID());
//            adapter.setListData(data_access.getAllZoneNames(), data_access.getAllZoneID());
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
            flContainer = (FrameLayout) findViewById(R.id.mainframe);
            //ivLayer1 = (LinearLayout) findViewById(R.id.zonegrid);
            imgLayer2 = (ImageView) findViewById(R.id.mapview);

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
            loader = new zoneLoader(this,myRewardListAdapter,mListView);
            loader.execute();

            /*if((loader == null) || (loader != null && loader.getStatus() == AsyncTask.Status.FINISHED) )
            {
                Log.d("Background","We are initializing");
                loader = new zoneLoader(this,myRewardListAdapter,mListView);
            }
             if(loader.getStatus() != AsyncTask.Status.RUNNING && loader.getStatus() == AsyncTask.Status.FINISHED)
            {
                Log.d("Background","We are now executing");
                loader.execute();
            }*/
            /*else
            {
                Log.d("Background","We are skipping execution and publishing");
                loader.onProgressUpdate();
            }*/


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
                if (item.isChecked() == true) {
                    item.setChecked(false);
                    stopService();
                    //stopService(new Intent(this, IndoorLocationService.class));
                } else {
                    item.setChecked(true);
                    getLocationDialog();

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
        if(mapper!= null)
        {
            mapper.setRefresh(true);
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
            roomDialog = new Dialog(MyZonesActivity.this, R.style.FullHeightDialog);
            roomDialog.setContentView(R.layout.room_dialog);
            roomDialog.setCancelable(true);

            TextView location = (TextView) roomDialog.findViewById(R.id.currentCoordinates);
            location.setText("Current location: "+mService.getPointX()+", "+mService.getPointY());
            final Button updateButton = (Button) roomDialog.findViewById(R.id.room_dialog_button);

            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String roomNum = ((EditText) roomDialog.findViewById(R.id.roomInput)).getText().toString();
                    String shape = ((EditText) roomDialog.findViewById(R.id.shapeInput)).getText().toString();
                    String width = ((EditText) roomDialog.findViewById(R.id.widthInput)).getText().toString();
                    String height = ((EditText) roomDialog.findViewById(R.id.heightInput)).getText().toString();
                    if (!roomNum.isEmpty() && !shape.isEmpty() && !width.isEmpty() && !height.isEmpty()) {
                        if (isNumeric(width) && isNumeric(height)) {
                            List<NameValuePair> room_location_info = new ArrayList<>(8);

                            room_location_info.add(new BasicNameValuePair("room_number", String.valueOf(roomNum)));
                            room_location_info.add(new BasicNameValuePair("floorplan", String.valueOf(currentFloorPlan)));
                            room_location_info.add(new BasicNameValuePair("floor", String.valueOf(currentFloor)));
                            room_location_info.add(new BasicNameValuePair("x", String.valueOf(mService.getPointX())));
                            room_location_info.add(new BasicNameValuePair("y", String.valueOf(mService.getPointY())));
                            room_location_info.add(new BasicNameValuePair("shape", String.valueOf(shape)));
                            room_location_info.add(new BasicNameValuePair("width", String.valueOf(width)));
                            room_location_info.add(new BasicNameValuePair("height", String.valueOf(height)));

                            String res = "";
                            //Create a new Zone
                            try {
                                res = new ExternalDatabaseController((ArrayList<NameValuePair>) room_location_info, UPDATEROOMLOCATION_PHP).send();

                                Log.i(LOG_TAG, "Insert DB Result: " + res);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (res.equalsIgnoreCase("successful")) {
                                Log.d("updateroomlocation", "was successful, result was " + res);
                            } else {
                                Log.d("updateroomlocation", res);
                            }
                        }
                    }
                    roomDialog.dismiss();
                }
            });
            //now that the dialog is set up, it's time to show it
            roomDialog.show();
        }

    }

    public boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
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

    public void getLocationDialog() {

        locationLoader load = new locationLoader(this);
        load.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    /*
        AsyncTask locationLoader class to retrieve buildings from database
    */
    private class locationLoader extends AsyncTask<String, Void, String> {

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
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("We are loading locations in background, please wait...");
                builder.setCancelable(false);
                loadingScreen = builder.create();
                //The above line didn't show the dialog i added this line:
                loadingScreen.show();
            }
            else
            {
                loadingScreen.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
    }

    public void startLocationService(int index, String[] apikeys)
    {
        //String[] keys = new String[3];
        /*int identify = getResources().getIdentifier("venu_id_"+index, "string", getPackageName());
        keys[0] = getString(identify);
        identify = getResources().getIdentifier("floor_id_"+index, "string", getPackageName());
        keys[1] = getString(identify);
        identify = getResources().getIdentifier("floorplan_id_"+index, "string", getPackageName());
        keys[2] = getString(identify);*/
        //keys[0] = ;
        //keys[1] = "a55c45c4-07ed-48f5-a3e9-aa3c3f10db85";
        //keys[2] = "b4195361-c401-4147-be70-e040efaf8a0c";
        Intent location = new Intent(this,IndoorLocationService.class);
        location.putExtra("apikeys",apikeys);
        bindService(location, myConnection, this.BIND_AUTO_CREATE);
        startService(location);
    }

    // Start the service
    public void bindService() {
        Intent intent = new Intent(this, IndoorLocationService.class);
        bindService(intent, myConnection, this.BIND_AUTO_CREATE);
        if(IndoorLocationService.isRunning)
        {
            startService(intent);
        }

    }

    // Stop the service
    public void stopService() {
        Intent intent = new Intent(this, IndoorLocationService.class);
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
            if (Math.abs(dY) < SWIPE_MAX_OFF_PATH && Math.abs(velocityX) >= SWIPE_THRESHOLD_VELOCITY && Math.abs(dX) >= SWIPE_MIN_DISTANCE) {

                if (dX > 0) {

                    Toast.makeText(getApplicationContext(), "Right Swipe", Toast.LENGTH_SHORT).show();
                    //Now Set your animation

                    if(imgLayer2.getVisibility()==View.GONE)
                    {
                        Animation fadeInAnimation = AnimationUtils.loadAnimation(MyZonesActivity.this, R.anim.slide_right_in);
                        imgLayer2.startAnimation(fadeInAnimation);
                        imgLayer2.setVisibility(View.VISIBLE);
                    }
                } else {

                    Toast.makeText(getApplicationContext(), "Left Swipe",Toast.LENGTH_SHORT).show();

                    if(imgLayer2.getVisibility()==View.VISIBLE)
                    {
                        Animation fadeInAnimation = AnimationUtils.loadAnimation(MyZonesActivity.this, R.anim.slide_left_out);
                        imgLayer2.startAnimation(fadeInAnimation);
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

