package fiu.ssobec.Synchronization;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Dalaidis on 2/17/2015.
 */
public class SyncService extends Service {
    private static SyncAdapter sSyncAdapter =null;
    private static final Object sSyncAdapterLock = new Object();
    public static final String LOG_TAG = "SyncService";


    public void onCreate(){

        Log.i(LOG_TAG, "Service created");
        synchronized (sSyncAdapterLock){

        if (sSyncAdapter == null){
            sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
        }
    }

    }
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
            }

    @Override
    /**
     * Logging-only destructor.
     */
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "Service destroyed");
    }
}
