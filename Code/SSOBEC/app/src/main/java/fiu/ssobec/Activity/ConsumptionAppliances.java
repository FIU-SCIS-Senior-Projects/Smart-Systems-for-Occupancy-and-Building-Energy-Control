package fiu.ssobec.Activity;

import android.app.ExpandableListActivity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import fiu.ssobec.Child;
import fiu.ssobec.Parent;
import fiu.ssobec.R;


public class ConsumptionAppliances extends ExpandableListActivity {

    //Initialize variables
    //private static final String STR_CHECK = " Has Checked!";
    //private static final String STR_UNCHECK = " Has unChecked!";
    private int ParentClickStatus = -1;
    private int ChildClickStatus = -1;
    private ArrayList<Parent> parents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources res = this.getResources();
        Drawable divider = res.getDrawable(R.drawable.line);

        // Set ExpandableListView values
        getExpandableListView().setGroupIndicator(null);
        getExpandableListView().setDivider(divider);
        getExpandableListView().setChildDivider(divider);
        getExpandableListView().setDividerHeight(1);
        registerForContextMenu(getExpandableListView());

        //Create one  Static Data in Arraylist
        final ArrayList<Parent> mydummyList = buildDummyData();

        // Adding ArrayList data to ExpandableListView values
        loadHosts(mydummyList);
    }

    //Data Service Implementation

    private ArrayList<Parent> buildDummyData() {
        // Creating ArrayList of type parent class to store parent class objects
        final ArrayList<Parent> list = new ArrayList<Parent>();
        for (int i = 1; i < 6; i++) {
            //Create parent class object
            final Parent parent = new Parent();

            // Set values in parent class object
            if (i == 1) {
                parent.setName("" + i);
                parent.setText1("Electric Appliances");
                parent.setText2("Select the appliances that you want to make a prediction");
                parent.setChildren(new ArrayList<Child>());

                // Create Child class object
                final Child child = new Child();
                child.setName("" + i);
                child.setText1("Laptop");
                parent.getChildren().add(child);
                final Child child1 = new Child();
                child1.setName("" + i);
                child1.setText1("Microwave");
                parent.getChildren().add(child1);
                final Child child2 = new Child();
                child2.setName("" + i);
                child2.setText1("Fridge/Freezer");
                parent.getChildren().add(child2);
                final Child child3 = new Child();
                child3.setName("" + i);
                child3.setText1("Printer");
                parent.getChildren().add(child3);


                //Add Child class object to parent class object
                parent.getChildren().add(child);
            } else {
                if (i == 2) {
                    parent.setName("" + i);
                    parent.setText1("Amount of Appliances");
                    parent.setText2("Select the amount of appliances");
                    parent.setChildren(new ArrayList<Child>());

                    final Child child = new Child();
                    child.setName("" + i);
                    child.setText1("1");
                    parent.getChildren().add(child);

                    final Child child1 = new Child();
                    child1.setName("" + i);
                    child1.setText1("2");
                    parent.getChildren().add(child1);

                    final Child child2 = new Child();
                    child.setName("" + i);
                    child.setText1("3");
                    parent.getChildren().add(child);

                    final Child child3 = new Child();
                    child.setName("" + i);
                    child.setText1("4");
                    parent.getChildren().add(child);

                    final Child child4 = new Child();
                    child.setName("" + i);
                    child.setText1("5");
                    parent.getChildren().add(child);
                } else if (i == 3) {
                    parent.setName("" + i);
                    parent.setText1("Hours of use");
                    parent.setText2("Select the hours of use");
                    parent.setChildren(new ArrayList<Child>());

                    final Child child = new Child();
                    child.setName("" + i);
                    child.setText1("1");
                    parent.getChildren().add(child);

                    final Child child1 = new Child();
                    child1.setName("" + i);
                    child1.setText1("2");
                    parent.getChildren().add(child1);

                    final Child child2 = new Child();
                    child.setName("" + i);
                    child.setText1("3");
                    parent.getChildren().add(child);

                    final Child child3 = new Child();
                    child.setName("" + i);
                    child.setText1("4");
                    parent.getChildren().add(child);

                    final Child child4 = new Child();
                    child.setName("" + i);
                    child.setText1("5");
                    parent.getChildren().add(child);
                } else if (i == 4) {
                    parent.setName("" + i);
                    parent.setText1("Days of use");
                    parent.setText2("Select the days of use");
                    parent.setChildren(new ArrayList<Child>());

                    final Child child = new Child();
                    child.setName("" + i);
                    child.setText1("1");
                    parent.getChildren().add(child);

                    final Child child1 = new Child();
                    child1.setName("" + i);
                    child1.setText1("2");
                    parent.getChildren().add(child1);

                    final Child child2 = new Child();
                    child.setName("" + i);
                    child.setText1("3");
                    parent.getChildren().add(child);

                    final Child child3 = new Child();
                    child.setName("" + i);
                    child.setText1("4");
                    parent.getChildren().add(child);

                    final Child child4 = new Child();
                    child.setName("" + i);
                    child.setText1("5");
                    parent.getChildren().add(child);
                } else if (i == 5) {
                    parent.setName("" + i);
                    parent.setText1("Montly Cost");
                    parent.setText2("Prediction of Montly Cost");
                    parent.setChildren(new ArrayList<Child>());

                    final Child child = new Child();
                    child.setName("" + i);
                    child.setText1("1");
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
            final MyExpandableListAdapter mAdapter = new MyExpandableListAdapter();

            // Set Adapter to ExpandableList Adapter
            this.setListAdapter(mAdapter);
        } else {
            // Refresh ExpandableListView data
            ((MyExpandableListAdapter) getExpandableListAdapter()).notifyDataSetChanged();
        }
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
            image.setImageResource(getResources().getIdentifier("fiu.ssobec:drawable/consumption" + parent.getName(), null, null));
            return convertView;
        }


        // This Function used to inflate child rows view
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parentView) {
            final Parent parent = parents.get(groupPosition);
            final Child child = parent.getChildren().get(childPosition);

            // Inflate childrow.xml file for child rows
            convertView = inflater.inflate(R.layout.childrow, parentView, false);

            // Get childrow.xml file elements and set values
            ((TextView) convertView.findViewById(R.id.text1)).setText(child.getText1());
            ImageView image = (ImageView) convertView.findViewById(R.id.childImage);
            image.setImageResource(getResources().getIdentifier("com.androidexample.customexpandablelist:drawable/setting" + parent.getName(), null, null));

            // Get grouprow.xml file checkbox elements
            CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.checkbox_child_row);
            checkbox.setChecked(child.isChecked());

            // Set CheckUpdateListener for CheckBox (see below CheckUpdateListener class)
            checkbox.setOnCheckedChangeListener(new CheckUpdateListener(child));

            return convertView;
        }


        @Override
        public Object getChild(int groupPosition, int childPosition) {
            //Log.i("Childs", groupPosition+"=  getChild =="+childPosition);
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
            Log.i("Parent", groupPosition + "=  getGroup ");

            return parents.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return parents.size();
        }

        //Call when parent row clicked
        @Override
        public long getGroupId(int groupPosition) {
            Log.i("Parent", groupPosition + "=  getGroupId " + ParentClickStatus);

            /*
            if (groupPosition == 2 && ParentClickStatus != groupPosition) {
            }*/

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

                ((MyExpandableListAdapter) getExpandableListAdapter()).notifyDataSetChanged();

                final Boolean checked = child.isChecked();
            }
        }
    }
}
