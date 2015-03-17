package fiu.ssobec;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;
import android.widget.EditText;

import fiu.ssobec.Activity.LoginActivity;

/**
 * Created by Fresa on 2/7/2015.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private LoginActivity mLoginActivity;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mLoginButton;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mLoginActivity = getActivity();
        mEmailField = (EditText) mLoginActivity.findViewById(R.id.email_text_field);
        mPasswordField = (EditText) mLoginActivity.findViewById(R.id.password_text_field);
        mLoginButton = (Button) mLoginButton.findViewById(R.id.login_button);
    }

    public void testPreconditions() {
        assertNotNull(mEmailField);
        assertNotNull(mPasswordField);
        assertNotNull(mLoginButton);
    }

    @MediumTest
    public void testText() {
        // simulate user action to input some value into EditText:
        mLoginActivity.runOnUiThread(new Runnable() {
            public void run() {
                mEmailField.setText("mandy@yahoo.com");
                mPasswordField.setText("mandy123");
                mLoginButton.performClick();
            }
        });

        getInstrumentation().waitForIdleSync();
        // Check if the EditText is properly set:
        //assertEquals("mandy@yahoo.com", mEmailField.getText());
        //assertEquals("mandy123", mPasswordField.getText());
    }
}
