package fiu.ssobec.Synchronization;

/**
 * Created by Maria on 2/18/2015.
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import fiu.ssobec.Synchronization.DataSync.AuthenticatorService;

/**
 * Static helper methods for working with the sync framework.
 */
public class SyncUtils {


    public static final String LOG_TAG = "SyncUtils";
    //public static final long SECONDS_PER_MINUTE = 30L;
    //public static final long SYNC_INTERVAL_IN_MINUTES = 1L;
    public static final long SYNC_INTERVAL = 60*60*24;

    private static final String CONTENT_AUTHORITY = SyncConstants.AUTHORITY;
    private static final String ACCOUNT_TYPE = SyncConstants.ACCOUNT_TYPE;
    private static final String MY_ACCOUNT = SyncConstants.ACCOUNT;

    private static final String PREF_SETUP_COMPLETE = SyncConstants.PREF_SETUP_COMPLETE;

    /**
     * Create an entry for this application in the system account list, if it isn't already there.
     *
     * @param context Context
     */

    public static void CreateSyncAccount(Context context) {
        boolean newAccount = false;
        boolean setupComplete = PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(PREF_SETUP_COMPLETE, false);

        Log.i(LOG_TAG,"CreateSyncAccount: Create account, if it's missing");
        // Create account, if it's missing. (Either first run, or user has deleted account.)
        Account account = AuthenticatorService.GetAccount(ACCOUNT_TYPE);
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, CONTENT_AUTHORITY, 1);
            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, CONTENT_AUTHORITY, true);
            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            ContentResolver.addPeriodicSync(
                    account, CONTENT_AUTHORITY, new Bundle(),SYNC_INTERVAL);
            newAccount = true;

            Log.i(LOG_TAG,"CreateSyncAccount: add Account Explicitly");
        }

        // Schedule an initial sync if we detect problems with either our account or our local
        // data has been deleted. (Note that it's possible to clear app data WITHOUT affecting
        // the account list, so wee need to check both.)
        if (newAccount || !setupComplete) {
            TriggerRefresh();
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putBoolean(PREF_SETUP_COMPLETE, true).commit();
        }

    }


    public static void TriggerRefresh() {
        Bundle b = new Bundle();
        // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!

        Log.i(LOG_TAG, "Trigger Refresh");
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(
                AuthenticatorService.GetAccount(ACCOUNT_TYPE),  // Sync account
                CONTENT_AUTHORITY,                              // Content authority
                b);                                             // Extras
    }
}
