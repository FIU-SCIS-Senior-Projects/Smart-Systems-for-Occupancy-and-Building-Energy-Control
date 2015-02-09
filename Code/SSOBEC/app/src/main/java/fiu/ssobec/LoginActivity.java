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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;


import java.util.ArrayList;

import java.util.List;
import java.util.StringTokenizer;


public class LoginActivity extends ActionBarActivity {


    String login_email, password;
    List<NameValuePair> username_pass;

    int id=0;
    String name="", email="";


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
        login_email = ((EditText) findViewById(R.id.email_text_field)).getText().toString();
        password = ((EditText) findViewById(R.id.password_text_field)).getText().toString();
        System.out.println("This is login_email:" + login_email + ", Password" + password);

        //add our user name and password to an ArrayList
        username_pass = new ArrayList<NameValuePair>(2);

        //in PHP:
        // $login_email = $_POST['login_email'];
        // $password = $_POST['password'];
        username_pass.add(new BasicNameValuePair("login_email", login_email.toString().trim()));
        username_pass.add(new BasicNameValuePair("password", password.toString().trim()));

        //send the username and password to loginpost.php file
        String res = new Database((ArrayList<NameValuePair>) username_pass, "http://smartsystems-dev.cs.fiu.edu/loginpost.php").send();

        System.out.println("Response is: "+res);

        //
        if (userDetails(res)) {
            runOnUiThread(new Runnable() {

                public void run() {
                    //
                }

            });

            Intent intent = new Intent(this, MyZonesActivity.class);
            intent.putExtra(MyZonesActivity.USER_ID,id);
            startActivity(intent);
        }
        else {
            System.out.println("User Not Found...");
        }

    }

    public boolean userDetails(String response)
    {
        boolean user_flag = false;
        String str_before = "";
        StringTokenizer stringTokenizer = new StringTokenizer(response, ":");

        System.out.println("User Details");
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

        if(user_flag)
        {
            new User(name, id, email);
        }

        return user_flag;
    }


}