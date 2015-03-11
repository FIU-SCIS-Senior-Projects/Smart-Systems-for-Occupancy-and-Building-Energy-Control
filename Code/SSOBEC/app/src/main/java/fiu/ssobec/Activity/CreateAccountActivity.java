package fiu.ssobec.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import fiu.ssobec.DataAccess.ExternalDatabaseController;
import fiu.ssobec.R;

public class CreateAccountActivity extends ActionBarActivity {

    public static final String LOG_TAG = "CreateAccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_account, menu);
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
    public void registerUser (View view){

        List<NameValuePair> new_user_info = new ArrayList<>(3);
        System.out.println("This button is working");

        String name = ((EditText) findViewById(R.id.first_name_field)).getText().toString()+""
                +((EditText) findViewById(R.id.last_name_field)).getText().toString();

        String password = ((EditText) findViewById(R.id.password_field)).getText().toString();
        String login_email = ((EditText) findViewById(R.id.email_field)).getText().toString();

        new_user_info.add(new BasicNameValuePair("name", name.trim()));
        new_user_info.add(new BasicNameValuePair("password", password.trim()));
        new_user_info.add(new BasicNameValuePair("login_email", login_email.trim()));

        //Create a new User Account

        try {
            String res = new ExternalDatabaseController((ArrayList<NameValuePair>) new_user_info,
                    "http://smartsystems-dev.cs.fiu.edu/createaccount.php").send();

            //Printing the result
            System.out.println(res);
            Log.i(LOG_TAG, "DB Result: " + res);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }
}
