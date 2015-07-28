package fiu.ssobec;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.ImageView;

import fiu.ssobec.Activity.EditZoneUsersActivity;

/**
 * Created by irvincardenas on 6/26/15.
 */
public class EditZoneActivityUsersActivityTest extends ActivityInstrumentationTestCase2<EditZoneUsersActivity> {

    private EditZoneUsersActivity testActivity;
    private ImageView addUserBtn;
    private ImageView removeUserBtn;

    public EditZoneActivityUsersActivityTest(Class<EditZoneUsersActivity> activityClass) {
        super(activityClass);
    }

    protected void setUp() throws Exception {
        super.setUp();
        testActivity = getActivity();
        addUserBtn = (ImageView) testActivity.findViewById(R.id.zone_add_user);
        removeUserBtn = (ImageView) testActivity.findViewById(R.id.zone_remove_user);
    }

    public void testPreConditions() {
        assertNotNull("testAcitivy is null", testActivity);
        assertNotNull("addUserBtn is null", addUserBtn);
        assertNotNull("removeUserBtn is null", removeUserBtn);
    }
}
