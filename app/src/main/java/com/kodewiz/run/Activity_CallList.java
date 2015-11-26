package com.kodewiz.run;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.kodewiz.run.data.Order;

public class Activity_CallList extends AppCompatActivity {

    Order order;
    String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Call office");
    }
}
