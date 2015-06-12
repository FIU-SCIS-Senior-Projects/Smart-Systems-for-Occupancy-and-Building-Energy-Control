package fiu.ssobec.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fiu.ssobec.Adapters.AppliancesAdapter;
import fiu.ssobec.Adapters.MyZoneListAdapter;
import fiu.ssobec.AdaptersUtil.PlugLoadListParent;
import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.DataAccess.ExternalDatabaseController;
import fiu.ssobec.Model.PlugLoad;
import fiu.ssobec.R;

public class ZoneAppliances extends ActionBarActivity implements NumberPicker.OnValueChangeListener {
    private DataAccessUser data_access;
    private Dialog d;
    private int zoneID;
    public static String EXTRA_ZONE_APPLIANCE_REGION_ID = "fiu.ssobec.ZoneAppliances.regionId";
    private static String ADDAPPLIANCE_PHP = "http://smartsystems-dev.cs.fiu.edu/addappliance.php";
    private static String LOG_TAG = "fiu.ssobec.ZoneAppliances";

    ArrayList<PlugLoadListParent> parents;
    ListView mListView;
    AppliancesAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        data_access = DataAccessUser.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone_appliances);

        data_access = DataAccessUser.getInstance(this);

        try {
            data_access.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();

        if(intent.getExtras() != null)
        {
            zoneID = intent.getIntExtra(EXTRA_ZONE_APPLIANCE_REGION_ID, 0);
        }

        parents = getPlugLoad();

        mListView = (ListView) findViewById(R.id.applianceslist_view);
        adapter = new AppliancesAdapter(this, parents);
        //myZoneListAdapter.setParents(parents);
        mListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_zone_appliances, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(item.getItemId()) {
            case R.id.action_addAppliance:
                show();
                return true;
            case R.id.action_settings:
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

        //Log.i("value is",""+newVal);

    }

    public void show()
    {

        d = new Dialog(ZoneAppliances.this);
        d.setTitle("Number of Windows");
        View view = findViewById(R.id.add_appliances_list);
        d.setContentView(R.layout.dialog_add_appliance);
        Button b1 = (Button) d.findViewById(R.id.cancel_btn);
        Button b2 = (Button) d.findViewById(R.id.add_btn);

        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //tvWindows.setText(String.valueOf(np.getValue()));
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
                addAppliance(v);
                //finishActivity(0);
            }
        });
        d.show();


    }


    //onClick Create Zone, create the new Zone with its attributes
    public void addAppliance(View view){
        List<NameValuePair> new_zone_info = new ArrayList<>(3);
        System.out.println("Create Zone button works");

        String appliance_name = ((EditText) d.findViewById(R.id.appliance_name)).getText().toString();
        String appliance_type = ((EditText) d.findViewById(R.id.appliance_type)).getText().toString();
        String appliance_status = ((EditText) d.findViewById(R.id.appliance_status)).getText().toString();
        String appliance_energy_usage = ((EditText) d.findViewById(R.id.appliance_energy_usage)).getText().toString();

        if(!appliance_name.isEmpty())
        {

            new_zone_info.add(new BasicNameValuePair("zone_description_region_id", String.valueOf(zoneID)));
            new_zone_info.add(new BasicNameValuePair("appliance_name",  appliance_name ));
            new_zone_info.add(new BasicNameValuePair("appliance_type", appliance_type));
            new_zone_info.add(new BasicNameValuePair("status", appliance_status));
            new_zone_info.add(new BasicNameValuePair("energy_usage_kwh", appliance_energy_usage));

            String res = "";
            //Create a new Zone
            try {
                res = new ExternalDatabaseController((ArrayList<NameValuePair>) new_zone_info,
                        ADDAPPLIANCE_PHP).send();

                Log.i(LOG_TAG, "Insert DB Result: " + res);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(res.equalsIgnoreCase("successful")){
//                Intent intent = new Intent(this, MyZonesActivity.class);
//                startActivity(intent);
            }
            else{

                Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
            }
            PlugLoadListParent appliance = new PlugLoadListParent();
            appliance.setName(appliance_name);
            parents.add(appliance);
//            ((AppliancesAdapter) (mListView.getAdapter() ).not

        }
        else
        {
        }


    }

    private ArrayList<PlugLoadListParent> getPlugLoad(){
        ArrayList<PlugLoadListParent> parents = data_access.getPlugLoadParentData(zoneID);
        if(parents.isEmpty())
        {
            Toast.makeText(getBaseContext(), "Not PlugLoadList", Toast.LENGTH_SHORT).show();
        }
        else {
            for(PlugLoadListParent plugLoad : parents){
                System.out.println(plugLoad.getName());
            }
        }
        return parents;
    }

}
