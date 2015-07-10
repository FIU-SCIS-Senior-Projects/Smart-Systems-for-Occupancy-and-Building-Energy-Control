package fiu.ssobec.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.DataAccess.ExternalDatabaseController;
import fiu.ssobec.Model.User;
import fiu.ssobec.R;
import fiu.ssobec.Synchronization.SyncUtils;

public class SendNotificationActivity extends ActionBarActivity {

    public static final String EXTRA_REGION_NAME = "fiu.ssobec.SendNotificatioinActivity.extra_region_name";

    public static final String LOG_TAG = "SendNotificationActivity";
    private static final String SEND_NOTIFICATIONS_PHP = "http://smartsystems-dev.cs.fiu.edu/sendnotifications.php";
    public static final String GET_ALL_REGION_SUBSCRIBERS_PHP = "http://smartsystems-dev.cs.fiu.edu/getallregionsubscribers.php";

    private static DataAccessUser data_access;
    public static final int USER_LOGGEDIN = 1;

    private static final int REWARD_POINTS = 3;

    ArrayList<String> subscribers;
    String regionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);

        Intent intent = getIntent();
        regionName = intent.getStringExtra(EXTRA_REGION_NAME);


        subscribers = getSubscribers();
        ListView mListView = (ListView) findViewById(R.id.subscribers_list);

//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
//                this,
//                android.R.layout.simple_list_item_1,
//                subscribers );

        SubscriberAdapter adapter = new SubscriberAdapter(subscribers);

        mListView.setAdapter(adapter);


        Button sendNotificationsButotn = (Button) findViewById(R.id.sendNotications);
        sendNotificationsButotn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotifications(v);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_notification, menu);
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

    public ArrayList<String> getSubscribers(){

        ArrayList<String> subscribers = new ArrayList<>();

        data_access = new DataAccessUser(this);

        //Open the data access to the tables
        try {
            data_access.open();
        } catch (SQLException e) {
            System.err.println(LOG_TAG + ": " + e.toString());
            e.printStackTrace();
        }

        List<NameValuePair> userInfo = new ArrayList<NameValuePair>();

        userInfo.add(new BasicNameValuePair("region_name", regionName ));


        String res = "";

        //Send the user info to update the user_rewards table and user table with the added reward points
        try {
            res = new ExternalDatabaseController((ArrayList<NameValuePair>) userInfo, GET_ALL_REGION_SUBSCRIBERS_PHP).send();

            System.out.println("RES: " + res);
            if(res != null) {
                int j = 0;
                JSONObject obj = new JSONObject(res);
                JSONObject myobj;
                Log.i("AddZoneActivity", "" + obj.length());

                while (obj.has(j + "")) {
                    myobj = obj.getJSONObject(j + "");
                    String zone_name = myobj.getString("login_email");
                    subscribers.add(zone_name);
                    j++;
                }
            }
        }
        catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
        return subscribers;
    }

    public void sendNotifications(View view) {
        //Declare the access to the SQLite table for user


        //Synchronize Data
        SyncUtils.CreateSyncAccount(this);
        SyncUtils.TriggerRefresh();

        User user = data_access.getUser(USER_LOGGEDIN); //Get a user that is currently logged in the system

        int id = user.getId();
        int total = user.getRewards() + REWARD_POINTS;
        String reward_description = "Send Notifications";
        String user_id = id + "";
        String total_rewards = total + "";
        String reward_points = REWARD_POINTS + "";


        //If a user that is logged in into the system is not found then start a new LoginActivity
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        //User that is currently logged in is found
        else {
//            int id = user.getId();
//            int total = user.getRewards() + RewardPoints;
//            String reward_description = "Turned Off Light";
//            String user_id = id + "";
//            String total_rewards = total + "";
//            String reward_points = RewardPoints + "";

            List<NameValuePair> userInfo = new ArrayList<NameValuePair>();

            userInfo.add(new BasicNameValuePair("region_name", regionName ));
            userInfo.add(new BasicNameValuePair("login_email", user.getEmail()));

            userInfo.add(new BasicNameValuePair("user_id", user_id.trim()));
            userInfo.add(new BasicNameValuePair("total_rewards", total_rewards.trim()));
            userInfo.add(new BasicNameValuePair("reward", reward_points.trim()));
            userInfo.add(new BasicNameValuePair("reward_description", reward_description.trim()));

            String res = "";

            //Send the user info to update the user_rewards table and user table with the added reward points
            try {
                res = new ExternalDatabaseController((ArrayList<NameValuePair>) userInfo, SEND_NOTIFICATIONS_PHP).send();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(res.equalsIgnoreCase("successful")){
//                user.setRewards(total);
                Toast.makeText(getApplicationContext(), "Notifications Sent!\n 3 Points for Gryffindor!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MyZonesActivity.class);
                startActivity(intent);
            }
            else{

                Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
            }

        }
    }


    private class SubscriberAdapter extends ArrayAdapter<String> {
        public SubscriberAdapter(ArrayList<String> subscribers) {
            super(SendNotificationActivity.this, android.R.layout.simple_list_item_1, subscribers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // if we weren't given a view, inflate one
            if (null == convertView) {
                convertView = SendNotificationActivity.this.getLayoutInflater()
                        .inflate(R.layout.row_subscriber_email_list, null);
            }

            // configure the view for this Crime
            String c = subscribers.get(position);

            TextView titleTextView =
                    (TextView)convertView.findViewById(R.id.subscriber_list_item_titleTextView);
            titleTextView.setText(c);
//            TextView dateTextView =
//                    (TextView)convertView.findViewById(R.id.crime_list_item_dateTextView);
//            dateTextView.setText(c.getDate().toString());
            final CheckBox solvedCheckBox =
                    (CheckBox)convertView.findViewById(R.id.subscriber_list_item_checkBox);
            solvedCheckBox.setChecked(true);

            solvedCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    solvedCheckBox.setChecked(!solvedCheckBox.isChecked());
                }
            });

            return convertView;
        }
    }
}