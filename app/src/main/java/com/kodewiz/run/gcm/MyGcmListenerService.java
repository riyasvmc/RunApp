package com.kodewiz.run.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GcmListenerService;
import com.kodewiz.run.OrdersInsertHandler;
import com.kodewiz.run.R;
import com.kodewiz.run.SyncHelper;
import com.kodewiz.run.activity.ActivityOrder;
import com.kodewiz.run.activity.MainActivity;
import com.kodewiz.run.data.DBHelper;
import com.kodewiz.run.data.Data;
import com.kodewiz.run.data.MyContentProvider;

import org.json.JSONArray;
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
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

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
        saveOrderDetailsToDatabase(message);
        SyncHelper.syncWithChickenAtDoorDatabase(getBaseContext());
    }

    private void saveOrderDetailsToDatabase(String message) {

        if(!TextUtils.isEmpty(message)){
            try {
                JSONObject j = new JSONObject(message);
                if(j != null) {
                    String order_id = j.optString(DBHelper.OrdersTable.ORDER_ID);
                    String name = j.optString(DBHelper.OrdersTable.NAME);
                    String mobile = j.optString("phone");
                    String item = j.optString(DBHelper.OrdersTable.ITEM);
                    String price = j.optString(DBHelper.OrdersTable.PRICE);
                    String quantity = j.optString(DBHelper.OrdersTable.QUANTITY);
                    String address = j.optString(DBHelper.OrdersTable.ADDRESS);
                    String description = j.optString(DBHelper.OrdersTable.DESCRIPTION);
                    String comment = j.optString(DBHelper.OrdersTable.COMMENT);
                    String coordinates = j.optString(DBHelper.OrdersTable.COORDINATES);
                    String date = j.optString("order_time");

                    ContentValues cv = new ContentValues();
                    cv.put(DBHelper.OrdersTable.ORDER_ID, order_id);
                    cv.put(DBHelper.OrdersTable.NAME, name);
                    cv.put(DBHelper.OrdersTable.MOBILE, mobile);
                    cv.put(DBHelper.OrdersTable.ITEM, item);
                    cv.put(DBHelper.OrdersTable.PRICE, price);
                    cv.put(DBHelper.OrdersTable.QUANTITY, quantity);
                    cv.put(DBHelper.OrdersTable.ADDRESS, address);
                    cv.put(DBHelper.OrdersTable.DESCRIPTION, description);
                    cv.put(DBHelper.OrdersTable.COMMENT, comment);
                    cv.put(DBHelper.OrdersTable.COORDINATES, coordinates);
                    cv.put(DBHelper.OrdersTable.DATE, date);

                    Uri insertUri = getContentResolver().insert(MyContentProvider.ORDERS_CONTENT_URI, cv);

                    sendNotification(j);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create and show a simple notification containing the order details
     *
     * @param j GCM message as json Object.
     */
    private void sendNotification(JSONObject j) {
        Intent intent = new Intent(this, ActivityOrder.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        // Uri for default notification sound
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // Uri for custom chicken crow sound
        Uri chickenSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.crow);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(j.optString(DBHelper.OrdersTable.NAME))
                .setContentText(j.optString(DBHelper.OrdersTable.ITEM))
                .setAutoCancel(true)
                .setSound(chickenSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(MainActivity.NOTIFICATION_ID, notificationBuilder.build());
    }
}
