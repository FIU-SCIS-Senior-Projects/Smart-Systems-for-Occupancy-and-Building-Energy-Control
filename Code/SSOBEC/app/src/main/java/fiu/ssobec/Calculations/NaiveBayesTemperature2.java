package fiu.ssobec.Calculations;

import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.FastMath;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fiu.ssobec.DataAccess.DataAccessUser;

/**
 * Created by Maria on 4/13/2015.
 */
public class NaiveBayesTemperature2 {

    public static final String LOG_TAG = "NaiveBayesTemperature";

    private double mean_diff_low;
    private double mean_diff_med;
    private double mean_diff_high;
    private double sd_diff_low;
    private double sd_diff_med;
    private double sd_diff_high;
    private double mean_otemp_low;
    private double mean_otemp_med;
    private double mean_otemp_high;
    private double sd_otemp_low;
    private double sd_otemp_med;
    private double sd_otemp_high;
    private int TEMP_LOW=54;
    private int TEMP_HIGH=56;
    private Context context;
    double probability_low;
    double probability_med;
    double probability_high;


    public NaiveBayesTemperature2(Context context) {
        this.context = context;
    }

    public void training()
    {
        DataAccessUser dataAccessUser = new DataAccessUser(context);

        try {
            dataAccessUser.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //get values from table
        //calculate mean and standard deviation for outside/inside temperature
        List<Integer> region_id = dataAccessUser.getAllZoneID();
        int id = region_id.get(0);

        ArrayList<Double> allvals_itemp_low = dataAccessUser.getInsideTemperatureByZone(id, 0, TEMP_LOW);
        ArrayList<Double> allvals_itemp_med = dataAccessUser.getInsideTemperatureByZone(id, TEMP_LOW, TEMP_HIGH);
        ArrayList<Double> allvals_itemp_high = dataAccessUser.getInsideTemperatureByZone(id, TEMP_HIGH, 0);//Return temp range from temp high to no upper bound
        ArrayList<Double> allvals_otemp_low = dataAccessUser.getOutsideTemperatureByZone(id, 0, TEMP_LOW);
        ArrayList<Double> allvals_otemp_med = dataAccessUser.getOutsideTemperatureByZone(id, TEMP_LOW, TEMP_HIGH);
        ArrayList<Double> allvals_otemp_high = dataAccessUser.getOutsideTemperatureByZone(id, TEMP_HIGH, 0);//Return temp range from temp high to no upper bound

        //Get all the differences
        ArrayList<Double> allvals_diffs_low = getDiffs(allvals_itemp_low, allvals_otemp_low);
        ArrayList<Double> allvals_diffs_med = getDiffs(allvals_itemp_med, allvals_otemp_med);
        ArrayList<Double> allvals_diffs_high = getDiffs(allvals_itemp_high, allvals_otemp_high);

        mean_diff_low = StatUtils.mean(ArrayUtils.toPrimitive(allvals_diffs_low.toArray(new Double[allvals_itemp_low.size()])));
        mean_diff_med = StatUtils.mean(ArrayUtils.toPrimitive(allvals_diffs_med.toArray(new Double[allvals_itemp_med.size()])));
        mean_diff_high = StatUtils.mean(ArrayUtils.toPrimitive(allvals_diffs_high.toArray(new Double[allvals_itemp_high.size()])));

        sd_diff_low = FastMath.sqrt(StatUtils.variance(ArrayUtils.toPrimitive(allvals_diffs_low.toArray(new Double[allvals_itemp_low.size()])))) ;
        sd_diff_med = FastMath.sqrt(StatUtils.variance(ArrayUtils.toPrimitive(allvals_diffs_med.toArray(new Double[allvals_itemp_med.size()])))) ;
        sd_diff_high = FastMath.sqrt(StatUtils.variance(ArrayUtils.toPrimitive(allvals_diffs_high.toArray(new Double[allvals_itemp_high.size()])))) ;

        double total = allvals_diffs_low.size() + allvals_diffs_med.size() + allvals_diffs_high.size();
        probability_low = allvals_diffs_low.size()/total;
        probability_med = allvals_diffs_med.size()/total;
        probability_high = allvals_diffs_high.size()/total;

        dataAccessUser.close();
    }

    private  ArrayList<Double> getDiffs(ArrayList<Double> allvals_itemp, ArrayList<Double> allvals_otemp)
    {
        ArrayList<Double> allvals_diffs = new ArrayList<>();

        if(allvals_itemp.size() != allvals_otemp.size())
            return null;

        for(int i=0; i<allvals_itemp.size(); i++)
        {
            allvals_diffs.add(Math.abs(allvals_itemp.get(i) - allvals_otemp.get(i)));
        }
        Log.i(LOG_TAG, "All Vals Diffs: "+allvals_diffs.toString());

        return allvals_diffs;
    }

    private double NormalDistribution(double x, double mean, double standardDeviation)
    {
        NormalDistribution nd = new NormalDistribution(mean, standardDeviation);
        return nd.density(x);
    }

    public String predict(int out_temp, int ins_temp){

        //double normal_low_out = NormalDistribution(out_temp, mean_otemp_low, sd_otemp_low);
        //double normal_med_out = NormalDistribution(out_temp, mean_otemp_med, sd_otemp_med);
        //double normal_high_out = NormalDistribution(out_temp, mean_otemp_high, sd_otemp_high);
        Log.i(LOG_TAG, "Outside temp: "+out_temp+" Inside temp: "+ins_temp+" Difference: "+Math.abs(out_temp - ins_temp));

        double normal_low_ins = NormalDistribution(Math.abs(out_temp - ins_temp), mean_diff_low, sd_diff_low);
        double normal_med_ins = NormalDistribution(Math.abs(out_temp - ins_temp), mean_diff_med, sd_diff_med);
        double normal_high_ins = NormalDistribution(Math.abs(out_temp - ins_temp), mean_diff_high, sd_diff_high);

        double P_low= normal_low_ins* probability_low;
        double P_med= normal_med_ins*probability_med;
        double P_high= normal_high_ins*probability_high;

        double max=P_low;
        String str_max ="LOW";
        if (max < P_med)
        {
            max=P_med;
            str_max = "MEDIUM";
        }
        else if (max < P_high)
        {
            max=P_high;
            str_max = "HIGH";
        }
        Log.i(LOG_TAG, "MAX: "+str_max+", P_LOW: "+P_low+", P_MED: "+P_med+", P_HIGH: "+P_high);

        return str_max;
    }

}
