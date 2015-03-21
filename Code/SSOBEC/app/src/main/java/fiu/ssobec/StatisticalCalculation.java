package fiu.ssobec;

import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private int region_id;
    private static String currdatetime;
    private static String earliest_timestamp;
    private static String last_timestamp;
    static final long ONE_MINUTE_IN_MILLIS=60000;
    Context mcontext;
    DataAccessUser data_access;

    public StatisticalCalculation(Context context, DataAccessUser data_access)
    {
        mcontext = context;
        this.data_access = data_access;

        earliest_timestamp = data_access.getFirstTimeStamp();
        last_timestamp = data_access.getLastTimeStamp();

        Log.i(LOG_TAG, "earliest_timestamp: "+earliest_timestamp);
        Log.i(LOG_TAG, "last_timestamp: "+last_timestamp);
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

        /*currdate = date+1;
        * */
        List<Integer> region_id = data_access.getAllZoneID();

        getDateWithoutTime(earliest_timestamp);
        Log.i(LOG_TAG, "Curr Date Time: "+currdatetime);
        for (Integer id : region_id) {

            //Average of temperature in a day
            inside_temp_avg = avg(data_access.getAllTemperatureBefore(id, currdatetime));

            Log.i(LOG_TAG, "Average Inside Temperature: "+inside_temp_avg);
            Log.i(LOG_TAG, "Array of Temperature: "+data_access.getAllTemperatureBefore(id, currdatetime).toString());

            //How many hours in a day were the lights on
            lighting_time_avg = data_access.getHowLongAreLightsON(id, currdatetime);

            Log.i(LOG_TAG, "Lighting Time Avg: "+lighting_time_avg);


            //How much energy was used by light in that day
            lighting_energyusage = (int) sum(data_access.getAllLightingEnergyUsageBefore(id, currdatetime));

            Log.i(LOG_TAG, "Lighting Energy Usage: "+lighting_energyusage);

            //What is the average occupancy in a room in a day
            occup_time_avg = avg(data_access.getAllOccupancyBefore(id, currdatetime));

            Log.i(LOG_TAG, "Occupancy Average: "+occup_time_avg);

             /*
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

        data_access.close();

    }

    public static double avg(ArrayList<Double> vals)
    {
        if(!vals.isEmpty())
        {
            Double[] d = new Double[vals.size()];
            vals.toArray(d);
            double[] valsarr = ArrayUtils.toPrimitive(d);
            Log.i(LOG_TAG, "avg-Find mean of: "+valsarr);
            return StatUtils.mean(valsarr);
        }
        else
        {   Log.i(LOG_TAG, "EmptyArr");
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
        {   Log.i(LOG_TAG, "EmptyArr");
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

    public void getDateWithoutTime(String datetime)
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date result=null;
        try {
            result =  df.parse(earliest_timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date resulttemp = result;
        //result =new Date(resulttemp.getTime() + (1440 * ONE_MINUTE_IN_MILLIS));

        currdatetime = df.format(result);
        DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateStringFormat2 = DateTimeFormat.forPattern("yyyy-MM-dd 00:00:00");
        DateTime time = dateStringFormat.parseDateTime(datetime);

        time.plusDays(2);

        String date = dateStringFormat2.print(time);

        currdatetime = date;
               //MutableDateTime mutableDateTime = time.toMutableDateTime();
        Log.i(LOG_TAG, "Datetime: "+datetime+", Datetime+1: "+date);


    }
}
