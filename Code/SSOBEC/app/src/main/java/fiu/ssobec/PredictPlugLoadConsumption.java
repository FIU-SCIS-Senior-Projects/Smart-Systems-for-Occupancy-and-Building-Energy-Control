package fiu.ssobec;

import java.util.ArrayList;

/**
 * Created by Dalaidis on 3/28/2015.
 */
public class PredictPlugLoadConsumption {

    private ArrayList<Double> powerKw;
    private ArrayList<Integer> quantity;
    private ArrayList<Integer> hoursUse;
    private ArrayList<Integer> daysUse;
    private double totalConsumption;
    private double totalCost;
    private double ENERGYCOST = 0.12;

    public PredictPlugLoadConsumption(ArrayList<Double> powerKw, ArrayList<Integer> quantity, ArrayList<Integer> hoursUse, ArrayList<Integer> daysUse) {
        this.powerKw = powerKw;
        this.quantity = quantity;
        this.hoursUse = hoursUse;
        this.daysUse = daysUse;
    }

    public double getTotalConsumption() {
        return totalConsumption;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void MonthlyConsumption() {

        System.out.println("Power Size: "+powerKw.size());
        System.out.println("Power KW: "+powerKw.toString());
        totalConsumption=0;
        for (int i=0; i < powerKw.size(); i++){

            totalConsumption = powerKw.get(i)*quantity.get(i)*hoursUse.get(i)*daysUse.get(i)+totalConsumption;
        }

        totalCost = totalConsumption * ENERGYCOST;

        System.out.println("Total Consumption is: "+totalConsumption+ " kWh");
    }

    public Double getApplConsumption(int p)
    {
        return  powerKw.get(p)*quantity.get(p)*hoursUse.get(p)*daysUse.get(p);
    }

    public Double getApplCost(int p)
    {
        return  getApplConsumption(p)*ENERGYCOST;
    }

}
