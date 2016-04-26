package com.kodewiz.run.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.kodewiz.run.QuickstartPreferences;
import com.kodewiz.run.R;
import com.kodewiz.run.SyncHelper;
import com.kodewiz.run.Utilities;
import com.kodewiz.run.data.MyContentProvider;
import com.kodewiz.run.gcm.MyGcmListenerService;
import com.kodewiz.run.gcm.RegistrationIntentService;

import java.text.DecimalFormat;

public class MainActivity extends ActivityCustom{

    // constants
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final int NOTIFICATION_ID = 0;
    private static final String TAG = "MainActivity";
    public static String COORDINATES_OFFICE = "12.910089, 77.635109";
    public static String COORDINATES_SHOP = "12.906082,77.638439";

    // instances
    private Context mContext = this;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private SharedPreferences sharedPreferences;

    // view instances
    private LinearLayout mLinearLayout;
    private ActionBar mActionBar;
    private Button mButtonRetry;
    private Button mButtonSync;
    private ProgressBar mRegistrationProgressBar;
    private static ProgressDialog mProgressDialog;
    private TextView mTotalAmount;
    private TextView mTextView_Orders;
    private CardView mCardView_Orders;
    private MyContentObserver mObserver;

    private double mLongTotalAmount;
    private String mStringCustomerCount = "0";
    private String mStringOrderCount = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(getBaseContext(), "Registration Completed", Toast.LENGTH_SHORT).show();
                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                mLinearLayout.setVisibility(View.VISIBLE);
            }
        };

        initViews();

        // register app on GCM server
        registerAppOnGCMServer();

    }

    private void initViews(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActionBar = getSupportActionBar();
        mLinearLayout = (LinearLayout) findViewById(R.id.main_layout);
        //mActionBar.hide();
        mButtonRetry = (Button) findViewById(R.id.button_retry);
        mButtonSync = (Button) findViewById(R.id.button_sync);
        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
    }

    private void updateUI(){
        queryContentResolver();
        setValuesToViews();
    }

    private void queryContentResolver(){
        Cursor c = getContentResolver().query(MyContentProvider.AMOUNT_CONTENT_URI, null, null, null, null);
        while (c.moveToNext()){
            mLongTotalAmount = 0;
            String priceCSV = "";
            priceCSV = c.getString(0);
            if(priceCSV != null) {
                String[] prices = priceCSV.split(",");
                int i = 0;
                for (String price : prices) {
                    try {
                        int p = Integer.parseInt(price.trim());
                        mLongTotalAmount += p;
                    } catch (Exception e) {
                        continue;
                    }
                    i++;
                }
            }
        }

        c = getContentResolver().query(MyContentProvider.CUSTOMER_COUNT_CONTENT_URI, null, null, null, null);
        while (c.moveToNext()){
            String cc = String.valueOf(c.getCount());
            mStringCustomerCount = (TextUtils.isEmpty(cc))? "0" : cc;
        }

        c = getContentResolver().query(MyContentProvider.ORDER_CONTENT_URI, null, null, null, null);
        while (c.moveToNext()){
            String oc = String.valueOf(c.getCount());
            mStringOrderCount = (TextUtils.isEmpty(oc))? "0" : oc;
        }
    }

    private void setValuesToViews(){
        mCardView_Orders = (CardView) findViewById(R.id.card_view_orders);
        mCardView_Orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ActivityOrder.newIntent(getBaseContext()));
            }
        });

        mTotalAmount = (TextView) findViewById(R.id.total_amount);
        mTotalAmount.setText("\u20B9" + getFormatedAmountString(mLongTotalAmount));

        mTextView_Orders = (TextView) findViewById(R.id.textview_orders);
        mTextView_Orders.setText(Html.fromHtml("<b>"+ mStringOrderCount +"</b> Orders"));

    }

    private String getFormatedAmountString(Double amount){
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        return decimalFormat.format(amount);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateUI();

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

        // register content observer
        mObserver = new MyContentObserver(new Handler());
        getContentResolver().registerContentObserver(MyContentProvider.CONTENT_URI, true, mObserver);

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        getContentResolver().unregisterContentObserver(mObserver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id){
            case R.id.action_about:
                Toast.makeText(this, "This is Run App", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    public void onClick(View view){
        int id = view.getId();
        Intent i;
        switch(id){
            case R.id.fab:
                i = new Intent(getBaseContext(), ActivityOrder.class);
                startActivity(i);
                break;
            case R.id.button_retry :
                registerAppOnGCMServer();
                break;
            case R.id.button_sync:
                syncDatabase();
                break;
            default: break;
        }
    }

    private void registerAppOnGCMServer(){

        mButtonRetry.setVisibility(View.GONE);
        mRegistrationProgressBar.setVisibility(View.VISIBLE);

        boolean isRegistrationComplete = sharedPreferences.getBoolean(QuickstartPreferences.REGISTRATION_COMPLETE, false);

        // check weather the app is registered with GCM Server
        if(!isRegistrationComplete){

            // check Internet & Google play services available
            if(Utilities.isConnectedToInternet(this) && checkPlayServices()){
                // Start IntentService to register App with GCM Server
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }else {
                mButtonRetry.setVisibility(View.VISIBLE);
                mRegistrationProgressBar.setVisibility(View.GONE);
            }
        }else{
            mRegistrationProgressBar.setVisibility(View.GONE);
            mLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    private void syncDatabase(){
        if(Utilities.isConnectedToInternet(this)){
            mProgressDialog = ProgressDialog.show(this, null, "Syncing");
            SyncHelper.syncWithChickenAtDoorDatabase(getBaseContext());
        }else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
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
                finish();
            }
            return false;
        }
        return true;
    }

    private class MyContentObserver extends ContentObserver{
        public MyContentObserver(Handler handler){
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            updateUI();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            updateUI();
        }
    }

    public static void dismissDialog(){
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }
}