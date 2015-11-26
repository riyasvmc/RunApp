package com.kodewiz.run;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilities {

    public static boolean isConnectedToInternet(AppCompatActivity activity){
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected){
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        return isConnected;
    }

    public static String getTimeSpanStringFromDate(String dateString){
        //String created_at = news.getCreated_at(); //"2011/11/12 16:05:06";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(dateString);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        if(date != null){
            return DateUtils.getRelativeTimeSpanString(date.getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString();
        }else{
            return null;
        }
    }

    public static Intent getDialIntent(String phoneNo) {
        String uri = "tel:" + phoneNo;
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(uri));
        return intent;
    }
}
