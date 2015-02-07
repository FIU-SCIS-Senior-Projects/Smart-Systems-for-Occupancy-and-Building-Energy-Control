package fiu.ssobec;


import android.content.Intent;

import android.support.v7.app.ActionBarActivity;

import android.os.Bundle;

import android.view.Menu;

import android.view.MenuItem;

import android.view.View;

import android.widget.EditText;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import java.util.ArrayList;

import java.util.List;


public class LoginActivity extends ActionBarActivity {


    String email, password;
    HttpPost httppost;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> username_pass;


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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


    //login_pass Button onClick event
    public void LoginPost(View view) throws InterruptedException {
        email = ((EditText) findViewById(R.id.email_text_field)).getText().toString();
        password = ((EditText) findViewById(R.id.password_text_field)).getText().toString();
        System.out.println("This is email:" + email + ", Password" + password);

        //add our user name and password to an ArrayList
        username_pass = new ArrayList<NameValuePair>(2);

        //in PHP:
        // $email = $_POST['email'];
        // $password = $_POST['password'];
        username_pass.add(new BasicNameValuePair("email", email.toString().trim()));
        username_pass.add(new BasicNameValuePair("password", password.toString().trim()));

        //send the username and password to loginpost.php file
        String res = new Database((ArrayList<NameValuePair>) username_pass, "http://smartsystems-dev.cs.fiu.edu/loginpost.php").send();

        System.out.println("Response is: "+res);
        if (res.equalsIgnoreCase("User Found")) {
            runOnUiThread(new Runnable() {

                public void run() {
                    //
                }

            });

            Intent intent = new Intent(this, MyZonesActivity.class);
            startActivity(intent);
        }
        else {
            System.out.println("User Not Found...");
        }

    }


}