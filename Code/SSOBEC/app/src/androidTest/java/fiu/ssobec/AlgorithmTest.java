package fiu.ssobec;

import android.support.test.espresso.action.ViewActions;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import org.hamcrest.Matchers;

import fiu.ssobec.Activity.MyZonesActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Dalaidis on 4/6/2015.
 */
public class AlgorithmTest extends ActivityInstrumentationTestCase2<MyZonesActivity> {


    private MyZonesActivity mActivity;

    public AlgorithmTest() {
        super(MyZonesActivity.class);
    }


    //Set Up Method
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @MediumTest
    public void testNaiveAlgorithm() {

        onView(withId(1)).perform(click());
        onView(withId(R.id.TemperatureButton)).perform(click());

        // Open the overflow menu OR open the options menu,
        // depending on if the device has a hardware or software overflow menu button.
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        // Click the item.
        onView(withText(Matchers.startsWith("Temperature")))
                .perform(click());

        for(int i = 70; i <= 100; i=i+2)
        {
            onView(withId(R.id.today_temperature_textfield)).perform(ViewActions.typeText(i+""));
            onView(withId(R.id.predict_button)).perform(ViewActions.click());
            System.out.println(i+") "+ onView(withId(R.id.prediction_result)).toString());
            onView(withId(R.id.today_temperature_textfield)).perform(ViewActions.clearText());
        }

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
