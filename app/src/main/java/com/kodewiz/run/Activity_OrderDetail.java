package com.kodewiz.run;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kodewiz.run.data.Order;

import java.util.Arrays;
import java.util.List;

public class Activity_OrderDetail extends AppCompatActivity {

    Order order;
    String item = "";
    String quantity = "";
    String price;
    String coordinates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0f);
        actionBar.setDisplayShowTitleEnabled(false);

        order = getPassedPack();
        if(order != null) {
            item = order.getItem();
            quantity = order.getQuantity();
            price = order.getPrice();

            // convert items & quanity strings to list
            if (item != null && quantity != null) {
                List<String> items = Arrays.asList(item.split("\\s*,\\s*"));
                List<String> quantities = Arrays.asList(quantity.split("\\s*,\\s*"));

                TextView textView_item_quantity = (TextView) findViewById(R.id.textView_item_quantity);
                String item_qty = "";
                for (int i = 0; i < items.size(); i++) {
                    item_qty += quantities.get(i) + " Kg " + items.get(i) + "; ";
                }
                textView_item_quantity.setText(item_qty);
            }

            TextView textView_price = (TextView) findViewById(R.id.textView_price);
            textView_price.setText("\u20B9 " + price);

            TextView textView_name = (TextView) findViewById(R.id.textView_name);
            textView_name.setText(order.getName());

            TextView textView_address = (TextView) findViewById(R.id.textView_address);
            textView_address.setText("Address: " + order.getAddress());

            TextView textView_coordinates = (TextView) findViewById(R.id.textView_coordinates);
            textView_coordinates.setText("Coordinates: " + order.getCoordinates());

            TextView textView_time = (TextView) findViewById(R.id.textView_datetime);
            textView_time.setText(order.getOrderTime());

            LinearLayout container = (LinearLayout) findViewById(R.id.linearLayout_container);
            Button button_phone = (Button) findViewById(R.id.button_phone);

            if(order.getPhone() != null) {
                container.invalidate();
                button_phone.setVisibility(View.VISIBLE);
                button_phone.setText("DIAL " + order.getPhone());
            }
            else {
                button_phone.setVisibility(View.GONE);
                container.invalidate();
            }

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(mOnClickListener);

            coordinates = order.getCoordinates();
        }
    }

    private Order getPassedPack(){
        GlobalVariables globalVariables = GlobalVariables.getInstance();
        return globalVariables.getSelectedOrder();
    }

    public void onClick(View view){
        int id = view.getId();
        switch (id){
            case R.id.button_phone:
                if(order.getPhone() != null){
                    startActivity(Utilities.getDialIntent(order.getPhone()));
                }else{
                    Toast.makeText(this, "No Phone", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // uri for navigation
            Uri mapNavigationUri = Uri.parse("google.navigation:q=" + coordinates + "&mode=d/");
            Uri mapDirectionUri = Uri.parse("https://www.google.com/maps/dir/" + MainActivity.officeCoordinates + "/" +
                    MainActivity.shopCoordinates + "/" + MainActivity.destinationCoordinates);

            Intent intent = new Intent(Intent.ACTION_VIEW, mapNavigationUri);
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        }
    };
}
