package fiu.ssobec;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import fiu.ssobec.Activity.LoginActivity;

/**
 * Created by Fresa on 2/7/2015.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    /**
     * Creates an {@link android.test.ActivityInstrumentationTestCase2}.
     *
     * @param pkg           ignored - no longer in use.
     * @param activityClass The activity to test. This must be a class in the instrumentation
     *                      targetPackage specified in the AndroidManifest.xml
     */

    private LoginActivity mLoginActivity;
    private EditText mLoginActivityEdit;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mLoginActivity = getActivity();
        mLoginActivityEdit = (EditText) mLoginActivity.findViewById(R.id.email_text_field);
    }

    public void testPreconditions() {
        assertNotNull("mLoginActivity is null", mLoginActivity);
        assertNotNull("mFirstTestText is null", mLoginActivityEdit);
    }

    public void testmLoginActivityEmailEdit() {

        //final String expected = mLoginActivity.getString(R.id.email_text_field);
        final String actual = mLoginActivityEdit.getText().toString();
        //assertEquals("mLoginActivityEdit", expected, actual);

    }
}
