package com.kodewiz.run.data;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.kodewiz.run.activity.MainActivity;

import java.io.InputStream;
import java.io.OutputStream;

public class GlobalVariables extends Application {
    private static GlobalVariables instance;
    private static Order sSelectedOrder = null;

    // Thermal printer
    public static BluetoothAdapter mBluetoothAdapter;
    public static BluetoothSocket mSocket;
    public static BluetoothDevice mDevice;
    public static OutputStream mOutputStream;
    public static InputStream mInputStream;

    public static synchronized GlobalVariables getInstance() {

        if(instance == null){
            instance = new GlobalVariables();
        }
        return instance;
    }

    public void setSelectedOrders(Order order){
        sSelectedOrder = order;
    }

    public Order getSelectedOrders(){
        return sSelectedOrder;
    }

}
