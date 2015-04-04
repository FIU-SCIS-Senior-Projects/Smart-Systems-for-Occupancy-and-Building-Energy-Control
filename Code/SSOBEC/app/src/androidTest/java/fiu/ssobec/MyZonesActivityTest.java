package fiu.ssobec;

import android.support.test.espresso.action.ViewActions;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import fiu.ssobec.Activity.MyZonesActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Maria on 2/12/2015.
 */
public class MyZonesActivityTest  extends ActivityInstrumentationTestCase2<MyZonesActivity> {

    private MyZonesActivity mActivity;

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
    public void testText() {

        onView(withId(1)).perform(ViewActions.click());
        onView(withId(R.id.TemperatureButton)).perform(ViewActions.click());
        onView(withId(R.id.temp_ac_performance)).perform(ViewActions.click());
        onView(withId(R.id.today_temperature_textfield)).perform(ViewActions.typeText("60"));
        onView(withId(R.id.predict_button)).perform(ViewActions.click());

        try {
            Thread.sleep(6500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
