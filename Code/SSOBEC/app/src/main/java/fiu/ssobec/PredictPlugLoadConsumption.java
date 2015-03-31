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
        for (int i=0; i < powerKw.size(); i++){

            totalConsumption = powerKw.get(i)*quantity.get(i)*hoursUse.get(i)*daysUse.get(i)+totalConsumption;
        }
        totalCost = totalConsumption * 0.12;

        System.out.println("Total Consumption is: "+totalConsumption+ " kWh");
    }

}
