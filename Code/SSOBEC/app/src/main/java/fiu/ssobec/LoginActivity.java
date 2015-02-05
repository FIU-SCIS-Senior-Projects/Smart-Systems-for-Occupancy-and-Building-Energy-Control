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

    public void LoginPost(View view)
    {
        email = ((EditText) findViewById(R.id.email_text_field)).getText().toString();
        password = ((EditText) findViewById(R.id.password_text_field)).getText().toString();
        System.out.println("This is email:" + email + ", Password" + password);
        new Thread(new Runnable() {

            public void run() {
                login_pass();
            }

        }).start();

    }


    //TODO: Generalize this method
    //TODO: Create an interface for constants and list of links to the database
    void login_pass() {

        try {

            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://smartsystems-dev.cs.fiu.edu/loginpost.php"); // make sure the url is correct.

            //add our user name and password to an arraylist
            username_pass = new ArrayList<NameValuePair>(2);

            //names of variables must be same as in the php file
            username_pass.add(new BasicNameValuePair("email", email.toString().trim()));

            //in PHP:
            // $email = $_POST['email'];
            // $password = $_POST['password'];
            username_pass.add(new BasicNameValuePair("password", password.toString().trim()));

            httppost.setEntity(new UrlEncodedFormEntity(username_pass));

            //Execute HTTP Post Request
            response = httpclient.execute(httppost);

            // edited by James from coderzheaven.. from here....
            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            final String response = httpclient.execute(httppost, responseHandler);

            System.out.println("Response : " + response);

            runOnUiThread(new Runnable() {

                public void run() {
                    System.out.println("Response from PHP : " + response);
                    //dialog.dismiss();
                }

            });


            if (response.equalsIgnoreCase("User Found")) {
                runOnUiThread(new Runnable() {

                    public void run() {
                        //
                    }

                });
                Intent intent = new Intent(this, MyZonesActivity.class);
                startActivity(intent);
            }
            else {
                System.out.println("Something happened");
            }



        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());

        }

    }


}