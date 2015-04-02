package fiu.ssobec;

import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fiu.ssobec.DataAccess.DataAccessUser;

/**
 * Created by Maria on 3/16/2015.
 *
 * Make statistical calculations and save the result in the
 * database
 */
public class StatisticalCalculation {


    public static final String LOG_TAG = "StatisticalCalculation";
    private static String currdatetime;
    private static String earliest_timestamp;
    private static String last_timestamp;

    private static String upperbound_date;
    private static String lowerbound_date;
    Context mcontext;
    DataAccessUser mdata_access;

    public StatisticalCalculation(Context context)
    {
        mcontext = context;
        mdata_access = new DataAccessUser(mcontext);
        try {
            mdata_access.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        earliest_timestamp = mdata_access.getFirstTimeStamp();
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
        int lighting_energyusage=0;
        int lighting_energywaste=0;
        int plugload_energyusage=0;
        int plugload_energywaste=0;
        int ac_energyusage=0;
        double occup_time_avg=0;
        double outside_temp_avg=0;

        boolean there_is_data = true;
        List<Integer> region_id = mdata_access.getAllZoneID();

        upperbound_date = earliest_timestamp;

        int counter=0;

        while(counter < 5)
        {
            getDateWithoutTime(upperbound_date);
            Log.i(LOG_TAG, "Original Timestamp: "+earliest_timestamp+", Upper: "+upperbound_date+", Lower: "+lowerbound_date);

            for (Integer id : region_id) {

                //Average of temperature in a day
                inside_temp_avg = avg(mdata_access.getAllTemperatureOnDateInterval(id, upperbound_date, lowerbound_date));

                Log.i(LOG_TAG, "Average Inside Temperature: " + inside_temp_avg);
                Log.i(LOG_TAG, "Array of Temperature: " + mdata_access.getAllTemperatureOnDateInterval(id, upperbound_date, lowerbound_date).toString());

                //How many hours in a day were the lights on
                lighting_time_avg = mdata_access.getHowLongAreLightsON(id, upperbound_date, lowerbound_date);
                Log.i(LOG_TAG, "Lighting Time Avg: "+lighting_time_avg);

                //How much energy was used by light in that day
                lighting_energyusage = (int) sum(mdata_access.getAllLightingEnergyUsageBefore(id, upperbound_date, lowerbound_date));
                Log.i(LOG_TAG, "Lighting Energy Usage: "+lighting_energyusage);

            /*
            //What is the average occupancy in a room in a day
            occup_time_avg = avg(data_access.getAllOccupancyBefore(id, currdatetime));

            Log.i(LOG_TAG, "Occupancy Average: "+occup_time_avg);


            //How much energy was wasted in that day
            lighting_energywaste = calculateLightWaste(data_access.getAllTimesWhenIsRoomEmpty(id, currdatetime));

            //How much energy was used by plug load in a day
            plugload_energyusage = sum(data_access.getAllPlugLoadEnergyBefore(id, currdatetime));

            //How much energy was wasted by plug load
            plugload_energywaste = calculatePlugLoadWaste(currdatetime);

            //How much energy was used by the AC
            ac_energyusage = data_access.getAllDateTimesACStateOnBefore(id, currdatetime);


            if(inside_temp_avg+lighting_time_avg+lighting_energyusage+lighting_energywaste+plugload_energyusage
                    +plugload_energywaste+ac_energyusage+occup_time_avg > 0)
            {
                //save it in the database
            }*/

            }
            addDay();
            counter++;
        }

    }

    public static double avg(ArrayList<Double> vals)
    {
        if(!vals.isEmpty())
        {
            Double[] d = new Double[vals.size()];
            vals.toArray(d);
            double[] valsarr = ArrayUtils.toPrimitive(d);
            //Log.i(LOG_TAG, "avg-Find mean of: "+ Arrays.toString(valsarr));
            return StatUtils.mean(valsarr);
        }
        else
        {   //Log.i(LOG_TAG, "EmptyArr");
            return 0;
        }
    }

    public static double sum(ArrayList<Double> vals)
    {
        if(!vals.isEmpty())
        {
            Double[] d = new Double[vals.size()];
            vals.toArray(d);
            double[] valsarr = ArrayUtils.toPrimitive(d);
            return StatUtils.sum(valsarr);
        }
        else
        {   //Log.i(LOG_TAG, "EmptyArr");
            return 0;
        }

    }

    public static int calculateLightWaste(ArrayList<String> datetimesRoomIsEmpty)
    {
        int lightwaste=0;

        //Iterate through all the dates
        Iterator itrdates = datetimesRoomIsEmpty.iterator();
        while(itrdates.hasNext())
        {
            //Query for the times the light was ON and date was the same as when the room was empty
            //light waste = get +
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
        DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateStringFormat2 = DateTimeFormat.forPattern("yyyy-MM-dd 00:00:00");
        DateTime up_time = dateStringFormat2.parseDateTime(upperbound_date);
        DateTime low_time = dateStringFormat2.parseDateTime(lowerbound_date);

        Log.i(LOG_TAG, "add day: upp: "+upperbound_date+", low: "+lowerbound_date);

        up_time = up_time.plusDays(1);

        upperbound_date = dateStringFormat2.print(up_time);

        low_time = low_time.plusDays(1);

        lowerbound_date = dateStringFormat2.print(low_time);

        Log.i(LOG_TAG, "2 add day: upp: "+upperbound_date+", low: "+lowerbound_date);
    }
}
