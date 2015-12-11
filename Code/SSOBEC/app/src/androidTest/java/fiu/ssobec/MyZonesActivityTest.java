package fiu.ssobec;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;

import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import fiu.ssobec.Activity.MyZonesActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Maria on 2/12/2015.
 */
public class MyZonesActivityTest  extends ActivityInstrumentationTestCase2<MyZonesActivity> {


    public MyZonesActivityTest() {
        super(MyZonesActivity.class);
    }


    //Set Up Method
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @MediumTest
    public void testDifferentZones() {

        /*onView(withId(R.id.email_text_field)).perform(ViewActions.typeText("mandy@yahoo.com"));
        onView(withId(R.id.password_text_field)).perform(ViewActions.typeText("mandy123"));
        onView(withId(R.id.login_button)).perform(ViewActions.click());
        onView(withId(1)).perform(ViewActions.click());*/

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withId(R.id.action_location)).perform(ViewActions.click());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withId(R.id.action_location)).perform(ViewActions.click());

        onView(withId(R.id.ArtificialLightingButton)).perform(ViewActions.click());
        onView(withId(R.id.myLightingChart)).check(ViewAssertions.doesNotExist());
        onView(withId(R.id.buildingLightingChart)).check(ViewAssertions.doesNotExist());
        pressBack();

        onView(withId(3)).perform(ViewActions.click());
        onView(withId(R.id.TemperatureButton)).perform(ViewActions.click());
        pressBack();
        onView(withId(R.id.ArtificialLightingButton)).perform(ViewActions.click());
        pressBack();
        onView(withId(R.id.OccupancyButton)).perform(ViewActions.click());
        pressBack();
        onView(withId(R.id.PludLoadButton)).perform(ViewActions.click());
        pressBack();
        pressBack();

        onView(withId(5)).perform(ViewActions.click());
        onView(withId(R.id.TemperatureButton)).perform(ViewActions.click());
        pressBack();
        onView(withId(R.id.ArtificialLightingButton)).perform(ViewActions.click());
        pressBack();
        onView(withId(R.id.OccupancyButton)).perform(ViewActions.click());
        pressBack();
        onView(withId(R.id.PludLoadButton)).perform(ViewActions.click());
        pressBack();


    }


}
