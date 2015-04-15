package fiu.ssobec;

import android.support.test.espresso.action.ViewActions;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;

import org.hamcrest.Matcher;

import fiu.ssobec.Activity.LoginActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.android.support.test.deps.guava.base.Preconditions.checkNotNull;
import static org.hamcrest.EasyMock2Matchers.equalTo;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by Fresa on 2/7/2015.
 */
@LargeTest
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @MediumTest
    public void testText() {

        onView(withId(R.id.email_text_field)).perform(ViewActions.typeText("mandy@yahoo.com"));
        onView(withId(R.id.password_text_field)).perform(ViewActions.typeText("mandy123"));
        onView(withId(R.id.login_button)).perform(ViewActions.click());
        onView(withId(1)).perform(ViewActions.click());
        onView(withId(R.id.PludLoadButton)).perform(ViewActions.click());
        onView(withId(R.id.predict_consumption_button)).perform(ViewActions.click());
        //Espresso.onView(withText("Electric")).perform(ViewActions.click());

        onView(allOf(withId(R.id.text1), withText("Electric Appliances"))).perform(ViewActions.click());

        //onData(Matchers.<Object>allOf(withId(R.id.text1), withText("Electric Appliances")));

        //onView(allOf(withId(R.id.text1), withText("Ana Laptop")));

/*        onView(allOf(withId(R.id.checkbox_child_row), hasSibling(allOf(withId(R.id.text1), withText("Ana Laptop")))))
                .perform(ViewActions.click());*/

        /*
        onData(withItemContent("Ana Laptop"))
                .onChildView(withId(R.id.checkbox_child_row))
                .perform(ViewActions.click());*/

        //onView(allOf(withId(R.id.text1), withText("Ana Laptop")));

        //onData(hasToString(startsWith("Ana")));

        //onView(allOf(withId(R.id.checkbox_child_row),hasSibling(allOf(withId(R.id.text1), withText("Ana Laptop"))))).check(matches(isDisplayed()));

        /*
        onData(allOf(is(instanceOf(Child.class)), withChildName("Ana Laptop")))
                .inAdapterView(withId(R.id.checkbox_child_row))
                .check(matches(isDisplayed()))
                .perform(ViewActions.click());*/

        onView(allOf(withId(R.id.text1),withText("Ana Laptop"))).check(matches(isDisplayed()));

        pressBack();

        pressBack();

        pressBack();

        onView(withId(2)).perform(ViewActions.click());
        onView(withId(R.id.PludLoadButton)).perform(ViewActions.click());
        onView(withId(R.id.predict_consumption_button)).perform(ViewActions.click());
        //Espresso.onView(withText("Electric")).perform(ViewActions.click());

        onView(allOf(withId(R.id.text1), withText("Electric Appliances"))).perform(ViewActions.click());

        //onView(allOf(withId(R.id.text1),withText("Ana Laptop"))).check(matches(isDisplayed()));

    }

    public static Matcher<Object> withChildName(String name) {
        checkNotNull(name);
        return withChildName(equalTo(name));
    }




}
