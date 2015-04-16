package fiu.ssobec.Calculations;

import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fiu.ssobec.DataAccess.DataAccessInterface;
import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.DataAccess.ExternalDatabaseController;

/**
 * Created by Maria on 3/16/2015.
 *
 * Make statistical calculations and save the result in the
 * database
 */
public class StatisticalCalculation {


    public static final String LOG_TAG = "StatisticalCalculation";
    private static String earliest_timestamp;
    private static String last_timestamp;

    private static String upperbound_date;
    private static String lowerbound_date;
    double plugload_energywaste;
    double ac_energyusage;
    double outside_temp_avg;
    double ac_setpoint;
    ArrayList<Integer> region_id;

    Context mcontext;
    DataAccessUser mdata_access;

    //Data is given every hour.
    private static int DB_TIME_INTERVAL = 1;

    public StatisticalCalculation(Context context, ArrayList<Integer> zones)
    {
        region_id = zones;
        mcontext = context;
        mdata_access = new DataAccessUser(mcontext);
        try {
            mdata_access.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        earliest_timestamp = mdata_access.getFirstTimeStamp();
        Log.i(LOG_TAG, "First Time Stamp: "+earliest_timestamp);
        last_timestamp = mdata_access.getLastTimeStamp();
    }

    public void close(){
        mdata_access.close();
    }

    public String getNewest_timestamp() {
        return earliest_timestamp;
    }

    public void setNewest_timestamp(String newest_timestamp) {
        this.earliest_timestamp = newest_timestamp;
    }

    public void calculateData()
    {
        double inside_temp_avg=0;
        double lighting_time_avg=0;
        double lighting_energyusage=0;
        double lighting_energywaste=0;
        double plugloadplugged_time_avg=0;
        double occup_time_avg=0;
        ac_setpoint=0;

        boolean there_is_data = true;

        upperbound_date = earliest_timestamp;

        while(there_is_data && (upperbound_date != DataAccessInterface.TIME_STAMP_FORMAT))
        {
            there_is_data = false;

            getDateWithoutTime(upperbound_date);
            Log.i(LOG_TAG, "Original Timestamp: " + earliest_timestamp + ", Upper: " + upperbound_date + ", Lower: " + lowerbound_date);

            Log.i(LOG_TAG, "Region IDs: "+region_id.toString());


            //List<Integer> region_id = mdata_access.getAllZoneID();
            for (Integer id : region_id) {

                String date = getDateFromDateTime(upperbound_date);

                if(mdata_access.dateExistsInStatTable(date, id))
                    continue;

                //Average of temperature in a day
                inside_temp_avg = avg(mdata_access.getAllTemperatureOnDateInterval(id, upperbound_date, lowerbound_date));

                Log.i(LOG_TAG, "Average Inside Temperature: " + inside_temp_avg);
                Log.i(LOG_TAG, "Array of Temperature: " + mdata_access.getAllTemperatureOnDateInterval(id, upperbound_date, lowerbound_date).toString());

                //How many hours in a day were the lights on
                lighting_time_avg = mdata_access.getTotalTimeLightWasON(id, upperbound_date, lowerbound_date)*DB_TIME_INTERVAL;
                Log.i(LOG_TAG, "Lighting Time Avg: "+lighting_time_avg);

                //How much energy was used by light in that day
                lighting_energyusage = mdata_access.getAllLightingEnergyUsageBefore(id, upperbound_date, lowerbound_date);
                Log.i(LOG_TAG, "Lighting Energy Usage: "+lighting_energyusage);

                //What is the average occupancy in a room in a day
                occup_time_avg = avg(mdata_access.getAllOccupancyBefore(id, upperbound_date, lowerbound_date));
                Log.i(LOG_TAG, "Occupancy Average: "+occup_time_avg);

                //How much energy was wasted in that day
                lighting_energywaste = calculateLightWaste(id, mdata_access.getAllTimesWhenIsRoomEmpty(id, upperbound_date, lowerbound_date));
                Log.i(LOG_TAG, "Lighting Energy Wasted: "+lighting_energywaste);

                //How much energy was used by plug load in a day
                plugloadplugged_time_avg = mdata_access.getnHoursApplIsPlugged(id, upperbound_date, lowerbound_date);
                Log.i(LOG_TAG, "Plug load Time avg: "+plugloadplugged_time_avg);

                if(id.equals(region_id.get(0)))
                    getOutsideTempAndACEnergy(date);


                if(inside_temp_avg+lighting_time_avg+lighting_energyusage+lighting_energywaste+plugloadplugged_time_avg+occup_time_avg+
                        ac_setpoint+plugload_energywaste+ac_energyusage > 0)
                {
                    //save it in the database
                    mdata_access.createStat(id,date, inside_temp_avg, lighting_time_avg,
                    lighting_energyusage, lighting_energywaste, plugloadplugged_time_avg,
                    plugload_energywaste, ac_energyusage, occup_time_avg, outside_temp_avg, ac_setpoint);

                    //Save on JSON Array.
                    //then send datainformation to the external database
                    if(lighting_energyusage + lighting_energywaste != 0)
                        insertEnergyInfoInExternalDB(date, id, lighting_energyusage, lighting_energywaste);

                    there_is_data = true;
                }
            }
            addDay();
        }

    }

    public static double avg(ArrayList<Double> vals)
    {
        if(!vals.isEmpty())
        {
            Double[] d = new Double[vals.size()];
            vals.toArray(d);
            double[] valsarr = ArrayUtils.toPrimitive(d);
            return StatUtils.mean(valsarr);
        }
        else
        {
            return 0;
        }
    }

    public double calculateLightWaste(int region_id, ArrayList<String> datetimesRoomIsEmpty)
    {
        double lightwaste=0;

        if(!datetimesRoomIsEmpty.isEmpty()) {
            String datetimes_arr = sqlArrayFormat(datetimesRoomIsEmpty);

            Log.i(LOG_TAG, "inClause: " + datetimes_arr);

            lightwaste = mdata_access.getLightingWaste(region_id, datetimes_arr);
        }

        return lightwaste;
    }

    public static int calculatePlugLoadWaste(String datetime)
    {
        int plugloadwaste=0;

        //get all the rows in the database where datetime == datetime
        //and plugLoad='ON', at Non working hours.

        return plugloadwaste;
    }

    public static void getDateWithoutTime(String datetime)
    {
        DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateStringFormat2 = DateTimeFormat.forPattern("yyyy-MM-dd 00:00:00");
        DateTime time = dateStringFormat.parseDateTime(datetime);
        upperbound_date = dateStringFormat2.print(time);
        time = time.plusDays(1);
        lowerbound_date = dateStringFormat2.print(time);
    }

    public static void addDay()
    {
        DateTimeFormatter dateStringFormat2 = DateTimeFormat.forPattern("yyyy-MM-dd 00:00:00");
        DateTime up_time = dateStringFormat2.parseDateTime(upperbound_date);
        DateTime low_time = dateStringFormat2.parseDateTime(lowerbound_date);

        up_time = up_time.plusDays(1);
        upperbound_date = dateStringFormat2.print(up_time);

        low_time = low_time.plusDays(1);
        lowerbound_date = dateStringFormat2.print(low_time);
    }

    //Format: (1, 2, 3)
    private String sqlArrayFormat(List<String> datetimes)
    {
        String res="(";
        int counter=0;

        Iterator itr = datetimes.iterator();
        while(itr.hasNext())
        {
            String str = itr.next().toString();
            if(counter == datetimes.size()-1)
                res = res+" '"+str+"' )";
            else
                res = res+" '"+str+"' ,";

            counter++;
        }

        return res;
    }

    private String getDateFromDateTime(String datetime)
    {
        DateTimeFormatter dateStringFormat2 = DateTimeFormat.forPattern("yyyy-MM-dd 00:00:00");
        DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime date_time = dateStringFormat2.parseDateTime(datetime);
        return dateStringFormat.print(date_time);
    }

    //Testing, get Outside Temperature and AC energy consumption
    private void getOutsideTempAndACEnergy(String date)
    {
        List<NameValuePair> id_and_timestamp = new ArrayList<>(1);
        outside_temp_avg=0;
        ac_energyusage=0;
        ac_setpoint=0;
        id_and_timestamp.add(new BasicNameValuePair("date", (date).trim()));

        try {
            String res = new ExternalDatabaseController((ArrayList<NameValuePair>) id_and_timestamp,
                    "http://smartsystems-dev.cs.fiu.edu/testing_naive_alg.php").send();
            Log.i(LOG_TAG, "Response from Database for table: "+res);

            JSONObject obj =  new JSONObject(res);
            JSONObject myobj = obj.getJSONObject("0");
            outside_temp_avg = myobj.getDouble("outside_temperature");
            ac_energyusage = myobj.getDouble("ac_energy_usage");
            ac_setpoint = myobj.getDouble("ac_setpoint");
            Log.i(LOG_TAG, "Outside Temperature: "+outside_temp_avg+", AC_Energy: "+ac_energyusage+", AC_setpoint: "+ac_setpoint);

        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }

    }

    private void insertEnergyInfoInExternalDB(String date, int id, double light_total, double light_waste)
    {
        List<NameValuePair> lightingValuePairs = new ArrayList<>(4);
        lightingValuePairs.add(new BasicNameValuePair("date", (date).trim()));
        lightingValuePairs.add(new BasicNameValuePair("zone_description_region_id", (id + "").trim()));
        lightingValuePairs.add(new BasicNameValuePair("lighting_total_kw", (light_total + "").trim()));
        lightingValuePairs.add(new BasicNameValuePair("lighting_waste_kw", (light_waste + "").trim()));

        try {
            String res = new ExternalDatabaseController((ArrayList<NameValuePair>) lightingValuePairs,
                    "http://smartsystems-dev.cs.fiu.edu/lighting_energy_performance.php").send();

            Log.i(LOG_TAG, "Response from database from lighting perf table: "+res);

            ArrayList<BasicNameValuePair> arrayList = mdata_access.getPlugLoadPerformance(id, upperbound_date, lowerbound_date);
            Iterator iterator = arrayList.iterator();
            while (iterator.hasNext())
            {
                BasicNameValuePair basicNameValuePair = (BasicNameValuePair) iterator.next();

                List<NameValuePair> plugloadValuePairs = new ArrayList<>(4);
                plugloadValuePairs.add(new BasicNameValuePair("date", (date).trim()));
                plugloadValuePairs.add(new BasicNameValuePair("zone_description_region_id", (id + "").trim()));
                plugloadValuePairs.add(new BasicNameValuePair("appliance_time_plugged", basicNameValuePair.getValue()));
                plugloadValuePairs.add(new BasicNameValuePair("appliance_type", basicNameValuePair.getName()));
                res = new ExternalDatabaseController((ArrayList<NameValuePair>) plugloadValuePairs,
                        "http://smartsystems-dev.cs.fiu.edu/plugload_energy_performance.php").send();

                Log.i(LOG_TAG, "Response from database from plugload performance table: "+res);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
