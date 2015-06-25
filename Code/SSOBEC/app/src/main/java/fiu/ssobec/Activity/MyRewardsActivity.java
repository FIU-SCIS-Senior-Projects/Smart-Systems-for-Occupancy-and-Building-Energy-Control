package fiu.ssobec.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fiu.ssobec.Adapters.MyRewardListAdapter;
import fiu.ssobec.AdaptersUtil.RewardListParent;
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
            user_id = user.getId();
            rewards = user.getRewards();

            List<NameValuePair> userId = new ArrayList<>(1);
            userId.add(new BasicNameValuePair("user_id", (user_id+"").trim()));

            String res = null;
            ArrayList<RewardListParent> rewards_list = new ArrayList<RewardListParent>();

            //Send the user ID to getrewards.php and get the records for the user account rewards from user_rewards table
            try {
                res = new ExternalDatabaseController((ArrayList<NameValuePair>) userId, GETREWARDS_PHP).send();

                Log.i(LOG_TAG, "Rewards Table: " + res);

                JSONObject obj;

                try {
                    obj = new JSONObject(res);
                    JSONObject myobj;
                    int j=0;
                    while (obj.has(j + "") && j < obj.length()) {
                        myobj = obj.getJSONObject(j + "");
                        System.out.println("length "+obj.length());
                        String time_stamp = myobj.getString("time_stamp");
                        String description = myobj.getString("description");
                        int points = myobj.getInt("points");

                        RewardListParent record = new RewardListParent();
                        record.setName(description);
                        record.setDescription(time_stamp);
                        record.setPoints("+"+ points);
                        rewards_list.add(record);

                       j++;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            setContentView(R.layout.activity_my_rewards);
            TextView points = (TextView) findViewById(R.id.total_points);
            points.setText("" + rewards);
            System.out.println(LOG_TAG + " User ID: " + user_id + " Total points: " + rewards);

            String name = user.getEmail();
            if (!name.isEmpty()){
                TextView username = (TextView)findViewById(R.id.my_rewards);
                username.setText(name + "'s Rewards");
            }

            ListView listview_rewards = (ListView) findViewById(R.id.rewards_table);
            MyRewardListAdapter myRewardListAdapter = new MyRewardListAdapter(this);
            myRewardListAdapter.setParents(rewards_list);
            listview_rewards.setAdapter(myRewardListAdapter);

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
