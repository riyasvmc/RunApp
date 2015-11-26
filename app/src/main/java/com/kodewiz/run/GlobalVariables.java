package com.kodewiz.run;

import android.app.Application;

import com.kodewiz.run.data.Order;

public class GlobalVariables extends Application {
    private static GlobalVariables instance;
    private static Order selectedOrder = null;

    public static synchronized GlobalVariables getInstance() {
        if(instance == null){
            instance = new GlobalVariables();
        }
        return instance;
    }

    public void setSelectedPack(Order order){
        selectedOrder = order;
    }
    public Order getSelectedOrder(){
        return selectedOrder;
    }
}
