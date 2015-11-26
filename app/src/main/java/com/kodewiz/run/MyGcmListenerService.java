package com.kodewiz.run;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.kodewiz.run.data.DBHelper;
import com.kodewiz.run.data.MyContentProvider;

import org.json.JSONException;
import org.json.JSONObject;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String order = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + order);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        saveOrderDetailsToDatabase(order);
        sendNotification(order);
        // [END_EXCLUDE]
    }

    private void saveOrderDetailsToDatabase(String message) {

        try {
            JSONObject j = new JSONObject(message);

            String name = j.optString(DBHelper.NAME);
            String email = j.optString(DBHelper.EMAIL);
            String item = j.optString(DBHelper.ITEM);
            String quantity = j.optString(DBHelper.QUANTITY);
            String address = j.optString(DBHelper.FLAT) + " " +
                    j.optString(DBHelper.ADDRESS) + " " +
                    j.optString(DBHelper.LANDMARK) + " " +
                    j.optString(DBHelper.AREA) + " " +
                    j.optString(DBHelper.ZIP);
            String price = j.optString(DBHelper.PRICE);
            String description = j.optString(DBHelper.DESCRIPTION);
            String phone = j.optString(DBHelper.PHONE);
            String comment = j.optString(DBHelper.COMMENT);
            String order_time = j.optString(DBHelper.ORDER_TIME);
            String coordinates = j.optString(DBHelper.COORDINATES);

            ContentValues cv = new ContentValues();
            cv.put(DBHelper.NAME, name);
            cv.put(DBHelper.EMAIL, email);
            cv.put(DBHelper.ITEM, item);
            cv.put(DBHelper.QUANTITY, quantity);
            cv.put(DBHelper.ADDRESS, address);
            cv.put(DBHelper.PRICE, price);
            cv.put(DBHelper.DESCRIPTION, description);
            cv.put(DBHelper.PHONE, phone);
            cv.put(DBHelper.COMMENT, comment);
            cv.put(DBHelper.ORDER_TIME, order_time);
            cv.put(DBHelper.COORDINATES, coordinates);
            Uri insertUri = getContentResolver().insert(MyContentProvider.ORDERS_CONTENT_URI, cv);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri chickenSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.crow);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Hey, Got An Order.")
                .setContentText("Run to Sun Chicken Store.")
                .setAutoCancel(true)
                .setSound(chickenSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
