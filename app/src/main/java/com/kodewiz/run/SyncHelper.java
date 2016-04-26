package com.kodewiz.run;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kodewiz.run.activity.MainActivity;
import com.kodewiz.run.data.DBHelper;
import com.kodewiz.run.data.Data;
import com.kodewiz.run.data.MyContentProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import hugo.weaving.DebugLog;

public class SyncHelper {

    public static void syncWithChickenAtDoorDatabase(final Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Data.URL_POST_SYNC, getIDsJSONObject(context), new Response.Listener<JSONObject>() {
            @DebugLog
            @Override
            public void onResponse(JSONObject response) {
                JSONArray array = response.optJSONArray("orders");
                ContentValues[] values = OrdersInsertHandler.convertArrayToContentValues(array);
                if(values != null){
                    int mInsertCount = context.getContentResolver().bulkInsert(MyContentProvider.ORDERS_CONTENT_URI, values);
                }

                String mIdsToRetain = response.optString("ids");
                deleteRowsExcludingTheIds(context, mIdsToRetain);
                MainActivity.dismissDialog();
            }
        }, new Response.ErrorListener() {
            @DebugLog
            @Override
            public void onErrorResponse(VolleyError error) {
                MainActivity.dismissDialog();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private static void deleteRowsExcludingTheIds(Context context, String ids){
        String selection = DBHelper.OrdersTable.ORDER_ID + " NOT IN " + "(" + ids + ")";
        context.getContentResolver().delete(MyContentProvider.ORDERS_CONTENT_URI, selection, null);
    }

    public static JSONObject getIDsJSONObject(Context context){
        String mIds = "";
        Cursor mCursor = context.getContentResolver().query(MyContentProvider.ORDERS_CONTENT_URI, new String[] {DBHelper.OrdersTable.ORDER_ID}, null, null, null);

        while (mCursor.moveToNext()){
            String mOrderId = mCursor.getString(mCursor.getColumnIndexOrThrow(DBHelper.OrdersTable.ORDER_ID));
            if(!TextUtils.isEmpty(mOrderId))
                mIds += mOrderId + (mCursor.isLast() ? "" : ",");
        }

        if(mIds.equals("")){
            mIds = "-1";
        }

        JSONObject mJsonIDs = new JSONObject();
        try {
            mJsonIDs.put("ids", mIds);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mJsonIDs;
    }
}
