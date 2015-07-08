package fiu.ssobec.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fiu.ssobec.AdaptersUtil.RewardListParent;
import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.DataAccess.ExternalDatabaseController;
import fiu.ssobec.Model.User;
import fiu.ssobec.R;
import fiu.ssobec.Synchronization.SyncUtils;

public class TurnLightOffActivity extends ActionBarActivity {
    public static final String LOG_TAG = "TurnOffLightActivity";
    public static final String TURNOFFLIGHT_PHP = "http://smartsystems-dev.cs.fiu.edu/turnofflight.php";
    private static DataAccessUser data_access;
    public static final int USER_LOGGEDIN = 1;
    int RewardPoints = 5; //Points awarded for turning off light in wasteful region

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_light_off);

    }

    public void turnOffLight(View view) {
        //Declare the access to the SQLite table for user
        data_access = new DataAccessUser(this);

        //Open the data access to the tables
        try {
            data_access.open();
        } catch (SQLException e) {
            System.err.println(LOG_TAG + ": " + e.toString());
            e.printStackTrace();
        }

        //Synchronize Data
        SyncUtils.CreateSyncAccount(this);
        SyncUtils.TriggerRefresh();

        User user = data_access.getUser(USER_LOGGEDIN); //Get a user that is currently logged in the system

        //If a user that is logged in into the system is not found then start a new LoginActivity
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        //User that is currently logged in is found
        else {
            int id = user.getId();
            int total = user.getRewards() + RewardPoints;
            String reward_description = "Turned Off Light";
            String user_id = id + "";
            String total_rewards = total + "";
            String reward_points = RewardPoints + "";

            List<NameValuePair> userInfo = new ArrayList<NameValuePair>(4);
            userInfo.add(new BasicNameValuePair("user_id", user_id.trim()));
            userInfo.add(new BasicNameValuePair("total_rewards", total_rewards.trim()));
            userInfo.add(new BasicNameValuePair("reward", reward_points.trim()));
            userInfo.add(new BasicNameValuePair("reward_description", reward_description.trim()));

            String res = "";

            //Send the user info to update the user_rewards table and user table with the added reward points
            try {
                res = new ExternalDatabaseController((ArrayList<NameValuePair>) userInfo, TURNOFFLIGHT_PHP).send();


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(res.equalsIgnoreCase("successful")){
                user.setRewards(total);
                Toast.makeText(getApplicationContext(), "Congrats on Saving Energy!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MyZonesActivity.class);
                startActivity(intent);
            }
            else{

                Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
            }

        }
    }
            @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_turn_light_off, menu);
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
}
