package fiu.ssobec.Activity;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;

import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.R;
import fiu.ssobec.Synchronization.DataSync.AuthenticatorService;
import fiu.ssobec.Synchronization.SyncConstants;
import fiu.ssobec.Synchronization.SyncUtils;

public class EnergyActivity extends ActionBarActivity {


    private DataAccessUser data_access;
    TextView mTextView;
    TextView mTextView1;
    TextView mTextView2;
    TextView time_stamp_text;
    private Menu mOptionsMenu;
    private Object mSyncObserverHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data_access = new DataAccessUser(this);

        try {
            data_access.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        System.out.println("Activity Intent: "+intent.toString());

        //Get the title of the activity
        String app_title = getIntent().getStringExtra(ZonesDescriptionActivity.ACTIVITY_NAME);

        this.setTitle(app_title);

        setContentView(R.layout.activity_energy);
        mTextView = (TextView) findViewById(R.id.CurrOccupValue);
        time_stamp_text = (TextView) findViewById(R.id.time_stamp_view);

        switch (app_title)
        {
            case "Temperature":
                setContentView(R.layout.activity_temperature);
                mTextView1 = (TextView) findViewById( R.id.Fahrenheit);
                mTextView2 = (TextView) findViewById( R.id.Celsius);

                getTemperature(); break;

            case "Occupancy":
                setContentView(R.layout.activity_occupancy);
                getOccupancy();
                break;

            case "PlugLoad":
                setContentView(R.layout.activity_plugload);
                getPlugLoad();
                break;

            case "Lighting": getLighting(); break;
        }

    }

    // Convert to Celsius
    private int convertFahrenheitToCelsius(float fahrenheit) {
        return (int) ((fahrenheit - 32) * 5 / 9);
    }

    private void getOccupancy()
    {
        ((TextView) findViewById(R.id.CurrOccupValue)).setText("2");
        ((TextView) findViewById(R.id.AvgOccupValue)).setText("4");
        /*
        System.out.println("Get occupancy from region_id: "+ZonesDescriptionActivity.regionID);

        int zone_id = ZonesDescriptionActivity.regionID;

        ArrayList<String> info = data_access.getLatestOccupancy(zone_id);

        if(info == null)
        {
            mTextView.setText("No Data");
        }
        else
        {
            mTextView.setText("Current Occupancy: "+info.get(1));
            time_stamp_text.setText("Time:"+info.get(0));
        }*/
    }

    private void getTemperature()
    {
        int zone_id = ZonesDescriptionActivity.regionID;

        ArrayList<String> info = data_access.getLatestTemperature(zone_id);

        if(info == null)
        {
            mTextView1.setText("No Data");
        }
        else
        {
            TextView time_stamp = (TextView) findViewById(R.id.temperatureTimeStamp);

            ((TextView) findViewById(R.id.Fahrenheit)).setText(info.get(1)+""+(char) 0x00B0+"F");
            ((TextView) findViewById(R.id.Celsius)).setText(convertFahrenheitToCelsius(Float.parseFloat(info.get(1)))+""+(char) 0x00B0+"C");

            String temp;

            if((temp = data_access.getOutsideTemperature()) != "No Data")
            {
                ((TextView) findViewById(R.id.max_outside_temp_f)).setText(temp+(char) 0x00B0+"F");
                ((TextView) findViewById(R.id.max_outside_temp_c)).setText(convertFahrenheitToCelsius(Float.parseFloat(temp))
                        +""+(char) 0x00B0+"C");
            }

            time_stamp.setText(info.get(0)); //set time stamp
        }


    }

    private void getPlugLoad()
    {
        ((TextView) findViewById(R.id.CurrPlugValue)).setText("2");
       /* System.out.println("Get plug load from region_id: "+ZonesDescriptionActivity.regionID);

        int zone_id = ZonesDescriptionActivity.regionID;

        ArrayList<String> info = data_access.getLatestPlugLoad(zone_id);

        if(info == null)
        {
            mTextView.setText("No Data");
        }
        else
        {
            mTextView.setText("Current PlugLoad: "+info.get(1));
            time_stamp_text.setText("Time:"+info.get(0));
        }*/
    }

    private void getLighting()
    {
        System.out.println("Get occupancy from region_id: "+ZonesDescriptionActivity.regionID);

        int zone_id = ZonesDescriptionActivity.regionID;

        ArrayList<String> info = data_access.getLatestLighting(zone_id);

        if(info == null)
        {
            mTextView.setText("No Data");
        }
        else
        {
            mTextView.setText("The Lighting in the room is: "+info.get(1)
                                +"\nCloud Percentage: "+data_access.getCloudPercentage()+"%");
            time_stamp_text.setText("Time:"+info.get(0));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_energy, menu);
        mOptionsMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_logout:
                Intent intent = new Intent(this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
                data_access.userLogout(MyZonesActivity.user_id);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                return true;

            case R.id.menu_refresh:
                System.out.println("Refresh!");
                SyncUtils.TriggerRefresh();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
    }

    @Override
    protected void onPause() {
        super.onPause();
        data_access.close();

        if (mSyncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
            mSyncObserverHandle = null;
        }
    }

    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
        /** Callback invoked with the sync adapter status changes. */
        @Override
        public void onStatusChanged(int which) {
            runOnUiThread(new Runnable() {
                /**
                 * The SyncAdapter runs on a background thread. To update the UI, onStatusChanged()
                 * runs on the UI thread.
                 */
                @Override
                public void run() {
                    // Create a handle to the account that was created by
                    // SyncService.CreateSyncAccount(). This will be used to query the system to
                    // see how the sync status has changed.
                    Account account = AuthenticatorService.GetAccount();
                    if (account == null) {
                        // GetAccount() returned an invalid value. This shouldn't happen, but
                        // we'll set the status to "not refreshing".
                        setRefreshActionButtonState(false);
                        return;
                    }
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

    public void setRefreshActionButtonState(boolean refreshing) {
        if (mOptionsMenu == null) {
            return;
        }

        final MenuItem refreshItem = mOptionsMenu.findItem(R.id.menu_refresh);
        if (refreshItem != null) {
            if (refreshing) {
                refreshItem.setActionView(R.layout.actionbar_syncprogress);
            } else {
                refreshItem.setActionView(null);
            }
        }
    }

    public void predictConsumption (View view)
    {
        Intent intent = new Intent(this,ConsumptionAppliances.class);
        Log.i("EnergyActivity", "Starting my new prediction table");
        startActivity(intent);
    }
}
