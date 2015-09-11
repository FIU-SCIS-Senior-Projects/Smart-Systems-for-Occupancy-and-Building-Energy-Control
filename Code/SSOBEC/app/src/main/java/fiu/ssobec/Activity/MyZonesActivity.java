package fiu.ssobec.Activity;

import android.accounts.Account;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
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

import fiu.ssobec.Adapters.ArraySwipeAdapterSample;
import fiu.ssobec.Adapters.ButtonAdapter;
import fiu.ssobec.Adapters.GridViewAdapter;
import fiu.ssobec.Adapters.ListViewAdapter;
import fiu.ssobec.Adapters.MyRewardListAdapter;
import fiu.ssobec.AdaptersUtil.RewardListParent;
import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.DataAccess.ExternalDatabaseController;
import fiu.ssobec.Model.User;
import fiu.ssobec.Model.Zones;
import fiu.ssobec.R;
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
    public static final String plugload_award_descrp="Reward for little consumption of energy in plugload";
    public static final String lighting_award_descrp="Reward for turning off the lights before leaving the room";
    zoneLoader loader;
    private static DataAccessUser data_access;

    private Object mSyncObserverHandle;
    public static int user_id;
    //Dialog variables to get user rating on zone conditions
    Dialog rankDialog;
    int [] ratings;
    TextView Dialogtext;

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

        //setContentView(R.layout.activity_loading);

        //Declare the access to the SQLite table for user
//        data_access = DataAccessUser.getInstance(this);
        data_access = new DataAccessUser(this);

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
            Log.d("Background","We are publishing background work");
            listView.setAdapter(myrewards);
            //Set buttons in a Grid View order
            do{
                gridViewButtons = (GridView) findViewById(R.id.grid_view_buttons);
                Log.d("onProgress","We are not progressing");
            }
            while(gridViewButtons==null);
//            ButtonAdapter m_badapter = new ButtonAdapter(this);
//            m_badapter.setListData(data_access.getAllZoneNames(), data_access.getAllZoneID());
//            gridViewButtons.setAdapter(m_badapter);
            gridAdapter = new GridViewAdapter(context, data_access.getAllZoneNames(), data_access.getAllZoneID());
//            adapter.setListData(data_access.getAllZoneNames(), data_access.getAllZoneID());
            gridAdapter.setMode(Attributes.Mode.Single);
            Log.d("MyZonesActivity", "What is gridAdapter? " + gridAdapter);
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
                setContentView(R.layout.activity_my_zones);

                //Set the rewards list from the user's rewards

                //ArrayList<RewardListParent> parents = getZones();
                //System.out.println("The number of zones is currently: "+parents.size());
                //myRewardListAdapter.setParents(parents);
                //mListView.setAdapter(myRewardListAdapter);
            }
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
                    parent.setDescription(plugload_award_descrp);
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

}

