package fiu.ssobec.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.Random;

import fiu.ssobec.Calculations.NaiveBayesTemperature;
import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.R;
import pl.droidsonroids.gif.GifImageView;

public class ACConsumptionPrediction extends ActionBarActivity {


    int outside_temperature;
    private DataAccessUser data_access;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acconsumption_prediction);

        data_access = new DataAccessUser(this);

        try {
            System.out.println("Open data access");
            data_access.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        outside_temperature = data_access.getOutsideTemperature();
        ((TextView) findViewById(R.id.forecast_temperature_val)).setText(""+outside_temperature);

        Random randomno = new Random();

        // check next int value
        int n = randomno.nextInt(2);

        GifImageView gifImageView = (GifImageView) findViewById(R.id.animated_edu);
        if(n == 1)
        {
            gifImageView.setImageResource(getResources().getIdentifier("@drawable/coolingeducation2", null, getPackageName()));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_acconsumption_prediction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    String res;
    public void predictTemperatureEnergy(View view)
    {
        NaiveBayesTemperature mnaive = new NaiveBayesTemperature(this);
        mnaive.training();
        String inside_temp = ((EditText) findViewById(R.id.today_temperature_textfield)).getText().toString();

        res = mnaive.predict(outside_temperature, Integer.parseInt(inside_temp));

         runOnUiThread(new Runnable() {

             public void run() {
                 ((TextView) findViewById(R.id.prediction_result)).setText(res);
             }

         });

    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            data_access.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        data_access.close();
    }
}
