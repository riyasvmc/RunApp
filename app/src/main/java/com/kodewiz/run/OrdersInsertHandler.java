package com.kodewiz.run;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.kodewiz.run.data.DBHelper;
import com.kodewiz.run.data.MyContentProvider;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Riyas V on 1/12/2016.
 */
public class OrdersInsertHandler {

    private static Context mContext;

    public OrdersInsertHandler(Context context){
        mContext = context;
    }

    public static int insertOrdersToDatabase(SQLiteDatabase db, ContentValues[] values){
        int numInserted= 0;
        db.beginTransaction();
        try {
            String sql = "INSERT OR REPLACE INTO " + DBHelper.OrdersTable.TABLE_NAME + " (order_id, name, mobile, item, price, quantity, address, description, comment, coordinates, date) VALUES (?,?,?,?,?,?,?,?,?,?,?);";
            SQLiteStatement insert = db.compileStatement(sql);
            for(ContentValues cv : values){
                insert.bindString(1, cv.getAsString(DBHelper.OrdersTable.ORDER_ID));
                insert.bindString(2, cv.getAsString(DBHelper.OrdersTable.NAME));
                insert.bindString(3, cv.getAsString(DBHelper.OrdersTable.MOBILE));
                insert.bindString(4, cv.getAsString(DBHelper.OrdersTable.ITEM));
                insert.bindString(5, cv.getAsString(DBHelper.OrdersTable.PRICE));
                insert.bindString(6, cv.getAsString(DBHelper.OrdersTable.QUANTITY));
                insert.bindString(7, cv.getAsString(DBHelper.OrdersTable.ADDRESS));
                insert.bindString(8, cv.getAsString(DBHelper.OrdersTable.DESCRIPTION));
                insert.bindString(9, cv.getAsString(DBHelper.OrdersTable.COMMENT));
                insert.bindString(10, cv.getAsString(DBHelper.OrdersTable.COORDINATES));
                insert.bindString(11, cv.getAsString(DBHelper.OrdersTable.DATE));
                insert.execute();
            }
            db.setTransactionSuccessful();
            numInserted = values.length;
        } finally {
            db.endTransaction();
        }
        return numInserted;
    }

    public static ContentValues[] convertArrayToContentValues(JSONArray array){

        ContentValues[] cvs = null;
        if(array != null){
            int mCount = array.length();
            cvs = new ContentValues[mCount];
            for(int i=0; i<mCount; i++){

                JSONObject jsonItem = array.optJSONObject(i);
                if(jsonItem != null){
                    Log.d("Riyas", "item " + i + " json: " + jsonItem.toString());
                    String order_id = jsonItem.optString("_id");
                    String name = jsonItem.optString("name");
                    String mobile = jsonItem.optString("mobile");
                    String item = jsonItem.optString("item");
                    String price = jsonItem.optString("price");
                    String quantity = jsonItem.optString("quantity");
                    String address = jsonItem.optString("address");
                    String description = jsonItem.optString("description");
                    String comment = jsonItem.optString("comment");
                    String coordinates = jsonItem.optString("coordinates");
                    String date = jsonItem.optString("date");

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

                    cvs[i] = cv;
                }
            }
        }
        return cvs;
    }
}
