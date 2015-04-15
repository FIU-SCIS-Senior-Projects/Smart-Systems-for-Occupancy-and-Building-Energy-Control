package fiu.ssobec;

import android.support.test.espresso.action.ViewActions;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import fiu.ssobec.Activity.LoginActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Maria on 4/13/2015.
 */
public class SyncAdapterTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    public SyncAdapterTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @MediumTest
    public void testAdapter() {

        onView(withId(R.id.email_text_field)).perform(ViewActions.typeText("mandy@yahoo.com"));
        onView(withId(R.id.password_text_field)).perform(ViewActions.typeText("mandy123"));
        onView(withId(R.id.login_button)).perform(click());


        try {
            Thread.sleep(55000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
