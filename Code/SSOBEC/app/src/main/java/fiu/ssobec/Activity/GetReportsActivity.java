package fiu.ssobec.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
    public static final String GETMYZONES_PHP = "http://smartsystems-dev.cs.fiu.edu/getreportmyzones.php";
    public static int user_id;
    private static DataAccessUser data_access;
    ArrayList<ReportListParent> all_regions_list = new ArrayList<ReportListParent>();
    ArrayList<ReportListParent> all_users_list = new ArrayList<ReportListParent>();
    ArrayList<ReportListParent> my_zones_list = new ArrayList<ReportListParent>();
    private double average = 0;
    private double my_zones_average = 0;
    private String user_rank = "NA"; //Not Applicable for a facility manager
    private String current_user = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("GetReportsActivity","We have made it to getReports");
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

        else { //User that is currently logged in is found

            if (user.getUsertype().equalsIgnoreCase("admin")) {  //Load Facility Manager Layout
                setContentView(R.layout.activity_get_reports);
                TextView TextView1 = (TextView)findViewById(R.id.reports_textview1);
                TextView1.setText("Manager Reports");

                ArrayList<ReportListParent> all_regions_report = getReportAllRegions();
                this.all_users_list = getReportAllUsers();

                ListView listview_all_regions = (ListView) findViewById(R.id.zone_plugload_listview);
                MyReportsListAdapter allRegionsListAdapter = new MyReportsListAdapter(this);
                allRegionsListAdapter.setParents(all_regions_report);
                listview_all_regions.setAdapter(allRegionsListAdapter);

                String avg = average+ "";
                if(avg.contains(".")){
                    int index = avg.indexOf(".");
                    avg = avg.substring(0,index + 2);
                }
                TextView plugload_average = (TextView) findViewById(R.id.average_plugload);
                plugload_average.setText("Average Energy Consumption is " + avg +" kW");

                GraphView graph = (GraphView) findViewById(R.id.plugload_graph);
                GraphView graph2 = (GraphView) findViewById(R.id.user_rewards_graph);

                DataPoint[] points = new DataPoint[all_regions_list.size()];
                Log.d(LOG_TAG,"What is the number of points? "+points.length);
                String[] names = new String[all_regions_list.size()];
                String leyend1 ="Legend: ";
                for (int i = 0; i < all_regions_list.size(); i++) {
                    points[i] = new DataPoint(i, Double.parseDouble(all_regions_list.get(i).getValue()));
                    names[i] = all_regions_list.get(i).getDescription();
                    leyend1 += all_regions_list.get(i).getDescription().trim() + "-" + all_regions_list.get(i).getName()+", ";
                    if(i == all_regions_list.size()-1){
                        leyend1 = leyend1.substring(0,leyend1.lastIndexOf(","));
                    }
                }
                BarGraphSeries<DataPoint> series = new BarGraphSeries<>(points);

                graph.addSeries(series);

                series.setColor(getResources().getColor(R.color.fbutton_color_sun_flower));
                series.setSpacing(20);
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
                    leyend2 += all_users_list.get(i).getDescription() + "-" + all_users_list.get(i).getName()+", ";
                    if(i == all_users_list.size()-1){
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
                spec.setIndicator("Users Comparison");
                tabs.addTab(spec);

                spec = tabs.newTabSpec("tag3");
                spec.setContent(R.id.tab3);
                spec.setIndicator("Suggestions");
                tabs.addTab(spec);

                setSuggestions(all_regions_list, average);

            }
            else{ //Display general user reports

                setContentView(R.layout.activity_get_reports);
                TextView TextView1 = (TextView)findViewById(R.id.reports_textview1);
                TextView1.setText("My Reports");
                Log.d(LOG_TAG,"We have set the contentView");
                //Get statistics to compare to all zones
                ArrayList<ReportListParent> all_regions_report = getReportAllRegions();
                Log.d(LOG_TAG, "We have retrieved all reports from all regions");
                //Get statistics for current user's followed zones
                user_id = user.getId();
                System.out.println(LOG_TAG + " User ID: " + user_id);
                ArrayList<ReportListParent> my_zones_report = getReportMyZones();
                Log.d(LOG_TAG,"We have retrieved MY reports");
                //Get statistics for all users' reward points
                current_user = user.getEmail();
                this.all_users_list = getReportAllUsers();
                if(!user_rank.equalsIgnoreCase("NA")){
                    int myrank = Integer.parseInt(user_rank);
                    if(user_rank.equalsIgnoreCase("1")){
                        TextView rank = (TextView) findViewById(R.id.reports_user_rank);
                        rank.setText("Congratulations! You Rank #" + user_rank +" with "+ all_users_list.get(myrank-1).getValue() +" points");

                        TextView highestrank = (TextView) findViewById(R.id.textView7);
                        highestrank.setText("");
                    }else{
                        TextView rank = (TextView) findViewById(R.id.reports_user_rank);
                        rank.setText("You Rank #" + user_rank +" with "+ all_users_list.get(myrank-1).getValue() +" points");

                        TextView highestrank = (TextView) findViewById(R.id.textView7);
                        highestrank.setText("Highest Rank is " + all_users_list.get(0).getName() +" with "+ all_users_list.get(0).getValue() +" points");
                    }

                }


                if (my_zones_report.size() > 2) {
                    ListView listview_all_regions = (ListView) findViewById(R.id.zone_plugload_listview);
                    MyReportsListAdapter myZonesListAdapter = new MyReportsListAdapter(this);
                    myZonesListAdapter.setParents(my_zones_report);
                    listview_all_regions.setAdapter(myZonesListAdapter);

                    TextView TextView2 = (TextView) findViewById(R.id.reports_table_name);
                    TextView2.setText("My Zone");
                }

                else{
                    TextView TextView2 = (TextView) findViewById(R.id.reports_table_name);
                    TextView2.setText("");
                    TextView TextView3 = (TextView) findViewById(R.id.energy_consumed);
                    TextView3.setText("");
                }


                String avg = average+ "";
                if(avg.contains(".")){
                    int index = avg.indexOf(".");
                    avg = avg.substring(0,index + 2);
                }
                TextView plugload_average = (TextView) findViewById(R.id.average_plugload);
                plugload_average.setText("All Zones Avg. Consumption is " + avg +" kW");


                String myavg = my_zones_average+ "";
                if(myavg.contains(".")){
                    int index = myavg.indexOf(".");
                    myavg = myavg.substring(0,index + 2);
                }

                TextView my_zones_avg = (TextView) findViewById(R.id.my_zones_average_plugload);
                my_zones_avg.setText("My Zones Avg. Consumption is " + myavg+" kW");


                GraphView graph = (GraphView) findViewById(R.id.plugload_graph);
                GraphView graph2 = (GraphView) findViewById(R.id.user_rewards_graph);

                DataPoint[] points = new DataPoint[my_zones_list.size()];

                String[] names = new String[my_zones_list.size()];
                String leyend1 ="Legend: ";
                for (int i = 0; i < my_zones_list.size(); i++) {
                    points[i] = new DataPoint(i, Double.parseDouble(my_zones_list.get(i).getValue()));
                    names[i] = my_zones_list.get(i).getDescription();
                    leyend1 += my_zones_list.get(i).getDescription().trim() + "-" + my_zones_list.get(i).getName()+", ";
                    if(i == my_zones_list.size()-1){
                        leyend1 = leyend1.substring(0,leyend1.lastIndexOf(","));
                    }
                }
                BarGraphSeries<DataPoint> series = new BarGraphSeries<>(points);

                graph.addSeries(series);

                series.setColor(getResources().getColor(R.color.fbutton_color_sun_flower));
                series.setSpacing(25);
                series.setDrawValuesOnTop(true);
                Log.d(LOG_TAG,"We are just before my bug fix");
                if(my_zones_list.size() >= 2) { //This is the original code: all_regions_list.size() >= 2
                    StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
                    staticLabelsFormatter.setHorizontalLabels(names);
                    graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

                }
                Log.d(LOG_TAG,"We have made it after my bug fix");

                String[] u_names = new String[all_users_list.size()];
                DataPoint[] u_points = new DataPoint[all_users_list.size()];
                String leyend2 ="Legend: ";
                for (int i = 0; i < all_users_list.size(); i++) {
                    u_points[i] = new DataPoint(i, Integer.parseInt(all_users_list.get(i).getValue()));
                    u_names[i] = all_users_list.get(i).getDescription();
                    leyend2 += all_users_list.get(i).getDescription() + "-" + all_users_list.get(i).getName()+", ";
                    if(i == all_users_list.size()-1){
                        leyend2 = leyend2.substring(0,leyend2.lastIndexOf(","));
                    }
                }

                BarGraphSeries<DataPoint> u_series = new BarGraphSeries<>(u_points);
                graph2.addSeries(u_series);

                u_series.setColor(getResources().getColor(R.color.plugload_green));
                u_series.setSpacing(25);
                u_series.setDrawValuesOnTop(true);
                Log.d(LOG_TAG,"Right before possible new problem");
                if(all_users_list.size() >= 2) {
                    StaticLabelsFormatter staticLabelsFormatter2 = new StaticLabelsFormatter(graph2);
                    staticLabelsFormatter2.setHorizontalLabels(u_names);
                    graph2.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter2);
                }
                Log.d(LOG_TAG,"Looks like we have a new problem");
                TextView leyendzones = (TextView) findViewById(R.id.leyend1);
                TextView leyendusers = (TextView) findViewById(R.id.leyend2);
                leyendzones.setText(leyend1);
                leyendusers.setText(leyend2);

                GraphViewFormatting(graph,"My Zones" , "Energy Usage in Kilowatts");
                GraphViewFormatting(graph2,"User Rankings" , "Total Reward Points");

                TabHost tabs = (TabHost)findViewById(R.id.tabHostReports);

                tabs.setup();

                TabHost.TabSpec spec = tabs.newTabSpec("tag1");
                spec.setContent(R.id.tab1);
                spec.setIndicator("My Zones");
                tabs.addTab(spec);

                spec = tabs.newTabSpec("tag2");
                spec.setContent(R.id.tab2);
                spec.setIndicator("My Ranking");
                tabs.addTab(spec);

                spec = tabs.newTabSpec("tag3");
                spec.setContent(R.id.tab3);
                spec.setIndicator("Suggestions");
                tabs.addTab(spec);

                setSuggestions(my_zones_list,my_zones_average);
                Log.d(LOG_TAG,"This is the end of the line buddy");
            }

        }
    }



    private ArrayList<ReportListParent> getReportAllRegions() {
        //Gets plugload statistics for all regions, calculates average consumption
        //Returns zone data for High, Median and Low consumption regions
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
                Log.d("GetReportsActivity","What is the value of j? "+j);
                average = average/j;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (all_regions_list.size() > 2) {
            ReportListParent recordHigh = all_regions_list.get(0);
            recordHigh.setDescription("High");
            ReportListParent recordMedian = all_regions_list.get(all_regions_list.size() / 2);
            recordMedian.setDescription("Median");
            ReportListParent recordLow = all_regions_list.get(all_regions_list.size() - 1);
            recordLow.setDescription("Low ");
            all_regions_report.add(recordHigh);
            all_regions_report.add(recordMedian);
            all_regions_report.add(recordLow);
        }
        return all_regions_report;
    }

    private ArrayList<ReportListParent> getReportMyZones() {

        List<NameValuePair> userId = new ArrayList<>(1);
        String id = user_id + "";
        userId.add(new BasicNameValuePair("user_id", (user_id+"").trim()));

        String res = "";
        ArrayList<ReportListParent> my_zones_report = new ArrayList<ReportListParent>();

        //Get the user's zones plugload information
        try {
            res = new ExternalDatabaseController((ArrayList<NameValuePair>) userId, GETMYZONES_PHP).send();

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
                        my_zones_average += plugload;
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
                    my_zones_list.add(record);

                    j++;
                }
                Log.d("GetReportsActivity2","What is j? "+j);
                my_zones_average = my_zones_average/j;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (my_zones_list.size()>2) {
            ReportListParent recordHigh = my_zones_list.get(0);
            recordHigh.setDescription("High");
            ReportListParent recordMedian = my_zones_list.get(my_zones_list.size() / 2);
            recordMedian.setDescription("Median");
            ReportListParent recordLow = my_zones_list.get(my_zones_list.size() - 1);
            recordLow.setDescription("Low ");
            my_zones_report.add(recordHigh);
            my_zones_report.add(recordMedian);
            my_zones_report.add(recordLow);
        }
        return my_zones_report;
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
                    if(name.equalsIgnoreCase(current_user)){
                        user_rank = description;
                    }

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
     * Sets Suggestions tab content of Get Reports Activity
     * @param zones_list
     */
    private void setSuggestions(ArrayList<ReportListParent> zones_list, double zones_avg){

        ArrayList<ReportListParent> above_average_list = new ArrayList<ReportListParent>();
        double sum = zones_avg * zones_list.size();

        for(ReportListParent zone: zones_list){
            Double value = Double.valueOf(zone.getValue());
            if(value > zones_avg){
                double percent = value*100/sum;
                String percent_str = percent+"";
                if(percent_str.contains(".")){
                    int index = percent_str.indexOf(".");
                    percent_str = percent_str.substring(0,index);
                }
                ReportListParent record = new ReportListParent();
                record.setName(zone.getName());
                record.setDescription(percent_str+"%");
                record.setValue(zone.getValue());
                record.setIcon(zone.getIcon());
                above_average_list.add(record);
            }
        }

        //Format averages data
        String my_avg = zones_avg+"";
        if(my_avg.contains(".")){
            int index = my_avg.indexOf(".");
            my_avg = my_avg.substring(0,index + 2);
        }
        String avg = average+"";
        if(avg.contains(".")){
            int index = avg.indexOf(".");
            avg = avg.substring(0,index + 2);
        }


        //Compare User's zones average consumption with average consumption for all zones
        if(zones_avg < average){
            ImageView icon2 = (ImageView)findViewById(R.id.icon2);
            icon2.setImageResource(R.drawable.good_job_green_ribbon);
            TextView text1 = (TextView) findViewById(R.id.suggestions1);
            text1.setText("Your Zones Average Consumption of " + my_avg+" kW is below the Average Consumption of "+avg+" kW of all zones");

        }else if(zones_avg == average){
            ImageView icon2 = (ImageView)findViewById(R.id.icon2);
            icon2.setImageResource(R.drawable.thumbs_up_circle);
            TextView text1 = (TextView) findViewById(R.id.suggestions1);
            text1.setText("Continue Saving!\nYour Zones Average Consumption of " + my_avg+" kW is at the Average Consumption of all zones");
        }else{
            ImageView icon2 = (ImageView)findViewById(R.id.icon2);
            icon2.setImageResource(R.drawable.warning);
            TextView text1 = (TextView) findViewById(R.id.suggestions1);
            text1.setText("Save Energy!!!\nYour Zones Average Consumption of " + my_avg+" kW is above the Average Consumption of "+avg+" kW of all zones");
        }

        //Display data for User's zones that are above User's average consumption
        if(above_average_list.size()>0){
            TextView text2 = (TextView) findViewById(R.id.suggestions2);
            text2.setText("Reduce Consumption in your "+ above_average_list.size() +" zone(s) above average consumption");
            ListView listview_zones = (ListView) findViewById(R.id.zone_suggestions_listview);
            MyReportsListAdapter myZonesListAdapter = new MyReportsListAdapter(this);
            myZonesListAdapter.setParents(above_average_list);
            listview_zones.setAdapter(myZonesListAdapter);

        }else{
            TextView text2 = (TextView) findViewById(R.id.suggestions2);
            text2.setText("");
            TextView table1 = (TextView) findViewById(R.id.suggestions_table_name);
            table1.setText("");
            TextView table2 = (TextView) findViewById(R.id.suggestions_table_percent);
            table2.setText("");
            TextView table3 = (TextView) findViewById(R.id.suggestions_table_energy);
            table3.setText("");
        }

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
        graph.getLegendRenderer().setVisible(false);
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
