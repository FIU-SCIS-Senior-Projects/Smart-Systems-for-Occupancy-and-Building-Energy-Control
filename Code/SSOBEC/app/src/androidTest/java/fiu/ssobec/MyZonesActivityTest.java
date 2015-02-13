package fiu.ssobec;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.GridView;

import fiu.ssobec.Activity.MyZonesActivity;

/**
 * Created by Maria on 2/12/2015.
 */
public class MyZonesActivityTest  extends ActivityInstrumentationTestCase2<MyZonesActivity> {

    private MyZonesActivity mActivity;
    private GridView mGridView;

    public MyZonesActivityTest() {
        super(MyZonesActivity.class);
    }


    //Set Up Method
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(false); //turns off touch mode in the device or emulator
        mActivity = getActivity(); //calls the activity if it's no already running

        //findViewById(fiu.ssobec.Activity.MyZonesActivity.R.id.grid_view_buttons);

    } // end of setUp() method definition

}
