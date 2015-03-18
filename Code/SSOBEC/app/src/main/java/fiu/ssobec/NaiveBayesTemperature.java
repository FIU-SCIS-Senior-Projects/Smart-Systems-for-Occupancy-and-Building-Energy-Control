package fiu.ssobec;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.FastMath;

import java.util.ArrayList;

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


    private NaiveBayesTemperature stats;
    public NaiveBayesTemperature() {

        this.stats = new NaiveBayesTemperature();

    }

    public void training()
    {

        //get values from table
        //calculate mean and standard deviation for outside/inside temperature

        double[] values= new double[1];
        ArrayList<Double> allvals_itemp_low;
        ArrayList<Double> allvals_itemp_med;
        ArrayList<Double> allvals_itemp_high;
        ArrayList<Double> allvals_otemp_low;
        ArrayList<Double> allvals_otemp_med;
        ArrayList<Double> allvals_otemp_high;

        double mean = StatUtils.mean(values);
        double std = FastMath.sqrt(StatUtils.variance(values));

    }


    private double NormalDistribution(double x, double mean, double standardDeviation)
    {


        return 0.0;
    }

    public void predict(int out_temp, int ins_temp){

        double normal_low_out = NormalDistribution(out_temp, mean_otemp_low, sd_otemp_low);
        double normal_med_out = NormalDistribution(out_temp, mean_otemp_med, sd_otemp_med);
        double normal_high_out = NormalDistribution(out_temp, mean_otemp_high, sd_otemp_high);
        double normal_low_ins = NormalDistribution(ins_temp, mean_itemp_low, sd_itemp_low);
        double normal_med_ins = NormalDistribution(ins_temp, mean_itemp_med, sd_itemp_med);
        double normal_high_ins = NormalDistribution(ins_temp, mean_itemp_high, sd_itemp_high);

        double P_low= normal_low_out*normal_low_ins;
        double P_med= normal_med_out*normal_med_ins;
        double P_high= normal_high_out*normal_high_ins;

    }




}
