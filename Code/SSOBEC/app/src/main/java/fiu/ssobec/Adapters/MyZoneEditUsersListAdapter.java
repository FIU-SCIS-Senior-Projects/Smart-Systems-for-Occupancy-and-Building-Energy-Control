package fiu.ssobec.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fiu.ssobec.Activity.AddUserToZoneActivity;
import fiu.ssobec.Activity.EditZoneActivity;
import fiu.ssobec.Activity.RemoveUserFromZoneActivity;
import fiu.ssobec.AdaptersUtil.ZoneListParent;
import fiu.ssobec.DataAccess.DataAccessUser;
import fiu.ssobec.DataAccess.ExternalDatabaseController;
import fiu.ssobec.Model.User;
import fiu.ssobec.R;

/**
 * Created by Maria on 4/14/2015.
 */
public class MyZoneEditUsersListAdapter implements ListAdapter {


    public static final String ADDNEWZONE_PHP = "http://smartsystems-dev.cs.fiu.edu/addnewzone.php";
    private LayoutInflater inflater;
    private ArrayList<ZoneListParent> parents;
    private FragmentManager fm;

    private static Context mContext;

    public MyZoneEditUsersListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        mContext = context;
    }

    public MyZoneEditUsersListAdapter(Context context, FragmentManager fm) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        this.fm = fm;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return parents.size();
    }

    @Override
    public Object getItem(int position) {
        return parents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolder mViewHolder = null;
        final ZoneListParent mparent = parents.get(position);

        if(convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row_zone_edit_users_list, parent, false);
            mViewHolder.zone_name = (TextView) convertView.findViewById(R.id.zone_name);
            mViewHolder.userPlus = (ImageView) convertView.findViewById(R.id.zone_add_user);
            mViewHolder.userMinus = (ImageView) convertView.findViewById(R.id.zone_remove_user);
            convertView.setTag(mViewHolder);
        }
        else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.zone_name.setText(mparent.getZone_name());

//        if(mparent.isZone_added())
//            mViewHolder.imgplus.setVisibility(View.VISIBLE);
//        else
//            mViewHolder.imgplus.setVisibility(View.INVISIBLE);

        mViewHolder.userPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, AddUserToZoneActivity.class);
                i.putExtra(EditZoneActivity.EXTRA_ZONE_ID, mparent);
                mContext.startActivity(i);
            }
        });

        mViewHolder.userMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, RemoveUserFromZoneActivity.class);
                i.putExtra(EditZoneActivity.EXTRA_ZONE_ID, mparent);
                mContext.startActivity(i);
            }
        });

//        convertView.findViewById(R.id.zone_edit).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mContext, "Edit Zone", Toast.LENGTH_SHORT).show();
//                Intent i = new Intent(mContext,EditZoneActivity.class);
//                i.putExtra(EditZoneActivity.EXTRA_ZONE_ID, mparent);
//                mContext.startActivity(i);
//            }
//        });


        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return  ((parents == null) || parents.isEmpty());
    }

    public void setParents(ArrayList<ZoneListParent> newparent){
        parents = newparent;
    }


    static class ViewHolder {
        private  TextView zone_name;
        private ImageView userPlus;
        private ImageView userMinus;

    }

    public static class AddZoneDialogFragment extends DialogFragment {

        private final ZoneListParent mparent;

        public AddZoneDialogFragment() {
            mparent=null;
        }

        @SuppressLint("ValidFragment")
        public AddZoneDialogFragment(ZoneListParent mparent) {
            this.mparent = mparent;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Add "+mparent.getZone_name()+" to your zones?")
                    .setPositiveButton("add", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            int user_id = getUserID();

                            List<NameValuePair> zone_id = new ArrayList<>(2);
                            zone_id.add(new BasicNameValuePair("region_id", (mparent.getZone_id()+"").trim()));
                            zone_id.add(new BasicNameValuePair("user_id", (user_id+"").trim()));

                            try {
                                String res = new ExternalDatabaseController((ArrayList<NameValuePair>) zone_id, ADDNEWZONE_PHP).send();
                                Log.i("ZoneListAdapter", "DB Insert result: " + res);
                                //Toast (zone added succesfully);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                //zone could not be added
                            }
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }

        private int getUserID() {

            DataAccessUser dataAccessUser = DataAccessUser.getInstance(mContext);
            try {
                dataAccessUser.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            User user = dataAccessUser.getUser(1);

            dataAccessUser.close();

            return user.getId();
        }
    }
}
