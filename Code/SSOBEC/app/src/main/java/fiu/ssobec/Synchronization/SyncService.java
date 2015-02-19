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

        Log.i(LOG_TAG, "Service created woo");
        synchronized (sSyncAdapterLock){

        if (sSyncAdapter == null){
            sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
        }
    }

    }
    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link android.os.IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p/>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link android.content.Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
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
