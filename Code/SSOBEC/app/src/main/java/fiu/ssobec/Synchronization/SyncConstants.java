package fiu.ssobec.Synchronization;

/**
 * Created by Maria on 2/18/2015.
 */
public interface SyncConstants {


    // Constants
    // The authority for the sync adapter's content provider
    static final String AUTHORITY = "fiu.ssobec.Synchronization.DataSync";
    // An account type, in the form of a domain name
    static final String ACCOUNT_TYPE = "ssobec.fiu";
    // The account name
    static final String ACCOUNT = "myaccount";

    static final String PREF_SETUP_COMPLETE = "setup_complete";
}
