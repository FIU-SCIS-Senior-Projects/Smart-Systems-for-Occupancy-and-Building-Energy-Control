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

    //Data is given every hour.
    private static int DB_TIME_INTERVAL = 1;

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
        double lighting_energyusage=0;
        double lighting_energywaste=0;
        double plugload_energyusage=0;
        double occup_time_avg=0;
        double outside_temp_avg=0;

        int plugload_energywaste=0;
        int ac_energyusage=0;

        boolean there_is_data = true;
        List<Integer> region_id = mdata_access.getAllZoneID();

        upperbound_date = earliest_timestamp;


        while(there_is_data)
        {
            there_is_data = false;

            getDateWithoutTime(upperbound_date);
            Log.i(LOG_TAG, "Original Timestamp: "+earliest_timestamp+", Upper: "+upperbound_date+", Lower: "+lowerbound_date);

            for (Integer id : region_id) {

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
                plugload_energyusage = mdata_access.getAllPlugLoadEnergyBefore(id, upperbound_date, lowerbound_date);
                Log.i(LOG_TAG, "Plug load Energy Usage: "+plugload_energyusage);

                //Average of outside temperature in a day
                outside_temp_avg = avg(mdata_access.getAllTemperatureOnDateInterval(id, upperbound_date, lowerbound_date));


                if(inside_temp_avg+lighting_time_avg+lighting_energyusage+lighting_energywaste+plugload_energyusage
                        +plugload_energywaste+ac_energyusage+occup_time_avg > 0)
                {
                    //save it in the database
                    mdata_access.createStat(id,getDateFromDateTime(upperbound_date), inside_temp_avg, lighting_time_avg,
                    lighting_energyusage, lighting_energywaste, plugload_energyusage,
                    plugload_energywaste, ac_energyusage, occup_time_avg, outside_temp_avg);
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
        {
            return 0;
        }

    }

    public int calculateLightWaste(int region_id, ArrayList<String> datetimesRoomIsEmpty)
    {
        int lightwaste=0;

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
}
