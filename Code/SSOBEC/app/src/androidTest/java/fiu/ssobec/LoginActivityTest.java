package fiu.ssobec;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.EditText;

import fiu.ssobec.Activity.LoginActivity;

/**
 * Created by Fresa on 2/7/2015.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private LoginActivity mLoginActivity;
    private EditText mEmailField;
    private EditText mPasswordField;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mLoginActivity = getActivity();
        mEmailField = (EditText) mLoginActivity.findViewById(R.id.email_text_field);
        mPasswordField = (EditText) mLoginActivity.findViewById(R.id.password_text_field);
    }

    public void testPreconditions() {
        assertNotNull(mEmailField);
        assertNotNull(mPasswordField);
    }

    @MediumTest
    public void testText() {
        // simulate user action to input some value into EditText:
        mLoginActivity.runOnUiThread(new Runnable() {
            public void run() {
                mEmailField.setText("hello");
                mPasswordField.setText("hello123");
            }
        });

        // Check if the EditText is properly set:
        assertEquals("hello", mEmailField.getText());
        assertEquals("hello123", mPasswordField.getText());
    }
}
