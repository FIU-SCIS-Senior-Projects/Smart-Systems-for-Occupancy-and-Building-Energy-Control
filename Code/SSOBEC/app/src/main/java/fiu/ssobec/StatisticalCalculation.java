package fiu.ssobec;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public void calculateData(Context context)
    {
        DataAccessUser data_access = new DataAccessUser(context);

        //AC_energy_Average
        int ac_energy_usage;
        ArrayList<Map<String, String>> acenergylist = new ArrayList<>();
        acenergylist = data_access.getAllACStateByZoneID(1);

        List<Integer> region_id = data_access.getAllZoneID();
        for (Integer id : region_id) {
            //Average Temperature
            ArrayList<Map<String, Integer>> temperaturelist = new ArrayList<>();
            temperaturelist = data_access.getAllTemperatureByZoneID(id);

            //Average Lighting
            ArrayList<Map<String, String>> lightstatelist = new ArrayList<>();
            lightstatelist = data_access.getAllLightingStateByZoneID(id);

            //plug_load
            ArrayList<Map<String, Integer>> plugloadlist = new ArrayList<>();
            plugloadlist = data_access.getAllPlugLoadEnergy(id);

            //Occupancy avg Time that is empty


            //TODO: Calculate Average of temperature

            //TODO: Calculate Average Time lighting is ON
        }


        //GET Occupancy and Lighting at the SAME time and loop through them

        //TODO: Get all the occupancy data from dates that are NOT today

        //TODO: Calculate average occupancy each day and save it in the DB

        //TODO: Get all lighting data for statistics

        //TODO: Calculate average time lighting was ON

        //TODO: Calculate the total energy consumed in a day

        //TODO: When the Light was ON, how was the occupancy? (add up all the energy when occupancy was out)

        //TODO: Get all the data on temperature

    }

    //TODO: public int average(List)


}
