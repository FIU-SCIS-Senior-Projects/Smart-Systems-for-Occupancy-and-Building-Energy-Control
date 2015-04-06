package fiu.ssobec;

import android.support.test.espresso.action.ViewActions;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import fiu.ssobec.Activity.MyZonesActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by Maria on 4/4/2015.
 */
public class StatisticalCalculationTest extends ActivityInstrumentationTestCase2<MyZonesActivity> {


    private MyZonesActivity mActivity;

    public StatisticalCalculationTest() {
        super(MyZonesActivity.class);
    }

    //Set Up Method
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @MediumTest
    public void testStats() {

        onView(withId(1)).perform(ViewActions.click());
        onView(withId(R.id.PludLoadButton)).perform(ViewActions.click());

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(allOf(withText("Energy Usage Comparison"))).perform(ViewActions.click());

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(allOf(withText("Appliances"))).perform(ViewActions.click());

        pressBack();

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.ArticialLightingButton)).perform(ViewActions.click());

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pressBack();
        onView(withId(R.id.TemperatureButton)).perform(ViewActions.click());
        onView(withId(R.id.temperature_graph)).perform(ViewActions.swipeLeft());
        onView(withId(R.id.temperature_graph)).perform(ViewActions.swipeLeft());
        onView(withId(R.id.temperature_graph)).perform(ViewActions.swipeLeft());
        onView(withId(R.id.temperature_graph)).perform(ViewActions.swipeLeft());
        onView(withId(R.id.temperature_graph)).perform(ViewActions.swipeRight());
        onView(withId(R.id.temperature_graph)).perform(ViewActions.swipeRight());

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pressBack();
        onView(withId(R.id.OccupancyButton)).perform(ViewActions.click());

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pressBack();
        pressBack();

        onView(withId(2)).perform(ViewActions.click());

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.OccupancyButton)).perform(ViewActions.click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
