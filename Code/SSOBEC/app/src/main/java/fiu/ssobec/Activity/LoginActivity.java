package fiu.ssobec.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.DataAccess.ExternalDatabaseController;
import fiu.ssobec.R;


public class LoginActivity extends ActionBarActivity {


    public static final String LOG_TAG = "LoginActivity";
    public static final String LOGIN_PHP = "http://smartsystems-dev.cs.fiu.edu/loginpost.php";
    String login_email, password;
    List<NameValuePair> username_pass;
    private DataAccessUser data_access;
    TextView warning_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        data_access = new DataAccessUser(this);

        try {
            System.out.println("Open data access");
            data_access.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        warning_msg = (TextView) findViewById(R.id.warning_text_view);

    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;

    }*/

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

    //Login Button has been clicked
    public void LoginPost(View view) throws InterruptedException {

        //get the email and password from the text fields
        login_email = ((EditText) findViewById(R.id.email_text_field)).getText().toString();
        password = ((EditText) findViewById(R.id.password_text_field)).getText().toString();

       //declare an arraylist that holds email and password
        username_pass = new ArrayList<NameValuePair>(2);

        //the PHP file will receive the information as follows:
        // $login_email = $_POST['login_email'];
        // $password = $_POST['password'];
        username_pass.add(new BasicNameValuePair("login_email", login_email.trim()));
        username_pass.add(new BasicNameValuePair("password", password.trim()));

        //send the username and password to loginpost.php file
        //save the response from the database in a string
        String res = new ExternalDatabaseController((ArrayList<NameValuePair>) username_pass, LOGIN_PHP).send();

        System.out.println("Response is: "+res);

        //in user Details, check that the response were user details and save those
        //details in our internal SQLite database
        if (userDetails(res)) {
            runOnUiThread(new Runnable() {

                public void run() {
                    warning_msg.setText("");
                }

            });

            //Start MyZonesActivity
            Intent intent = new Intent(this, MyZonesActivity.class);
            Log.i(LOG_TAG, "Start My Zones Activity");
            startActivity(intent);
        }
        else
        {
            runOnUiThread(new Runnable() {

                public void run() { warning_msg.setText("Wrong email or password");
                }

            });
        }
    }

    public boolean userDetails(String response)
    {
        String name="";
        String email="";
        boolean user_flag = false;
        String str_before = "";
        StringTokenizer stringTokenizer = new StringTokenizer(response, ":");
        int id=0;

        while (stringTokenizer.hasMoreElements()) {

            String temp = stringTokenizer.nextElement().toString();
            if (str_before.equalsIgnoreCase("id"))
            {
                System.out.println("id: "+temp);
                user_flag = true;
                id = Integer.parseInt(temp);
            }
            else if (str_before.equalsIgnoreCase("name"))
            {
                System.out.println("name: "+temp);
                name = temp;
            }
            else if (str_before.equalsIgnoreCase("login_email"))
            {
                System.out.println("login_email: "+temp);
                email = temp;
            }

            str_before = temp;
        }

        //Create new user. LoggedIn is equal 1 to certified that the user is loggedIn.
        if(user_flag && (data_access.userExist(id) == null))
        {
            data_access.createUser(name, id, email);
        }
        //If the user exists, declare that the user has logged in, into the system.
        else if (data_access.userExist(id) != null)
            data_access.userLogin(id);

        return user_flag;
    }

    // OnClick Create Account Button
    public void getCreateAccount(View view) {
        Intent intent = new Intent(this,CreateAccountActivity.class);
        startActivity(intent);
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