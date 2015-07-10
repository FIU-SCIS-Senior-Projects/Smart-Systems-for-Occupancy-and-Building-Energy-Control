package fiu.ssobec.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fiu.ssobec.Adapters.MyReportsListAdapter;
import fiu.ssobec.AdaptersUtil.ReportListParent;
import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.DataAccess.ExternalDatabaseController;
import fiu.ssobec.Model.User;
import fiu.ssobec.R;
import fiu.ssobec.Synchronization.SyncUtils;

public class GetReportsActivity extends ActionBarActivity {
    public static final int USER_LOGGEDIN = 1;
    public static final String LOG_TAG = "GetReportsActivity";
    public static final String GETALLZONES_PHP = "http://smartsystems-dev.cs.fiu.edu/getreportallzones.php";
    public static final String GETALLUSERS_PHP = "http://smartsystems-dev.cs.fiu.edu/getreportallusers.php";
    private static DataAccessUser data_access;
    ArrayList<ReportListParent> all_regions_list = new ArrayList<ReportListParent>();
    ArrayList<ReportListParent> all_users_list = new ArrayList<ReportListParent>();
    private double average = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Declare the access to the SQLite table for user
        data_access = new DataAccessUser(this);

        //Open the data access to the tables
        try {
            data_access.open();
        } catch (SQLException e) {
            System.err.println(LOG_TAG + ": " + e.toString());
            e.printStackTrace();
        }

        //Synchronize Data
        SyncUtils.CreateSyncAccount(this);
        SyncUtils.TriggerRefresh();

        User user = data_access.getUser(USER_LOGGEDIN); //Get a user that is currently logged in the system

        //If a user that is logged in into the system is not found then start a new LoginActivity
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        //User that is currently logged in is found
        else {

            if (user.getUsertype().equalsIgnoreCase("admin")) {  //Load Facility Manager Layout
                setContentView(R.layout.activity_get_reports);

                ArrayList<ReportListParent> all_regions_report = getReportAllRegions();
                this.all_users_list = getReportAllUsers();

                ListView listview_all_regions = (ListView) findViewById(R.id.zone_plugload_listview);
                MyReportsListAdapter allRegionsListAdapter = new MyReportsListAdapter(this);
                allRegionsListAdapter.setParents(all_regions_report);
                listview_all_regions.setAdapter(allRegionsListAdapter);

                String avg = average+ "";
                if(avg.contains(".")){
                    int index = avg.indexOf(".");
                    avg = avg.substring(0,index + 3);
                }
                TextView plugload_average = (TextView) findViewById(R.id.average_plugload);
                plugload_average.setText("Average Energy Consumption is " + avg +" kW");

                GraphView graph = (GraphView) findViewById(R.id.plugload_graph);
                GraphView graph2 = (GraphView) findViewById(R.id.user_rewards_graph);

                DataPoint[] points = new DataPoint[all_regions_list.size()];

                String[] names = new String[all_regions_list.size()];
                String leyend1 ="Legend: ";
                for (int i = 0; i < all_regions_list.size(); i++) {
                    points[i] = new DataPoint(i, Double.parseDouble(all_regions_list.get(i).getValue()));
                    names[i] = all_regions_list.get(i).getDescription();
                    leyend1 += all_regions_list.get(i).getDescription() + " " + all_regions_list.get(i).getName()+", ";
                    if(i == all_regions_list.size()){
                        leyend1 = leyend1.substring(0,leyend1.lastIndexOf(","));
                    }
                }
                BarGraphSeries<DataPoint> series = new BarGraphSeries<>(points);

                graph.addSeries(series);

                series.setColor(getResources().getColor(R.color.plugload_green));
                series.setSpacing(25);
                series.setDrawValuesOnTop(true);

                if(all_regions_list.size() >= 2) {
                    StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
                    staticLabelsFormatter.setHorizontalLabels(names);
                    graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

                }

                String[] u_names = new String[all_users_list.size()];
                DataPoint[] u_points = new DataPoint[all_users_list.size()];
                String leyend2 ="Legend: ";
                for (int i = 0; i < all_users_list.size(); i++) {
                    u_points[i] = new DataPoint(i, Integer.parseInt(all_users_list.get(i).getValue()));
                    u_names[i] = all_users_list.get(i).getDescription();
                    leyend2 += all_users_list.get(i).getDescription() + " " + all_users_list.get(i).getName()+", ";
                    if(i == all_users_list.size()){
                        leyend2 = leyend2.substring(0,leyend2.lastIndexOf(","));
                    }
                }

                BarGraphSeries<DataPoint> u_series = new BarGraphSeries<>(u_points);
                graph2.addSeries(u_series);

                u_series.setColor(getResources().getColor(R.color.plugload_green));
                u_series.setSpacing(25);
                u_series.setDrawValuesOnTop(true);

                if(all_users_list.size() >= 2) {
                    StaticLabelsFormatter staticLabelsFormatter2 = new StaticLabelsFormatter(graph2);
                    staticLabelsFormatter2.setHorizontalLabels(u_names);
                    graph2.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter2);
                }

                TextView leyendzones = (TextView) findViewById(R.id.leyend1);
                TextView leyendusers = (TextView) findViewById(R.id.leyend2);
                leyendzones.setText(leyend1);
                leyendusers.setText(leyend2);

                GraphViewFormatting(graph,"Zones" , "Energy Usage in Kilowatts");
                GraphViewFormatting(graph2,"Users" , "Total Reward Points");

                TabHost tabs = (TabHost)findViewById(R.id.tabHostReports);

                tabs.setup();

                TabHost.TabSpec spec = tabs.newTabSpec("tag1");
                spec.setContent(R.id.tab1);
                spec.setIndicator("All Zones Comparison");
                tabs.addTab(spec);

                spec = tabs.newTabSpec("tag2");
                spec.setContent(R.id.tab2);
                spec.setIndicator("User Rewards Comparison");
                tabs.addTab(spec);


            }
            else{
                //TODO: Add get personalized reports for general user
            }
        }
    }

    private ArrayList<ReportListParent> getReportAllRegions() {
        Log.i(LOG_TAG, "Starting GetReportsActivity");
        List<NameValuePair> emptyarr = new ArrayList<>(1);

        String res = "";
        ArrayList<ReportListParent> all_regions_report = new ArrayList<ReportListParent>();

        //Get the regions' plugload information
        try {
            res = new ExternalDatabaseController((ArrayList<NameValuePair>) emptyarr, GETALLZONES_PHP).send();

            Log.i(LOG_TAG, "Result: " + res);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(LOG_TAG, "res: " + res);
        System.out.println("printout: " + res);


        if (res != null) {
            JSONObject obj;

            try {
                obj = new JSONObject(res);
                JSONObject myobj;
                int j = 0;
                while (obj.has(j + "") && j < obj.length()) {
                    myobj = obj.getJSONObject(j + "");
                    System.out.println("length " + obj.length());

                    String name = myobj.getString("region_name");
                    int position = j+1;
                    String description = position + "";


                    String value = "";
                    if (!myobj.isNull("plugload")) {
                        double plugload = myobj.getDouble("plugload");
                        average += plugload;
                        value = (plugload +"");
                        if(value.contains(".")){
                            int index = value.indexOf(".");
                            value = value.substring(0,index + 2);
                        }
                    }

                    ReportListParent record = new ReportListParent();
                    record.setName(name);
                    record.setDescription(description);
                    record.setIcon("zone");
                    record.setValue(value);
                    all_regions_list.add(record);

                    j++;
                }

                average = average/j;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ReportListParent recordHigh = all_regions_list.get(0);
        recordHigh.setDescription("High");
        ReportListParent recordMedian = all_regions_list.get(all_regions_list.size()/2);
        recordMedian.setDescription("Median");
        ReportListParent recordLow = all_regions_list.get(all_regions_list.size()-1);
        recordLow.setDescription("Low");
        all_regions_report.add(recordHigh);
        all_regions_report.add(recordMedian);
        all_regions_report.add(recordLow);
        
        return all_regions_report;
    }

    private ArrayList<ReportListParent> getReportAllUsers() {
        Log.i(LOG_TAG, "Starting GetReportsActivity");
        List<NameValuePair> emptyarr = new ArrayList<>(1);

        String res = "";
        ArrayList<ReportListParent> all_users_list = new ArrayList<ReportListParent>();

        //Get the regions' plugload information
        try {
            res = new ExternalDatabaseController((ArrayList<NameValuePair>) emptyarr, GETALLUSERS_PHP).send();

            Log.i(LOG_TAG, "Result: " + res);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(LOG_TAG, "res: " + res);
        System.out.println("printout: " + res);
        if (res != null) {
            JSONObject obj;

            try {
                obj = new JSONObject(res);
                JSONObject myobj;
                int j = 0;
                while (obj.has(j + "") && j < obj.length()) {
                    myobj = obj.getJSONObject(j + "");
                    System.out.println("length " + obj.length());

                    String name = myobj.getString("name");
                    int position = j+1;
                    String description = position + "";

                    String value = "";

                    int rewards = myobj.getInt("value");
                    value = (rewards +"");


                    ReportListParent record = new ReportListParent();
                    record.setName(name);
                    record.setDescription(description);
                    record.setIcon("user");
                    record.setValue(value);
                    all_users_list.add(record);

                    j++;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return all_users_list;
    }


    /**
     * Stylization for the graph views for GetReportsActivity
     * @param graph, x Axis name, y Axis name
     */
        private void GraphViewFormatting(GraphView graph, String xAxis, String yAxis){

            graph.getGridLabelRenderer().setHorizontalAxisTitle(xAxis);
            graph.getGridLabelRenderer().setVerticalAxisTitle(yAxis);
            graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
            graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
            graph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.BLACK);
            graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.BLACK);
            graph.getGridLabelRenderer().setTextSize(22);
            graph.getLegendRenderer().setVisible(true);
            graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_get_reports, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }
    }
