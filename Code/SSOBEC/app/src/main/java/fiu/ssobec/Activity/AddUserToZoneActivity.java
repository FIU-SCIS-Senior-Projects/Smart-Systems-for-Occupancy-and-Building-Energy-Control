package fiu.ssobec.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import fiu.ssobec.AdaptersUtil.ZoneListParent;
import fiu.ssobec.DataAccess.ExternalDatabaseController;
import fiu.ssobec.R;

public class AddUserToZoneActivity extends ActionBarActivity {

    public static final String LOG_TAG = "fiu.ssobec.AddUserToZoneActivity";
    public static final String ADDUSERTOZONE_PHP = "http://smartsystems-dev.cs.fiu.edu/addusertozone.php";
    public static final String EXTRA_ZONE_ID = "fiu.ssobec.Activity.extra_zone_id";

    private ZoneListParent zone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_to_zone);

        Intent i = getIntent();
        this.zone = (ZoneListParent)i.getSerializableExtra(EXTRA_ZONE_ID);

        Button addUserButton = (Button) findViewById(R.id.add_user_button);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUserToZone(v);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_user_to_zone, menu);
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


    //onClick Create Zone, create the new Zone with its attributes
    public void addUserToZone(View view){
        List<NameValuePair> new_zone_info = new ArrayList<>(3);

        int zone_id = zone.getZone_id();

        String login_email = ((EditText) findViewById(R.id.et_user_email)).getText().toString();


        if(!login_email.isEmpty())
        {

            new_zone_info.add(new BasicNameValuePair("login_email", String.valueOf(login_email)));
            new_zone_info.add(new BasicNameValuePair("region_id", String.valueOf(zone_id)));

            String res = "";
            //Create a new Zone
            try {
                res = new ExternalDatabaseController((ArrayList<NameValuePair>) new_zone_info,
                        ADDUSERTOZONE_PHP).send();

                Log.i(LOG_TAG, "Insert DB Result: " + res);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(res.equalsIgnoreCase("successful")){
                Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(this, MyZonesActivity.class);
//                startActivity(intent);
                //finish();
            }
            else{

                Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
        }

    }
}
