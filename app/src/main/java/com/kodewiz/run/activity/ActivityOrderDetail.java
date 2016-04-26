package com.kodewiz.run.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kodewiz.run.R;
import com.kodewiz.run.Utilities;
import com.kodewiz.run.data.DBHelper;
import com.kodewiz.run.data.GlobalVariables;
import com.kodewiz.run.data.MyContentProvider;
import com.kodewiz.run.data.Order;
import com.kodewiz.run.printer.PrinterCommands;
import com.kodewiz.run.view.ProgressWheel;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.UUID;

import hugo.weaving.DebugLog;

public class ActivityOrderDetail extends ActivityCustom implements Runnable{

    private static final String TAG = "Riyas Vmc";
    private static final String SUCCESS = "success";
    private static final int BLUETOOTH_ENABLE = 18;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    Order mOrder;
    ActionBar actionBar;
    TextView mTextViewPrice;
    TextView mTextViewAddress;
    TextView mTextViewTime;
    TextView mTextView_Description;
    TextView mTextViewItemQuantity;
    ProgressWheel progressWheelMinute;
    private ProgressDialog mBluetoothConnectProgressDialog;
    LinearLayout container;
    Button button_phone;
    FloatingActionButton fab;
    Handler handler = new Handler();
    ProgressDialog mProgressDialog;
    private File mFile;

    // Thermal printing
    // refer: http://new-grumpy-mentat.blogspot.in/2014/06/java-escpos-image-printing.html
    private BitSet dots;
    private int mWidth;
    private int mHeight;
    private String mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        initializeViews();
        mOrder = getPassedOrder();

        // Toast.makeText(this, "Order id: " + mOrder.getOrderId(), Toast.LENGTH_SHORT).show();
        actionBar = getSupportActionBar();
        actionBar.setTitle("Order list");
        actionBar.setElevation(0f);

        // dismiss notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(MainActivity.NOTIFICATION_ID);

        if (mOrder != null) {
            setValuesToViews();

            if (mOrder.getMobile() != null) {
                container.invalidate();
                button_phone.setVisibility(View.VISIBLE);
                button_phone.setText("DIAL " + mOrder.getMobile());
            } else {
                button_phone.setVisibility(View.GONE);
                container.invalidate();
            }
        } else {
            Toast.makeText(this, "No object passed", Toast.LENGTH_LONG).show();
        }
    }

    private void setValuesToViews() {
        if (mOrder != null) {
            actionBar.setTitle(mOrder.getName());
            mTextViewPrice.setText(mOrder.getFormatedTotalPrice());
            mTextViewAddress.setText(mOrder.getAddress());
            mTextViewItemQuantity.setText(mOrder.getItemQtyString());
            mTextViewTime.setText(mOrder.getDate());
            mTextView_Description.setText(mOrder.getDescription());

            if (mOrder.getTimeRemainsInMinutes() > 0) {

                progressWheelMinute.setVisibility(View.VISIBLE);
                container.invalidate();

                progressWheelMinute.setProgress(mOrder.getTimeInRadians());
                progressWheelMinute.setText(mOrder.getTimeRemainsInMinutes() + "m");
            } else {
                progressWheelMinute.setVisibility(View.GONE);
                container.invalidate();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setValuesToViews();
        handler.postDelayed(mViewsValueSetterRunnable, ActivityOrder.VIEW_REFRESH_PERIOD);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(mViewsValueSetterRunnable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_order_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id) {
            case R.id.action_print_order:
                printOrderReceipt();
                break;
        }
        return true;
    }

    private void initializeViews() {

        // setting actionBar Up enabled
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTextViewPrice = (TextView) findViewById(R.id.textView_price);
        mTextViewAddress = (TextView) findViewById(R.id.textView_address);
        mTextViewTime = (TextView) findViewById(R.id.textView_datetime);
        mTextView_Description = (TextView) findViewById(R.id.textView_description);
        mTextViewItemQuantity = (TextView) findViewById(R.id.textView_item_qty_string);
        progressWheelMinute = (ProgressWheel) findViewById(R.id.progress_wheel_min);
        container = (LinearLayout) findViewById(R.id.linearLayout_container);
        button_phone = (Button) findViewById(R.id.button_phone);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    private Order getPassedOrder() {
        GlobalVariables globalVariables = GlobalVariables.getInstance();
        return globalVariables.getSelectedOrders();
    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_phone:
                if (mOrder.getMobile() != null) {
                    startActivity(Utilities.getDialIntent(mOrder.getMobile()));
                } else {
                    Toast.makeText(this, "No Phone", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_generate_reciept:
                // generateReciept();
                break;
            case R.id.action_print_order:
                printOrderReceipt();
                break;
            case R.id.fab:
                onClick_fab();
                break;
        }
    }

    private void printOrderReceipt() {
                findBT();
    }

    private void onClick_fab() {
        Intent intent = new Intent(Intent.ACTION_VIEW, mOrder.getNavigationUri(mOrder.getCoordinates()));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    private Runnable mViewsValueSetterRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("Riyas", "run()");
            setValuesToViews();
            handler.postDelayed(this, ActivityOrder.VIEW_REFRESH_PERIOD);
        }
    };

    // Thermal Printing section
    private void sendData() throws IOException {

        if(GlobalVariables.getInstance().mOutputStream != null) {
            GlobalVariables.getInstance().mOutputStream.write(PrinterCommands.INIT);
            GlobalVariables.getInstance().mOutputStream.write(PrinterCommands.SELECT_FONT_B);
            GlobalVariables.getInstance().mOutputStream.write(PrinterCommands.JUSTIFY_CENTER);

            print_image();
            String seperator = "\n------------------------------------------\n";

            GlobalVariables.getInstance().mOutputStream.write(("www.chickenatdoor.com\n\n").getBytes());
            GlobalVariables.getInstance().mOutputStream.write(PrinterCommands.SELECT_FONT_B);
            GlobalVariables.getInstance().mOutputStream.write(("Order Id: " + mOrder.getOrderId() + "\n").getBytes());
            GlobalVariables.getInstance().mOutputStream.write(PrinterCommands.EMPHASIZE);
            GlobalVariables.getInstance().mOutputStream.write(PrinterCommands.SELECT_FONT_A);
            GlobalVariables.getInstance().mOutputStream.write((mOrder.getName()).getBytes());

            GlobalVariables.getInstance().mOutputStream.write(PrinterCommands.SELECT_FONT_B);
            GlobalVariables.getInstance().mOutputStream.write(seperator.getBytes());


            GlobalVariables.getInstance().mOutputStream.write(PrinterCommands.JUSTIFY_LEFT);
            GlobalVariables.getInstance().mOutputStream.write(PrinterCommands.INIT);
            GlobalVariables.getInstance().mOutputStream.write(PrinterCommands.SELECT_FONT_B);
            GlobalVariables.getInstance().mOutputStream.write(mOrder.getItemQtyString().getBytes());
            GlobalVariables.getInstance().mOutputStream.write(PrinterCommands.JUSTIFY_CENTER);

            String message = seperator +
                    "Total Price: " + mOrder.getPrice() + "\n" +
                    "Time: " + Utilities.formatDateTime(getBaseContext(), mOrder.getDate());
            GlobalVariables.getInstance().mOutputStream.write(message.getBytes());
            GlobalVariables.getInstance().mOutputStream.write(PrinterCommands.FEED_LINE);
            GlobalVariables.getInstance().mOutputStream.write(PrinterCommands.FEED_LINE);
            GlobalVariables.getInstance().mOutputStream.write(PrinterCommands.FEED_LINE);
            GlobalVariables.getInstance().mOutputStream.write(PrinterCommands.FEED_LINE);
            GlobalVariables.getInstance().mOutputStream.write(PrinterCommands.FEED_LINE);

            showToast("Data sent");
        }else{
            Toast.makeText(this, "Output Stream is empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendAllOrdersData() throws IOException {

        GlobalVariables.getInstance().mOutputStream.write(PrinterCommands.INIT);
        GlobalVariables.getInstance().mOutputStream.write(PrinterCommands.SELECT_FONT_B);

        // query data
        Cursor cursor = getContentResolver().query(MyContentProvider.ORDER_CONTENT_URI, new String[] { "DISTINCT " + DBHelper.OrdersTable.NAME, DBHelper.OrdersTable.MOBILE }, null, null, DBHelper.OrdersTable._ID);
        int i = 0;
        while (cursor.moveToNext()){
            int i_title = cursor.getColumnIndexOrThrow(DBHelper.OrdersTable.NAME);
            int i_mobile = cursor.getColumnIndexOrThrow(DBHelper.OrdersTable.MOBILE);
            String title = cursor.getString(i_title);
            String mobile = cursor.getString(i_mobile);
            GlobalVariables.getInstance().mOutputStream.write((String.valueOf(i) + ". " + title + " " + mobile + "\n").getBytes());
            i++;
        }
        showToast("Data sent");
    }

    private void print_image() throws IOException {
        Bitmap bmp = Utilities.getBitmapFromAsset(getBaseContext(), "icon.bmp");
        convertBitmap(bmp);
        GlobalVariables.getInstance().mOutputStream.write(PrinterCommands.SET_LINE_SPACING_24);

        int offset = 0;
        while (offset < bmp.getHeight()) {
            GlobalVariables.getInstance().mOutputStream.write(PrinterCommands.SELECT_BIT_IMAGE_MODE);
            for (int x = 0; x < bmp.getWidth(); ++x) {

                for (int k = 0; k < 3; ++k) {

                    byte slice = 0;
                    for (int b = 0; b < 8; ++b) {
                        int y = (((offset / 8) + k) * 8) + b;
                        int i = (y * bmp.getWidth()) + x;
                        boolean v = false;
                        if (i < dots.length()) {
                            v = dots.get(i);
                        }
                        slice |= (byte) ((v ? 1 : 0) << (7 - b));
                    }
                    GlobalVariables.getInstance().mOutputStream.write(slice);
                }
            }
            offset += 24;
            GlobalVariables.getInstance().mOutputStream.write(PrinterCommands.FEED_LINE);
        }
        GlobalVariables.getInstance().mOutputStream.write(PrinterCommands.SET_LINE_SPACING_30);
    }


    public String convertBitmap(Bitmap inputBitmap) {

        mWidth = inputBitmap.getWidth();
        mHeight = inputBitmap.getHeight();

        convertArgbToGrayscale(inputBitmap, mWidth, mHeight);
        mStatus = "ok";
        return mStatus;

    }

    private void convertArgbToGrayscale(Bitmap bmpOriginal, int width,
                                        int height) {
        int pixel;
        int k = 0;
        int B = 0, G = 0, R = 0;
        dots = new BitSet();
        try {

            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width; y++) {
                    // get one pixel color
                    pixel = bmpOriginal.getPixel(y, x);

                    // retrieve color of all channels
                    R = Color.red(pixel);
                    G = Color.green(pixel);
                    B = Color.blue(pixel);
                    // take conversion up to one single value by calculating
                    // pixel intensity.
                    R = G = B = (int) (0.299 * R + 0.587 * G + 0.114 * B);
                    // set bit into bitset, by calculating the pixel's luma
                    if (R < 55) {
                        dots.set(k);//this is the bitset that i'm printing
                    }
                    k++;

                }


            }


        } catch (Exception e) {
            // TODO: handle exception
            Log.e(TAG, e.toString());
        }
    }

    private void findBT()
    {
        GlobalVariables.getInstance().mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(GlobalVariables.getInstance().mBluetoothAdapter == null)
        {
            // No Bluetooth device found!
            return;
        }else {
            if (!GlobalVariables.getInstance().mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, BLUETOOTH_ENABLE);
            }else {
                if(GlobalVariables.getInstance().mDevice == null) {
                    connectDevice();
                }else{
                    try {
                        sendData();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @DebugLog
    private void connectDevice(){
        /*Set<BluetoothDevice> pairedDevices = GlobalVariables.getInstance().mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("BlueTooth Printer")) {
                    GlobalVariables.getInstance().mDevice = device;
                    openBT();
                    break;
                }
            }
        }*/
        Intent connectIntent = new Intent(ActivityOrderDetail.this, ActivityDeviceList.class);
        startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case BLUETOOTH_ENABLE:
                if(resultCode == Activity.RESULT_OK){
                    connectDevice();
                }else{
                    Toast.makeText(this, "Access denied!", Toast.LENGTH_SHORT).show();
                }

                break;

            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle mExtra = data.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    GlobalVariables.getInstance().mDevice = GlobalVariables.getInstance().mBluetoothAdapter.getRemoteDevice(mDeviceAddress);

                    mBluetoothConnectProgressDialog = ProgressDialog.show(this, "Connecting...",
                            GlobalVariables.getInstance().mDevice.getName(), true, false);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;
        }
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void dismissProgressDialog(){
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void run() {
        try {
            GlobalVariables.getInstance().mSocket = GlobalVariables.getInstance().mDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            GlobalVariables.getInstance().mBluetoothAdapter.cancelDiscovery();
            GlobalVariables.getInstance().mSocket.connect();
            GlobalVariables.getInstance().mOutputStream = GlobalVariables.getInstance().mSocket.getOutputStream();
            GlobalVariables.getInstance().mInputStream = GlobalVariables.getInstance().mSocket.getInputStream();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(GlobalVariables.getInstance().mSocket);
            mBluetoothConnectProgressDialog.cancel();
            //Toast.makeText(ActivityOrderDetail.this, "Connecting failed", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(ActivityOrderDetail.this, "DeviceConnected", Toast.LENGTH_LONG).show();
            try {
                sendData();
            }catch (Exception e){

            }
        }
    };
}