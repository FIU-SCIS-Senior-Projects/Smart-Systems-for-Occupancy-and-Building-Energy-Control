package fiu.ssobec;

import android.content.Context;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.FastMath;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import fiu.ssobec.DataAccess.DataAccessUser;

/**
 * Created by Dalaidis on 3/17/2015.
 */
public class NaiveBayesTemperature {

    private double mean_itemp_low;
    private double mean_itemp_med;
    private double mean_itemp_high;
    private double sd_itemp_low;
    private double sd_itemp_med;
    private double sd_itemp_high;
    private double mean_otemp_low;
    private double mean_otemp_med;
    private double mean_otemp_high;
    private double sd_otemp_low;
    private double sd_otemp_med;
    private double sd_otemp_high;
    private int TEMP_LOW;
    private int TEMP_HIGH;
    private Context context;
    double probability_low;
    double probability_med;
    double probability_high;


    public NaiveBayesTemperature(Context context) {
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

        double[] values= new double[1];
        List<Integer> region_id = dataAccessUser.getAllZoneID();
        for (Integer id : region_id) {


            ArrayList<Double> allvals_itemp_low = dataAccessUser.getInsideTemperatureByZone(id, 0, TEMP_LOW);
            ArrayList<Double> allvals_itemp_med = dataAccessUser.getInsideTemperatureByZone(id, TEMP_LOW, TEMP_HIGH);
            ArrayList<Double> allvals_itemp_high = dataAccessUser.getInsideTemperatureByZone(id, TEMP_HIGH, 0);//Return temp range from temp high to no upper bound
            ArrayList<Double> allvals_otemp_low = dataAccessUser.getOutsideTemperatureByZone(id, 0, TEMP_LOW);
            ArrayList<Double> allvals_otemp_med = dataAccessUser.getOutsideTemperatureByZone(id, TEMP_LOW, TEMP_HIGH);
            ArrayList<Double> allvals_otemp_high = dataAccessUser.getOutsideTemperatureByZone(id, TEMP_HIGH, 0);//Return temp range from temp high to no upper bound

            mean_itemp_low = StatUtils.mean(ArrayUtils.toPrimitive(allvals_itemp_low.toArray(new Double[allvals_itemp_low.size()]))) ;
            mean_itemp_med = StatUtils.mean(ArrayUtils.toPrimitive(allvals_itemp_med.toArray(new Double[allvals_itemp_med.size()]))) ;
            mean_itemp_high = StatUtils.mean(ArrayUtils.toPrimitive(allvals_itemp_high.toArray(new Double[allvals_itemp_high.size()]))) ;
            mean_otemp_low = StatUtils.mean(ArrayUtils.toPrimitive(allvals_otemp_low.toArray(new Double[allvals_otemp_low.size()]))) ;
            mean_otemp_med = StatUtils.mean(ArrayUtils.toPrimitive(allvals_otemp_med.toArray(new Double[allvals_otemp_med.size()]))) ;
            mean_otemp_high = StatUtils.mean(ArrayUtils.toPrimitive(allvals_otemp_high.toArray(new Double[allvals_otemp_high.size()]))) ;

            sd_itemp_low = FastMath.sqrt(StatUtils.variance(ArrayUtils.toPrimitive(allvals_itemp_low.toArray(new Double[allvals_itemp_low.size()])))) ;
            sd_itemp_med = FastMath.sqrt(StatUtils.variance(ArrayUtils.toPrimitive(allvals_itemp_med.toArray(new Double[allvals_itemp_med.size()])))) ;
            sd_itemp_high = FastMath.sqrt(StatUtils.variance(ArrayUtils.toPrimitive(allvals_itemp_high.toArray(new Double[allvals_itemp_high.size()])))) ;
            sd_otemp_low = FastMath.sqrt(StatUtils.variance(ArrayUtils.toPrimitive(allvals_otemp_low.toArray(new Double[allvals_otemp_low.size()])))) ;
            sd_otemp_med = FastMath.sqrt(StatUtils.variance(ArrayUtils.toPrimitive(allvals_otemp_med.toArray(new Double[allvals_otemp_med.size()])))) ;
            sd_otemp_high = FastMath.sqrt(StatUtils.variance(ArrayUtils.toPrimitive(allvals_otemp_high.toArray(new Double[allvals_otemp_high.size()])))) ;

            double total = allvals_itemp_low.size() + allvals_itemp_med.size() + allvals_itemp_high.size();
            probability_low = allvals_itemp_low.size()/total;
            probability_med = allvals_itemp_med.size()/total;
            probability_high = allvals_itemp_high.size()/total;
        }


        dataAccessUser.close();
    }


    private double NormalDistribution(double x, double mean, double standardDeviation)
    {
        NormalDistribution nd = new NormalDistribution(mean, standardDeviation);
        return nd.density(x);
    }

    public void predict(int out_temp, int ins_temp){

        double normal_low_out = NormalDistribution(out_temp, mean_otemp_low, sd_otemp_low);
        double normal_med_out = NormalDistribution(out_temp, mean_otemp_med, sd_otemp_med);
        double normal_high_out = NormalDistribution(out_temp, mean_otemp_high, sd_otemp_high);
        double normal_low_ins = NormalDistribution(ins_temp, mean_itemp_low, sd_itemp_low);
        double normal_med_ins = NormalDistribution(ins_temp, mean_itemp_med, sd_itemp_med);
        double normal_high_ins = NormalDistribution(ins_temp, mean_itemp_high, sd_itemp_high);

        double P_low= normal_low_out*normal_low_ins* probability_low;
        double P_med= normal_med_out*normal_med_ins*probability_med;
        double P_high= normal_high_out*normal_high_ins*probability_high;

        double max=P_low;
        if (max<P_med)
            max=P_med;
        else if (max < P_high)
            max = P_high;

        
    }


}