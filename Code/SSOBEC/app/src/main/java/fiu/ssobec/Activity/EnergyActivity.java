package fiu.ssobec.Activity;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncStatusObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.SQLException;
import java.util.ArrayList;

import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.MyPlugLoadListAdapter;
import fiu.ssobec.PlugLoadListParent;
import fiu.ssobec.R;
import fiu.ssobec.Synchronization.DataSync.AuthenticatorService;
import fiu.ssobec.Synchronization.SyncConstants;
import fiu.ssobec.Synchronization.SyncUtils;

public class EnergyActivity extends ActionBarActivity {


    private DataAccessUser data_access;
    TextView mTextView1;
    TextView mTextView2;
    TextView time_stamp_text;
    private Menu mOptionsMenu;
    private Object mSyncObserverHandle;
    String app_title = "Energy View";

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String title = "activity_title";
    SharedPreferences sharedpreferences;

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

        sharedpreferences = getSharedPreferences(MyPREFERENCES, this.MODE_PRIVATE);

        //Get the title of the activity
        if(intent.getExtras() != null)
        {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(title, getIntent().getStringExtra(ZonesDescriptionActivity.ACTIVITY_NAME));
            editor.commit();
        }

        if (sharedpreferences.contains(title))
        {
            app_title = sharedpreferences.getString(title, "");
        }

        this.setTitle(app_title);

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

            case "Lighting":
                setContentView(R.layout.activity_lighting);
                getLighting();
                break;
        }

    }

    // Convert to Celsius
    private int convertFahrenheitToCelsius(float fahrenheit) {
        return (int) ((fahrenheit - 32) * 5 / 9);
    }

    private void getOccupancy()
    {

        GraphView graph = (GraphView) findViewById(R.id.occupancy_graph);

        ArrayList<String> dates = data_access.getLastFewHoursofOccupancyDates(ZonesDescriptionActivity.regionID);
        ArrayList<Integer> occ_vals = data_access.getLastFewHoursofOccupancy(ZonesDescriptionActivity.regionID);

        //((TextView) findViewById(R.id.CurrOccupValue)).setText(occ_vals.get(occ_vals.size()-1));
        ((TextView) findViewById(R.id.AvgOccupValue)).setText("2");

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, occ_vals.get(4)),
                new DataPoint(1, occ_vals.get(3)),
                new DataPoint(2, occ_vals.get(2)),
                new DataPoint(3, occ_vals.get(1)),
                new DataPoint(4, occ_vals.get(0))
        });
        graph.addSeries(series);
        series.setColor(getResources().getColor(R.color.occupancy_red));
        series.setSpacing(15);
        series.setDrawValuesOnTop(true);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[]{dates.get(4),
                                                                dates.get(3),dates.get(2),
                                                                dates.get(1), dates.get(0)});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Occupancy");
        graph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.BLACK);
        graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.BLACK);

        graph.getGridLabelRenderer().setTextSize(20);
    }

    private void getTemperature()
    {
        int zone_id = ZonesDescriptionActivity.regionID;

        ((TextView) findViewById(R.id.Fahrenheit)).setText("78"+(char) 0x00B0+"F");
        ((TextView) findViewById(R.id.Celsius)).setText("24"+(char) 0x00B0+"C");
        ((TextView) findViewById(R.id.max_outside_temp_f)).setText("78" + (char) 0x00B0 + "F");
        ((TextView) findViewById(R.id.max_outside_temp_c)).setText("24" + (char) 0x00B0 + "C");

        ArrayList<String> info = data_access.getLatestTemperature(zone_id);

        GraphView graph = (GraphView) findViewById(R.id.temperature_graph);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 78),
                new DataPoint(1, 90),
                new DataPoint(2, 78),
                new DataPoint(3, 76),
                new DataPoint(4, 79),
                new DataPoint(5, 80),
                new DataPoint(6, 70)
        });

        graph.addSeries(series);

    }

    private void getPlugLoad()
    {
        //((TextView) findViewById(R.id.CurrPlugValue)).setText("2");

        ArrayList<PlugLoadListParent> parents = data_access.getPlugLoadParentData(ZonesDescriptionActivity.regionID);

        //
        //parents = newParents;
        ListView mListView = (ListView) findViewById(R.id.my_plugload_listview);
        MyPlugLoadListAdapter myPlugLoadListAdapter = new MyPlugLoadListAdapter(this);
        myPlugLoadListAdapter.setParents(parents);
        mListView.setAdapter(myPlugLoadListAdapter);
        //ButtonAdapter m_badapter = new ButtonAdapter(this);
        //m_badapter.setListData(data_access.getAllZoneNames(), data_access.getAllZoneID());
    }

    private void getLighting()
    {
        ((TextView) findViewById(R.id.CurrLightValue)).setText("ON");
        GraphView graph = (GraphView) findViewById(R.id.lighting_graph);

        String[] stat = {"OFF", "ON"};

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 0),
                new DataPoint(2, 0),
                new DataPoint(3, 1),
                new DataPoint(4, 1),
                new DataPoint(5, 1),
                new DataPoint(6, 1)
        });

        graph.addSeries(series);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setVerticalLabels(new String[]{stat[1],
                stat[0],stat[0],
                stat[1],stat[1], stat[1], stat[1]});

        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(app_title.equalsIgnoreCase("Temperature"))
            getMenuInflater().inflate(R.menu.menu_temperature, menu);
        else
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
                SyncUtils.TriggerRefresh();
                return true;

            case R.id.temp_ac_performance:
                predictAC(null);
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

    public void predictAC (View view)
    {
        Intent intent = new Intent(this,ACConsumptionPrediction.class);
        Log.i("EnergyActivity", "Starting my new prediction for AC");
        startActivity(intent);
    }
}
