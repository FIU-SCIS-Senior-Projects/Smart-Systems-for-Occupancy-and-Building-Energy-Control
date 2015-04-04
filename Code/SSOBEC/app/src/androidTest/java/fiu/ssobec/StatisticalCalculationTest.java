package fiu.ssobec;

import android.support.test.espresso.action.ViewActions;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import fiu.ssobec.Activity.MyZonesActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

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
        //onView(withId(R.id.OccupancyButton)).perform(ViewActions.click());

        //onView(withId(R.id.TemperatureButton)).perform(ViewActions.click());
        onView(withId(R.id.PludLoadButton)).perform(ViewActions.click());

        try {
            Thread.sleep(7500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

}
