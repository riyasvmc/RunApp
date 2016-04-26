package com.kodewiz.run;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TimeUtils;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Utilities {

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream;
        Bitmap bitmap = null;
        try {
            inputStream = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    // Static method for checking device is connected to internet.
    public static boolean isConnectedToInternet(AppCompatActivity activity){
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected){
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        return isConnected;
    }

    // this method format Date string to human readable format (time span)
    public static String getTimeSpanStringFromDate(Context context, String dateString){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(dateString);
            return DateUtils.formatDateTime(context, date.getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME);

        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    // this method format Date string to human readable format (relative time span)
    public static String getRelativeTimeSpanStringFromDate(Context context, String dateString){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(dateString);
            return DateUtils.getRelativeDateTimeString(context, date.getTime(), DateUtils.DAY_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0).toString();

        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    // this method format Date string
    public static String formatDateTime(Context context, String dateString){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(dateString);
            return DateUtils.formatDateTime(context, date.getTime(), DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_TIME).toString();

        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    // converts Date string to Time string, example 10:10 AM
    public static String getTimeStringFromDate(String dateString){
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a");
        // hh:mm a
        try {
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    // return Intent for dial a phone
    public static Intent getDialIntent(String phoneNo) {
        String uri = "tel:" + phoneNo;
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(uri));
        return intent;
    }
}
