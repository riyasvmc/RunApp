package com.kodewiz.run;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.kodewiz.run.data.DBHelper;
import com.kodewiz.run.data.MyContentProvider;
import com.kodewiz.run.data.Order;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private Context mContext = this;
    private ArrayList<Order> mOrderList;
    private ListView mListView;
    private ArrayAdapter<Order> mAdapter;
    private Handler handler = new Handler();
    private static final int LOADER_ID = 1;

    public static String officeCoordinates = "12.910089, 77.635109";
    public static String shopCoordinates = "12.906082,77.638439";
    public static String destinationCoordinates = "12.904600, 77.644827";

    private boolean switchActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Order list");

        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                Toast.makeText(context, "Regeistration complete", Toast.LENGTH_SHORT).show();
            }
        };

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRegistrationComplete = sharedPreferences.getBoolean(QuickstartPreferences.REGISTRATION_COMPLETE, false);

        if (!isRegistrationComplete && checkPlayServices() && Utilities.isConnectedToInternet(this)) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        } else {
            mRegistrationProgressBar.setVisibility(View.GONE);
        }

        mListView = (ListView) findViewById(R.id.listView);
        mOrderList = new ArrayList<Order>();
        mAdapter = new OrderListAdapter(this, android.R.layout.simple_list_item_1, mOrderList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mOnItemClickListner_listView);
        mListView.setOnItemLongClickListener(mOnItemLongClickListener);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                handler.postDelayed(this, 60 * 1000);
            }
        }, 6 * 1000 );

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    public void onClick(View view){
        int id = view.getId();
        switch(id){
            case R.id.imageView_navigate:
                View parentRow = (View) view.getParent();
                int position = mListView.getPositionForView(parentRow);
                GlobalVariables.getInstance().setSelectedPack(mOrderList.get(position));

                // ToDo change coordinates appropriatelym
                // intent to start Google Maps navigation

                // uri for navigation
                Uri mapNavigationUri = Uri.parse("google.navigation:q=" + destinationCoordinates + "&mode=d/");

                // uri for direction with multiple destinations
                Uri mapDirectionUri = Uri.parse("https://www.google.com/maps/dir/" + officeCoordinates + "/" +
                        shopCoordinates + "/" + destinationCoordinates);

                Intent intent = new Intent(Intent.ACTION_VIEW, mapNavigationUri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);

                break;
            case R.id.fab :
                Intent i = new Intent(getBaseContext(), Activity_CallList.class);
                startActivity(i);
            default: break;
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String order = DBHelper.ID + " DESC";
        CursorLoader cursorLoader = new CursorLoader(mContext, MyContentProvider.ORDERS_CONTENT_URI, null, null, null, order);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        int i_name = c.getColumnIndexOrThrow(DBHelper.NAME);
        int i_email = c.getColumnIndexOrThrow(DBHelper.EMAIL);
        int i_item = c.getColumnIndexOrThrow(DBHelper.ITEM);
        int i_quantity = c.getColumnIndexOrThrow(DBHelper.QUANTITY);
        int i_address = c.getColumnIndexOrThrow(DBHelper.ADDRESS);
        int i_price = c.getColumnIndexOrThrow(DBHelper.PRICE);
        int i_description = c.getColumnIndexOrThrow(DBHelper.DESCRIPTION);
        int i_phone = c.getColumnIndexOrThrow(DBHelper.PHONE);
        int i_comment = c.getColumnIndexOrThrow(DBHelper.COMMENT);
        int i_order_time = c.getColumnIndexOrThrow(DBHelper.ORDER_TIME);
        int i_coordinates = c.getColumnIndexOrThrow(DBHelper.COORDINATES);

        mOrderList.clear();
        while(c.moveToNext()){
            Order newOrder = new Order(c.getString(i_name), c.getString(i_email), c.getString(i_item), c.getString(i_quantity), c.getString(i_address), c.getString(i_price)
            , c.getString(i_description), c.getString(i_phone), c.getString(i_comment), c.getString(i_order_time), c.getString(i_coordinates));
            mOrderList.add(newOrder);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private AdapterView.OnItemClickListener mOnItemClickListner_listView = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            GlobalVariables.getInstance().setSelectedPack(mOrderList.get(position));

            if(!switchActivity){
                Intent i = new Intent(mContext, Activity_OrderDetail.class);
                startActivity(i);
            }else{
                Intent i = new Intent(mContext, ScrollingActivity.class);
                startActivity(i);
            }
        }
    };

    private AdapterView.OnItemLongClickListener mOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            switchActivity = switchActivity ? false : true ;
            Toast.makeText(MainActivity.this, "Activity switched", Toast.LENGTH_SHORT).show();
            return true;
        }
    };

    /*Intent intent = new Intent(Intent.ACTION_VIEW,
            Uri.parse("http://maps.google.com/maps?saddr="+ shopCoordinates +"&daddr=" + destinationCoordinates));
    startActivity(intent);*/
}
