package fiu.ssobec.Activity;

import android.app.ExpandableListActivity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import fiu.ssobec.Child;
import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.Parent;
import fiu.ssobec.PredictPlugLoadConsumption;
import fiu.ssobec.R;


public class ConsumptionAppliances extends ExpandableListActivity {

    //Initialize variables
    private int ParentClickStatus = -1;
    private int ChildClickStatus = -1;
    private ArrayList<Parent> parents;
    static String ELECTRIC_APPL = "Electric Appliances";
    static String AMOUNT_APPL = "Amount of Appliances";
    static String HOURS_USE = "Hours of Use";
    static String DAYS_USE = "Days of Use";
    static String MONTHLY_CONSUMPTION = "Monthly Consumption";
    static String MONTHLY_COST = "Monthly Cost";

    static String [] parent_text1 = {ELECTRIC_APPL, AMOUNT_APPL, HOURS_USE, DAYS_USE, MONTHLY_CONSUMPTION,MONTHLY_COST};
    static String [] parent_text2 = {"Select the appliances that you want to make a prediction",
            "Select the amount of appliances",
            "Select the hours of Use",
            "Select the days of Use",
            "Prediction of Monthly Consumption",
            "Prediction of Monthly Cost"};

    static String [] appl_names = {"laptop", "Microwave", "Fridge/Freezer", "Printer"};

    static int num_childs;

    MyExpandableListAdapter mAdapter;

    private static DataAccessUser data_access;

    HashMap<String, Double> appl_info_hmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources res = this.getResources();
        Drawable divider = res.getDrawable(R.drawable.line);

        //Declare the access to the SQLite table for user
        data_access = new DataAccessUser(this);

        //Open the data access to the tables
        try { data_access.open(); } catch (SQLException e) { e.printStackTrace(); }

        // Set ExpandableListView values
        getExpandableListView().setGroupIndicator(null);
        getExpandableListView().setDivider(divider);
        getExpandableListView().setChildDivider(divider);
        getExpandableListView().setDividerHeight(1);
        registerForContextMenu(getExpandableListView());

        //Create one  Static Data in Arraylist
        final ArrayList<Parent> mydummyList = getDBData();

        // Adding ArrayList data to ExpandableListView values
        loadHosts(mydummyList);
    }


    private ArrayList<Parent> getDBData() {
        // Creating ArrayList of type parent class to store parent class objects
        final ArrayList<Parent> list = new ArrayList<>();

        appl_info_hmap = data_access.getAllApplianceInformation(ZonesDescriptionActivity.regionID);

        num_childs = appl_info_hmap.size();

        String[] app_names_children = Arrays.copyOf(appl_info_hmap.keySet().toArray(), appl_info_hmap.keySet().toArray().length, String[].class);

        for (int i = 0; i < 6; i++) {

            //Create parent class object
            final Parent parent = new Parent();

            parent.setName("" + i);
            parent.setText1(parent_text1[i]);
            parent.setText2(parent_text2[i]);
            parent.setChildren(new ArrayList<Child>());

            if(i == 4)
                num_childs = num_childs + 1;

            for(int j = 0 ; j < num_childs ; j++)
            {
                System.out.println(" i = "+i+", j = "+j);

                if((i == 4 || i == 5) && (j == num_childs-1))
                {
                    System.out.println("button child: i = "+i+", j = "+j);
                    final Child child = new Child();
                    child.setName("" + j);
                    parent.getChildren().add(child);
                }
                else
                {
                    final Child child = new Child();
                    child.setName("" + j);
                    child.setText1(app_names_children[j]);
                    parent.getChildren().add(child);
                }
            }
            //Adding Parent class object to ArrayList
            list.add(parent);
        }

        num_childs--;
        return list;
    }


    //Data Service Implementation

    private ArrayList<Parent> buildDummyData() {
        // Creating ArrayList of type parent class to store parent class objects
        final ArrayList<Parent> list = new ArrayList<Parent>();
        int numChilds = 4;

        for (int i = 0; i < 6; i++) {
            //Create parent class object
            final Parent parent = new Parent();

            parent.setName("" + i);
            parent.setText1(parent_text1[i]);
            parent.setText2(parent_text2[i]);
            parent.setChildren(new ArrayList<Child>());

            if(i == 4 || i == 5)
                numChilds = 5;

            for(int j = 0 ; j < numChilds ; j++)
            {
                if((i == 4 || i == 5) && j == numChilds - 1)
                {
                    final Child child = new Child();
                    child.setName("" + j);
                    parent.getChildren().add(child);
                }
                else
                {
                    final Child child = new Child();
                    child.setName("" + j);
                    child.setText1(appl_names[j]);
                    parent.getChildren().add(child);
                }

            }

            //Adding Parent class object to ArrayList
            list.add(parent);
        }
        return list;
    }

    private void loadHosts(final ArrayList<Parent> newParents) {

        if (newParents == null)
            return;

        parents = newParents;

        // Check for ExpandableListAdapter object
        if (this.getExpandableListAdapter() == null) {

            //Create ExpandableListAdapter Object
            mAdapter = new MyExpandableListAdapter();

            // Set Adapter to ExpandableList Adapter
            this.setListAdapter(mAdapter);

        } else {
            // Refresh ExpandableListView data
            ((MyExpandableListAdapter) getExpandableListAdapter()).notifyDataSetChanged();
        }

    }

    //When an Activity is resumed, open the SQLite
    //connection
    @Override
    protected void onResume() {
        super.onResume();
        try {
            data_access.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //When an Activity is left, close the
    //SQLite connection
    @Override
    protected void onPause() {
        super.onPause();
        data_access.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_consumption_appliances, menu);
        return true;
    }

    /**
     * A Custom adapter to create Parent view (Used grouprow.xml) and Child View((Used childrow.xml).
     */
    private class MyExpandableListAdapter extends BaseExpandableListAdapter {


        private LayoutInflater inflater;

        public MyExpandableListAdapter() {
            inflater = LayoutInflater.from(ConsumptionAppliances.this);
        }

        // This Function used to inflate parent rows view
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parentView) {
            final Parent parent = parents.get(groupPosition);

            // Inflate grouprow.xml file for parent rows
            convertView = inflater.inflate(R.layout.grouprow, parentView, false);

            // Get grouprow.xml file elements and set values
            ((TextView) convertView.findViewById(R.id.text1)).setText(parent.getText1());
            ((TextView) convertView.findViewById(R.id.text)).setText(parent.getText2());
            ImageView image = (ImageView) convertView.findViewById(R.id.imageParent);
            image.setImageResource(getResources().getIdentifier("fiu.ssobec:drawable/consumption"
                    + parent.getName(), null, null));
            return convertView;
        }


        // This Function used to inflate child rows view
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parentView) {
            final Parent parent = parents.get(groupPosition);
            final Child child = parent.getChildren().get(childPosition);

            ArrayList<Double> powerKw = new ArrayList<>();
            ArrayList<Integer> quantity = new ArrayList<>();
            ArrayList<Integer> hoursUse = new ArrayList<>();
            ArrayList<Integer> daysUse = new ArrayList<>();

            calcData(powerKw,quantity,hoursUse,daysUse);

            final PredictPlugLoadConsumption mypredict = new PredictPlugLoadConsumption(powerKw,quantity,hoursUse,daysUse);
            mypredict.MonthlyConsumption();

            // Inflate childrow.xml file for child rows
            if (parent.getName().equals("0")) {
                convertView = inflater.inflate(R.layout.childrow, parentView, false);
                CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.checkbox_child_row);
                checkbox.setChecked(child.isChecked());
                checkbox.setOnCheckedChangeListener(new CheckUpdateListener(child));

                ((TextView) convertView.findViewById(R.id.text1)).setText(child.getText1());
                ImageView image = (ImageView) convertView.findViewById(R.id.childImage);
                image.setImageResource(getResources().getIdentifier("com.androidexample.customexpandablelist:drawable/setting"
                        + parent.getName(), null, null));
            }
            else if (parent.getName().equals("4"))
            {
                if(child.getName().equals((num_childs-1)+""))
                {
                    convertView = inflater.inflate(R.layout.childrow_calcbutton, parentView, false);
                    Button b = (Button) convertView.findViewById(R.id.monthly_cons_button);
                    final View finalConvertView1 = convertView;
                    b.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v)
                        {
                            System.out.println("Button Clicked");
                            mypredict.getTotalConsumption();
                            ((TextView) finalConvertView1.findViewById(R.id.total_consumption_textview)).setText(mypredict.getTotalConsumption() + "");

                        }
                    });
                }
                else
                {
                    convertView = inflater.inflate(R.layout.childrow_result_textfield, parentView, false);
                    ((TextView) convertView.findViewById(R.id.text1)).setText(child.getText1());
                    ImageView image = (ImageView) convertView.findViewById(R.id.childImage);
                    image.setImageResource(getResources().getIdentifier("com.androidexample.customexpandablelist:drawable/setting"
                            + parent.getName(), null, null));
                }
            }
            else if (parent.getName().equals("5"))
            {
                if(child.getName().equals((num_childs-1)+""))
                {
                    convertView = inflater.inflate(R.layout.childrow_calcbutton, parentView, false);
                    Button b = (Button) convertView.findViewById(R.id.monthly_cons_button);
                    b.setText(MONTHLY_COST);
                    final View finalConvertView = convertView;
                    b.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v)
                        {
                            System.out.println("Button Clicked");
                            ((TextView) finalConvertView.findViewById(R.id.total_consumption_textview)).setText( mypredict.getTotalCost()+"");

                        }
                    });
                }
                else
                {
                    convertView = inflater.inflate(R.layout.childrow_result_textfield, parentView, false);
                    ((TextView) convertView.findViewById(R.id.text1)).setText(child.getText1());
                    ImageView image = (ImageView) convertView.findViewById(R.id.childImage);
                    image.setImageResource(getResources().getIdentifier("com.androidexample.customexpandablelist:drawable/setting"
                            + parent.getName(), null, null));
                }
            }
            else
            {
                convertView = inflater.inflate(R.layout.childrow_num_field, parentView, false);
                ((TextView) convertView.findViewById(R.id.text1)).setText(child.getText1());
                ImageView image = (ImageView) convertView.findViewById(R.id.childImage);
                image.setImageResource(getResources().getIdentifier("com.androidexample.customexpandablelist:drawable/setting"
                        + parent.getName(), null, null));

                //get field...
                EditText my_num_text = (EditText) convertView.findViewById(R.id.numField_child_row);
                my_num_text.setText(child.getEditTextChildNumField()+"");
                my_num_text.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }

                    @Override
                    public void afterTextChanged(Editable s) {
                        System.out.println("Text Changed: "+s.toString());
                        int result;
                        try
                        {
                            result = Integer.parseInt(s.toString());
                            child.setEditTextChildNumField(result);
                        }
                        catch (NumberFormatException e)
                        {
                            System.out.println("Not a number: "+e);
                        }

                    }
                });
            }

            return convertView;
        }

        public void calcData( ArrayList<Double> powerKw, ArrayList<Integer> quantity, ArrayList<Integer> hoursUse, ArrayList<Integer> daysUse) {

            for (int i = 0; i < 4; i++) {
                //Create parent class object
                final Parent parent = parents.get(i);

                for (int j = 0; j < num_childs; j++) {


                    Child otherchilds = parent.getChildren().get(j);
                    switch(i)
                    {
                        case 0:
                            if(otherchilds.isChecked())
                            {
                                Log.i("ConsumptionAppliances"," For: "+otherchilds.getText1());
                                Log.i("ConsumptionAppliances"," For: "+appl_info_hmap.toString());

                                // Log.i("ConsumptionAppliances"," Value: "+appl_info_hmap.get(otherchilds.getText1()));
                                powerKw.add(appl_info_hmap.get(otherchilds.getText1()));
                            }
                            else
                                powerKw.add(0.0);

                            break;
                        case 1:
                            quantity.add(otherchilds.getEditTextChildNumField());
                            break;
                        case 2:
                            hoursUse.add(otherchilds.getEditTextChildNumField());
                            break;
                        case 3:
                            daysUse.add(otherchilds.getEditTextChildNumField());
                            break;
                    }
                }
            }

        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return parents.get(groupPosition).getChildren().get(childPosition);
        }

        //Call when child row clicked
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            /****** When Child row clicked then this function call *******/

            Log.i("Noise", "parent == "+groupPosition+"=  child : =="+childPosition);
            if (ChildClickStatus != childPosition) {
                ChildClickStatus = childPosition;
            }

            return childPosition;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            int size = 0;
            if (parents.get(groupPosition).getChildren() != null)
                size = parents.get(groupPosition).getChildren().size();
            return size;
        }


        @Override
        public Object getGroup(int groupPosition) {
            //Log.i("Parent", groupPosition + "=  getGroup ");
            return parents.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return parents.size();
        }

        //Call when parent row clicked
        @Override
        public long getGroupId(int groupPosition) {
            //Log.i("Parent", groupPosition + "=  getGroupId " + ParentClickStatus);

            ParentClickStatus = groupPosition;
            if (ParentClickStatus == 0)
                ParentClickStatus = -1;

            return groupPosition;
        }

        @Override
        public void notifyDataSetChanged() {
            // Refresh List rows
            super.notifyDataSetChanged();
        }

        @Override
        public boolean isEmpty() {
            return ((parents == null) || parents.isEmpty());
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        /**
         * **************** Checkbox Checked Change Listener *******************
         */
        private final class CheckUpdateListener implements OnCheckedChangeListener {
            private final Child child;

            private CheckUpdateListener(Child child) {
                this.child = child;
            }

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i("onCheckedChanged", "isChecked: " + isChecked);

                child.setChecked(isChecked);
                final Boolean checked = child.isChecked();
                if(checked)
                {
                    for (int i=1; i < 5; i++) {
                        final Parent parent = parents.get(i); //get parent from position 'i'.
                        System.out.println("Checked, Parent: " + parent.getText1());

                        for (int j = 0; j < 4; j++) {
                            Child otherchilds = parent.getChildren().get(j);
                            System.out.println("Checked, Other Childs: " + otherchilds.getText1());
                        }
                    }
                }
            }
        }

    }
}
