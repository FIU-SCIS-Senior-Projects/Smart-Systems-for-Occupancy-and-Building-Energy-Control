package fiu.ssobec;

import java.util.ArrayList;

/**
 * Created by Dalaidis on 3/28/2015.
 */
public class PredictPlugLoadConsumption {

    private ArrayList<Integer> powerKw;
    private ArrayList<Integer> quantity;
    private ArrayList<Integer> hoursUse;
    private ArrayList<Integer> daysUse;

    public PredictPlugLoadConsumption(ArrayList<Integer> powerKw, ArrayList<Integer> quantity, ArrayList<Integer> hoursUse, ArrayList<Integer> daysUse) {
        this.powerKw = powerKw;
        this.quantity = quantity;
        this.hoursUse = hoursUse;
        this.daysUse = daysUse;
    }

    public void MonthlyConsumption() {

    int totalConsumption=0;

        for (int i=0; i>powerKw.size(); i++){
            totalConsumption+=powerKw.get(i)*quantity.get(i)*hoursUse.get(i)*daysUse.get(i);
        }

        System.out.println("Total Contumption is: "+totalConsumption+ " kWh");
    }
}
