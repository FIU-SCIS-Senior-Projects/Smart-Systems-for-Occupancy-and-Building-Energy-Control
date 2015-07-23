package fiu.ssobec;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;

import org.hamcrest.Matcher;

import fiu.ssobec.Activity.LoginActivity;
import fiu.ssobec.Activity.MyZonesActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.android.support.test.deps.guava.base.Preconditions.checkNotNull;
import static org.hamcrest.EasyMock2Matchers.equalTo;


/**
 * @author Irvin Steve Cardenas
 */
@LargeTest
public class UnfollowZoneActivityTest extends ActivityInstrumentationTestCase2<MyZonesActivity> {

    public UnfollowZoneActivityTest() {
        super(MyZonesActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @MediumTest
    public void testText() {

        // STEP 1: Log In as an Admin
        onView(withId(R.id.email_text_field)).perform(ViewActions.typeText("irvsteve@gmail.com"));
        onView(withId(R.id.password_text_field)).perform(ViewActions.typeText("abc123"));
        onView(withId(R.id.login_button)).perform(ViewActions.click());

        // Assert we are in the right view
//        onView(withId(R.id.list_view_userrewards)).check(ViewAssertions.doesNotExist());
//        pressBack();

        // STEP 2: Click on Add Zones Menu Item
        onView(withId(R.id.action_addzone)).perform(ViewActions.click());

        // STEP 3: Assert we are in the right View
        onView(withId(R.id.zonelist_view)).check(ViewAssertions.doesNotExist());

    }

    public static Matcher<Object> withChildName(String name) {
        checkNotNull(name);
        return withChildName(equalTo(name));
    }




}
