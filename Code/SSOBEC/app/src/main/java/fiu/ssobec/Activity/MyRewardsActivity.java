package fiu.ssobec.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.DataAccess.ExternalDatabaseController;
import fiu.ssobec.Model.User;
import fiu.ssobec.R;
import fiu.ssobec.Synchronization.SyncUtils;

//Created by Diana June 2015
public class MyRewardsActivity extends ActionBarActivity {

    public static final String LOG_TAG = "MyRewardsActivity";
    public static final String GETREWARDS_PHP = "http://smartsystems-dev.cs.fiu.edu/getrewards.php";
    private static DataAccessUser data_access;
    public static int user_id;
    public static int rewards;
    public static final int USER_LOGGEDIN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Declare the access to the SQLite table for user
        data_access = new DataAccessUser(this);

        //Open the data access to the tables
        try {
            data_access.open();
        }
        catch (SQLException e) {
            System.err.println(LOG_TAG + ": " + e.toString());
            e.printStackTrace();
        }

        //Synchronize Data
        SyncUtils.CreateSyncAccount(this);
        SyncUtils.TriggerRefresh();

        User user = data_access.getUser(USER_LOGGEDIN); //Get a user that is currently logged in the system

        //If a user that is logged in into the system is not found then start a new LoginActivity
        if(user == null)
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        //User that is currently logged in is found
        else {
            this.user_id = user.getId();
            this.rewards = user.getRewards();
            List<NameValuePair> user_info = new ArrayList<NameValuePair>(1);
            user_info.add(new BasicNameValuePair("user_id", "" + user_id));

            String res = null;
            try {
                res = new ExternalDatabaseController((ArrayList<NameValuePair>) user_info, GETREWARDS_PHP).send();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Response is: "+res);


            setContentView(R.layout.activity_my_rewards);
            TextView points = (TextView) findViewById(R.id.total_points);
            points.setText(""+rewards);

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_rewards, menu);
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
