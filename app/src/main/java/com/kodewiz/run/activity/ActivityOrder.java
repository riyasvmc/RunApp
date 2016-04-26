package com.kodewiz.run.activity;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.TextView;

import com.kodewiz.run.R;
import com.kodewiz.run.adapter.OrdersRecyclerAdapter;
import com.kodewiz.run.data.DBHelper;
import com.kodewiz.run.data.GlobalVariables;
import com.kodewiz.run.data.MyContentProvider;
import com.kodewiz.run.data.Order;

import java.util.ArrayList;

public class ActivityOrder extends ActivityCustom implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final int VIEW_REFRESH_PERIOD = 60 * 1000; // 60 second

    private static final String TAG = "MainActivity";
    private Context mContext = this;
    private ArrayList<Order> mList;
    private RecyclerView mRecyclerView;
    private OrdersRecyclerAdapter mAdapter;
    private Handler handler = new Handler();
    private static final int LOADER_ID = 1;

    // searching
    public static String mQuery = "";
    private MenuItem mSearchItem;
    private SearchView mSearchView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // setting actionBar Up enabled
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTextView = (TextView) findViewById(R.id.textView);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mList = new ArrayList<>();
        mAdapter = new OrdersRecyclerAdapter(this, mList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        // mRecyclerView.setOnItemClickListener(mOnItemClickListner_recyclerView);
        // mRecyclerView.setEmptyView(findViewById(R.id.empty));

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        mAdapter.notifyDataSetChanged();
        handler.postDelayed(mViewsValueSetterRunnable, VIEW_REFRESH_PERIOD);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(mViewsValueSetterRunnable);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = DBHelper.OrdersTable.NAME + " like ? or " + DBHelper.OrdersTable.NAME + " like ? ";
        String[] selectionArgs = new String[] { mQuery+"%", "% " +mQuery + "%"};

        String order = DBHelper.OrdersTable.DATE + " DESC";
        CursorLoader cursorLoader = new CursorLoader(mContext, MyContentProvider.ORDERS_CONTENT_URI, null, selection, selectionArgs, order);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        // check for empty counter and toggle visibility of recyclerview
        if(c.getCount() == 0){
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText("No Results Matching Your Search \"" + mQuery + "\"");
            mRecyclerView.setVisibility(View.GONE);
        }else{
            mTextView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        int i_id = c.getColumnIndexOrThrow(DBHelper.OrdersTable._ID);
        int i_orderId = c.getColumnIndexOrThrow(DBHelper.OrdersTable.ORDER_ID);
        int i_name = c.getColumnIndexOrThrow(DBHelper.OrdersTable.NAME);
        int i_mobile = c.getColumnIndexOrThrow(DBHelper.OrdersTable.MOBILE);
        int i_item = c.getColumnIndexOrThrow(DBHelper.OrdersTable.ITEM);
        int i_price = c.getColumnIndexOrThrow(DBHelper.OrdersTable.PRICE);
        int i_qty = c.getColumnIndexOrThrow(DBHelper.OrdersTable.QUANTITY);
        int i_address = c.getColumnIndexOrThrow(DBHelper.OrdersTable.ADDRESS);
        int i_description = c.getColumnIndexOrThrow(DBHelper.OrdersTable.DESCRIPTION);
        int i_comment = c.getColumnIndexOrThrow(DBHelper.OrdersTable.COMMENT);
        int i_coordinates = c.getColumnIndexOrThrow(DBHelper.OrdersTable.COORDINATES);
        int i_date = c.getColumnIndexOrThrow(DBHelper.OrdersTable.DATE);

        mList.clear();
        while(c.moveToNext()){
            // make item object
            Order newOrder = new Order(c.getString(i_id), c.getString(i_orderId), c.getString(i_name), c.getString(i_mobile), c.getString(i_item), c.getString(i_price)
            , c.getString(i_qty), c.getString(i_address), c.getString(i_coordinates), c.getString(i_description), c.getString(i_comment), c.getString(i_date));

            // adding item to array list
            mList.add(newOrder);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private Runnable mViewsValueSetterRunnable = new Runnable() {
        @Override
        public void run() {
            mAdapter.notifyDataSetChanged();
            handler.postDelayed(this, ActivityOrder.VIEW_REFRESH_PERIOD);
        }
    };

    @TargetApi(14)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_orders, menu);
        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        mSearchItem = menu.findItem(R.id.action_search);

        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        mSearchView.setId(R.id.action_search);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setOnQueryTextListener(searchTextChangeListener);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private SearchView.OnQueryTextListener searchTextChangeListener = new SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextChange(String query) {

            if(query.toString() != ""){
                mQuery = query;
                restartLoader();
            }else{
                mQuery = null;
                restartLoader();
            }
            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            hideKeyboard();
            return true;
        }
    };

    private void restartLoader(){
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static Intent newIntent(Context context){
        Intent i = new Intent(context, ActivityOrder.class);
        return i;
    }
}