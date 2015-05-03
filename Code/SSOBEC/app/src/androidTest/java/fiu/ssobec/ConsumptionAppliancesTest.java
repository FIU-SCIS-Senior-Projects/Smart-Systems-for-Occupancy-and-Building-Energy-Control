package fiu.ssobec;

import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.espresso.util.TreeIterables;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;

import org.hamcrest.Matcher;

import java.util.concurrent.TimeoutException;

import fiu.ssobec.Activity.LoginActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Maria on 4/29/2015.
 */
public class ConsumptionAppliancesTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    public ConsumptionAppliancesTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @MediumTest
    public void testConsumptionAppliances() {

        onView(withId(R.id.email_text_field)).perform(ViewActions.typeText("mandy@yahoo.com"));
        onView(withId(R.id.password_text_field)).perform(ViewActions.typeText("mandy123"));
        onView(withId(R.id.login_button)).perform(ViewActions.click());
        onView(isRoot()).perform(waitId(1, 100 * 1000));
        onView(withId(1)).perform(click());
        onView(withId(R.id.predict_consumption)).perform(click());
        onView(withText("Electric Appliances")).perform(click());

        onView(withId(R.id.checkbox_child_row)).check(matches(isNotChecked()));
        //onView(withId(R.id.checkbox_child_row), posi).check(matches(isNotChecked()));

        //onData(hasToString(startsWith("ASDF"))).perform(click());

    }

    public static ViewAction waitId(final int viewId, final long millis) {
        return new ViewAction() {

            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait for a specific view with id <" + viewId + "> during " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + millis;
                final Matcher<View> viewMatcher = withId(viewId);

                do {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            return;
                        }
                    }

                    uiController.loopMainThreadForAtLeast(50);
                }
                while (System.currentTimeMillis() < endTime);

                // timeout happens
                throw new PerformException.Builder()
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }
}
