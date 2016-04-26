package com.kodewiz.run.data;

import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Order implements Serializable{

    String mId;
    String mOrderId;
    String mName;
    String mMobile;
    String mItem;
    String mPrice;
    String mQuantity;
    String mAddress;
    String mCoordinates;
    String mDescription;
    String mComment;
    String mDate;

    public static final int DELIVERY_TIME_IN_MINUTES = 45;

    public Order(String id, String orderId, String name, String mobile, String item, String price, String quantity, String address, String coordinates, String description, String comment, String date) {
        mId = id;
        mOrderId = orderId;
        mName = name;
        mMobile = mobile;
        mItem = item;
        mPrice = price;
        mQuantity = quantity;
        mAddress = address;
        mCoordinates = coordinates;
        mDescription = description;
        mComment = comment;
        mDate = date;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getOrderId() {
        return mOrderId;
    }

    public void setOrderId(String orderId) {
        mOrderId = orderId;
    }

    public String getName() {
        return WordUtils.capitalize(mName);
    }

    public void setName(String name) {
        mName = name;
    }

    public String getMobile() {
        return mMobile;
    }

    public void setMobile(String mobile) {
        mMobile = mobile;
    }

    public String getItem() {
        return WordUtils.capitalize(mItem);
    }

    public void setItem(String item) {
        mItem = item;
    }

    public String getPrice() {
        return mPrice.trim();
    }

    public void setPrice(String price) {
        mPrice = price;
    }

    public String getQuantity() {
        return mQuantity;
    }

    public void setQuantity(String quantity) {
        mQuantity = quantity;
    }

    public String getAddress() {
        return WordUtils.capitalize(mAddress).trim();
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getCoordinates() {
        return mCoordinates;
    }

    public void setCoordinates(String coordinates) {
        mCoordinates = coordinates;
    }

    public String getDescription() {
        return "Description: " + mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public int getTotalPrice(){
        int total = 0;
        String[] items = getPrice().split(",");
        for(String item : items){
            item.trim();
            try{
                total += Integer.valueOf(item);
            }catch (Exception e){
                return 0;
            }
        }
        return total;
    }

    public String getFormatedTotalPrice(){
        return "\u20B9" + getTotalPrice();
    }

    public static Uri getNavigationUri(String coordinates){
        // uri for navigation
        Uri uri = Uri.parse("google.navigation:q=" + coordinates + "&mode=d/");
        /*Uri mapDirectionUri = Uri.parse("https://www.google.com/maps/dir/" + MainActivity.COORDINATES_OFFICE + "/" +
                MainActivity.COORDINATES_SHOP + "/" + order.getCoordinates());*/
        return uri;
    }

    public int getTimeRemainsInMinutes(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(getDate());
                if(date != null){
                    String date_string = date.toString();
                    long mCurrentMillis = System.currentTimeMillis();
                    long mDateMillis = date.getTime();
                    long mDiff = mCurrentMillis - mDateMillis;
                    int timeElapsedInMinutes = (int)((System.currentTimeMillis() - date.getTime())/1000/60);
                    int timeRemainsInMinutes = DELIVERY_TIME_IN_MINUTES - timeElapsedInMinutes;
                    return timeRemainsInMinutes < 0 ? 0 : timeRemainsInMinutes;
                }else{
                    return 0;
                }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTimeInRadians(){
        int radian = (getTimeRemainsInMinutes()*360)/DELIVERY_TIME_IN_MINUTES;
        Log.d("Riyas", "Radians: " + radian);
        return radian;
    }

    public String getItemQtyString(){
        String item_qty_string = "";
        if (getItem() != null && getQuantity() != null) {
            List<String> items = Arrays.asList(getItem().split("\\s*,\\s*"));
            List<String> quantities = Arrays.asList(getQuantity().split("\\s*,\\s*"));
            try{
                for (int i = 0; i < items.size(); i++) {
                    item_qty_string += "- " + quantities.get(i) + " Kg " + items.get(i) + "\n";
                }
            }catch (Exception e){
                Log.d("Riyas", e.getMessage());
            }
        }
        return item_qty_string;
    }
}
