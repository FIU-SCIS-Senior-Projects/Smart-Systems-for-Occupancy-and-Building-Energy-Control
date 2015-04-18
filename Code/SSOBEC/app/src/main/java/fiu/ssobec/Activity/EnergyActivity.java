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
import android.widget.TabHost;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.http.NameValuePair;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import fiu.ssobec.Adapters.MyPlugLoadListAdapter;
import fiu.ssobec.AdaptersUtil.PlugLoadListParent;
import fiu.ssobec.Calculations.StatisticalCalculation;
import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.DataAccess.ExternalDatabaseController;
import fiu.ssobec.R;
import fiu.ssobec.Synchronization.DataSync.AuthenticatorService;
import fiu.ssobec.Synchronization.SyncConstants;
import fiu.ssobec.Synchronization.SyncUtils;

public class EnergyActivity extends ActionBarActivity {


    public static final String LOG_TAG = "EnergyActivity";
    private DataAccessUser data_access;
    TextView mTextView1;
    TextView mTextView2;
    private Menu mOptionsMenu;
    private Object mSyncObserverHandle;
    String app_title;
    ArrayList<String> building_appliance_types;
    ArrayList<Double> building_appliance_consumption;

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

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        //Get the title of the activity
        if(intent.getExtras() != null)
        {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(title, getIntent().getStringExtra(ZonesDescriptionActivity.ACTIVITY_NAME));
            //editor.commit();
            editor.apply();
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

        if(dates.isEmpty())
        {
            this.setContentView(R.layout.empty_activity_message);
            return;
        }

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
        series.setTitle("Occupancy");

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[]{dates.get(4),
                dates.get(3), dates.get(2),
                dates.get(1), dates.get(0)});

        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Occupancy");
        graph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.BLACK);
        graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.BLACK);

        graph.getGridLabelRenderer().setTextSize(22);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }

    private void getTemperature()
    {
        int inside_temperature;
        float outside_temperature = 89;

        GraphView graph = (GraphView) findViewById(R.id.temperature_graph);

        ArrayList<Integer> temp_vals = data_access.getLatestManyTemperature(ZonesDescriptionActivity.regionID);

        if(temp_vals.isEmpty())
        {
            this.setContentView(R.layout.empty_activity_message);
            return;
        }

        inside_temperature = temp_vals.get(temp_vals.size()-1);
        
        ((TextView) findViewById(R.id.Fahrenheit)).setText(inside_temperature + "" + (char) 0x00B0 + "F");
        ((TextView) findViewById(R.id.Celsius)).setText(convertFahrenheitToCelsius(inside_temperature)+""+(char) 0x00B0+"C");
        ((TextView) findViewById(R.id.max_outside_temp_f)).setText(outside_temperature+"" + (char) 0x00B0 + "F");
        ((TextView) findViewById(R.id.max_outside_temp_c)).setText(convertFahrenheitToCelsius(outside_temperature)+"" + (char) 0x00B0 + "C");

        DataPoint[] points = new DataPoint[50];

        for (int i = 0; i < 50; i++) {
            points[i] = new DataPoint(i, temp_vals.get(i));
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);

        // set manual X bounds
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(5);
        graph.onDataChanged(false, false);

        // enable scrolling
        graph.getViewport().setScrollable(true);
        graph.addSeries(series);

    }

    private void getPlugLoad()
    {
        ArrayList<PlugLoadListParent> parents = data_access.getPlugLoadParentData(ZonesDescriptionActivity.regionID);

        if(parents.isEmpty())
        {
            this.setContentView(R.layout.empty_activity_message);
            return;
        }

        getBuildingPlugLoadPerformance();

        ListView mListView = (ListView) findViewById(R.id.my_plugload_listview);
        MyPlugLoadListAdapter myPlugLoadListAdapter = new MyPlugLoadListAdapter(this);
        myPlugLoadListAdapter.setParents(parents);
        mListView.setAdapter(myPlugLoadListAdapter);

        GraphView graph = (GraphView) findViewById(R.id.plugload_graph);
        GraphView graph2 = (GraphView) findViewById(R.id.plugloadbuilding_graph);

        DataPoint[] points = new DataPoint[parents.size()];
        DataPoint[] b_points = new DataPoint[parents.size()];
        String[] appl_names = new String[parents.size()];
        String[] appl_type = new String[parents.size()];

        for (int i = 0; i < parents.size(); i++) {
            points[i] = new DataPoint(i, Double.parseDouble(parents.get(i).getEnergy_consumed()));
            appl_names[i] = parents.get(i).getName();
            appl_type[i] = parents.get(i).getAppl_type();

            if(building_appliance_types.contains(appl_type[i]))
            {
                b_points[i] = new DataPoint(i, building_appliance_consumption.get(building_appliance_types.indexOf(appl_type[i])));
            }
            else
            {
                b_points[i] = new DataPoint(i, 0.0);
            }
        }

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(points);
        BarGraphSeries<DataPoint> b_series = new BarGraphSeries<>(b_points);
        graph.addSeries(series);
        graph2.addSeries(b_series);

        series.setColor(getResources().getColor(R.color.plugload_green));
        series.setSpacing(15);
        series.setDrawValuesOnTop(true);

        b_series.setColor(getResources().getColor(R.color.plugload_green));
        b_series.setSpacing(15);
        b_series.setDrawValuesOnTop(true);

        if(parents.size() >= 2) {
            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
            StaticLabelsFormatter staticLabelsFormatter2 = new StaticLabelsFormatter(graph2);
            staticLabelsFormatter.setHorizontalLabels(appl_names);
            staticLabelsFormatter2.setHorizontalLabels(appl_type);
            graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
            graph2.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter2);

        }

        GraphViewFormatting(graph);
        GraphViewFormatting(graph2);

        TabHost tabs = (TabHost)findViewById(R.id.tabHost);

        tabs.setup();

        TabHost.TabSpec spec = tabs.newTabSpec("tag1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Appliances");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Energy Usage Comparison");
        tabs.addTab(spec);
    }

    private void getLighting()
    {
        DecimalFormat df = new DecimalFormat("#.#");
        ((TextView) findViewById(R.id.CurrLightValue)).setText("ON");

        double myval = data_access.getLightingAverageDay(ZonesDescriptionActivity.regionID);

        if(myval == 0)
        {
            this.setContentView(R.layout.empty_activity_message);
            return;
        }

        ((TextView) findViewById(R.id.AvgLightValue)).setText(df.format(myval)+" hours");

        PieChart mPieChart = (PieChart) findViewById(R.id.myLightingChart);

        float light_kw = (float) data_access.getLightingEnergyUsage(ZonesDescriptionActivity.regionID);
        float light_waste = (float) data_access.getLightingEnergyWaste(ZonesDescriptionActivity.regionID);
        mPieChart.addPieSlice(new PieModel("efficiently used", (light_kw-light_waste)*1000, getResources().getColor(R.color.lighting_yellow)));
        mPieChart.addPieSlice(new PieModel("wasted", light_waste*1000, getResources().getColor(R.color.warning_red)));
        mPieChart.startAnimation();

        //Get lighting performance from other buildings
        PieChart mPieChart2 = getBuildingLightingPerformance();
        mPieChart2.startAnimation();
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

    public void predictConsumption(View view)
    {
        Intent intent = new Intent(this,ConsumptionAppliances.class);
        Log.i("EnergyActivity", "Starting my new prediction for ConsumptionAppliances");
        startActivity(intent);
    }

    private PieChart getBuildingLightingPerformance()
    {
        PieChart mPieChart = (PieChart) findViewById(R.id.buildingLightingChart);

        ArrayList<Double> light_consumption = new ArrayList<>();
        ArrayList<Double> light_waste = new ArrayList<>();

        List<NameValuePair> emptyarr = new ArrayList<>(1);
        String res=null;

        try {
            res = new ExternalDatabaseController((ArrayList<NameValuePair>) emptyarr,
                    "http://smartsystems-dev.cs.fiu.edu/getbuildinglightingperformance.php").send();

            JSONObject obj =  new JSONObject(res);
            JSONObject myobj;
            int j=0;
            while (obj.has(j + "")) {
                myobj = obj.getJSONObject(j + "");
                light_consumption.add(myobj.getDouble("lighting_total_kw"));
                light_waste.add(myobj.getDouble("lighting_waste_kw"));

                j++;
            }
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        if(!light_consumption.isEmpty()&&!light_waste.isEmpty())
        {
            mPieChart.addPieSlice(new PieModel("efficiently used", (float) ((StatisticalCalculation.avg(light_consumption))*1000), getResources().getColor(R.color.lighting_yellow)));
            mPieChart.addPieSlice(new PieModel("wasted", (float) ((StatisticalCalculation.avg(light_waste))*1000), getResources().getColor(R.color.warning_red)));
        }

        return mPieChart;
    }

    private void getBuildingPlugLoadPerformance()
    {
        List<NameValuePair> emptyarr = new ArrayList<>(1);

        building_appliance_consumption = new ArrayList<>();
        building_appliance_types = new ArrayList<>();

        String res=null;
        try {
            res = new ExternalDatabaseController((ArrayList<NameValuePair>) emptyarr,
                    "http://smartsystems-dev.cs.fiu.edu/getbuildingplugloadperformance.php").send();

            Log.i(LOG_TAG, "getBuildingPlugLoadPerformance: "+res);
            JSONObject obj =  new JSONObject(res);
            JSONObject myobj;
            int j=0;
            while (obj.has(j + "")) {
                myobj = obj.getJSONObject(j + "");
                building_appliance_types.add(myobj.getString("appliance_type"));
                building_appliance_consumption.add(myobj.getDouble("appliance_time_plugged"));
                j++;
            }

            Log.i(LOG_TAG, "building_appliance_types: "+building_appliance_types.toString());
            Log.i(LOG_TAG, "building_appliance_consumption: "+building_appliance_consumption.toString());
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void GraphViewFormatting(GraphView graph)
    {
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Appliances");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Energy Usage in Kilowatts");
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.BLACK);
        graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.BLACK);
        graph.getGridLabelRenderer().setTextSize(22);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }
}
