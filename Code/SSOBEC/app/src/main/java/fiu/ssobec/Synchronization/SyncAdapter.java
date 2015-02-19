package fiu.ssobec.Synchronization;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import fiu.ssobec.Activity.ZonesDescriptionActivity;
import fiu.ssobec.DataAccess.Database;

/**
 * Created by Dalaidis on 2/17/2015.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {


    public static final String LOG_TAG = "SyncAdapter";

    /**
     * Creates an {@link android.content.AbstractThreadedSyncAdapter}.
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    /**
     * Perform a sync for this account. SyncAdapter-specific parameters may
     * be specified in extras, which is guaranteed to not be null. Invocations
     * of this method are guaranteed to be serialized.
     *
     * @param account    the account that should be synced
     * @param extras     SyncAdapter-specific parameters
     * @param authority  the authority of this sync request
     * @param provider   a ContentProviderClient that points to the ContentProvider for this
     *                   authority
     * @param syncResult SyncAdapter-specific parameters
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        //TODO: Put the data transfer code here.

        Log.i(LOG_TAG, "Beginning network synchronization");

        //put a small database code
        //Add the region id to the NameValuePair ArrayList;
        List<NameValuePair> regionId = new ArrayList<>(1);
        regionId.add(new BasicNameValuePair("region_id",(ZonesDescriptionActivity.regionID+"").toString().trim()));

        String res = "";
        try {
            res = new Database((ArrayList<NameValuePair>) regionId, "http://smartsystems-dev.cs.fiu.edu/occupancypost.php").send();
            System.out.println("Occupancy Response is: "+res);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.i(LOG_TAG, "Finishing network synchronization");

    }
}
