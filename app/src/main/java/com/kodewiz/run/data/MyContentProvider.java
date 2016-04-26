package com.kodewiz.run.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.kodewiz.run.OrdersInsertHandler;

public class MyContentProvider extends ContentProvider {

    private DBHelper mDBH;

    private static final String AUTHORITY = "com.kodewiz.run.provider";
    private static final int ORDERS_LIST = 3;
    private static final int ORDERS_ID = 4;
    private static final int CUSTOMERS_LIST = 5;
    private static final int CUSTOMERS_ID = 6;
    private static final int CUSTOMER_COUNT = 8;
    private static final int TOTAL_AMOUNT = 7;
    private static final int ORDER_COUNT = 9;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "orders", ORDERS_LIST);
        uriMatcher.addURI(AUTHORITY, "orders/#", ORDERS_ID);
        uriMatcher.addURI(AUTHORITY, "customers", CUSTOMERS_LIST);
        uriMatcher.addURI(AUTHORITY, "customers/#", CUSTOMERS_ID);
        uriMatcher.addURI(AUTHORITY, "customer_count", CUSTOMER_COUNT);
        uriMatcher.addURI(AUTHORITY, "amount", TOTAL_AMOUNT);
        uriMatcher.addURI(AUTHORITY, "order", ORDER_COUNT);

    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case ORDERS_LIST: return ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.zeefive.orders";
            case ORDERS_ID: return ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.zeefive.orders";
            case CUSTOMERS_LIST: return ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.zeefive.customers";
            case CUSTOMERS_ID: return ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.zeefive.customers";
            case CUSTOMER_COUNT: return ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.zeefive.customer_count";
            case TOTAL_AMOUNT: return ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.zeefive.amount";
            case ORDER_COUNT: return ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.zeefive.order";
            default: throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri ORDERS_CONTENT_URI = Uri.withAppendedPath(CONTENT_URI, "orders");
    public static final Uri AMOUNT_CONTENT_URI = Uri.withAppendedPath(CONTENT_URI, "amount");
    public static final Uri CUSTOMERS_CONTENT_URI = Uri.withAppendedPath(CONTENT_URI, "customers");
    public static final Uri CUSTOMER_COUNT_CONTENT_URI = Uri.withAppendedPath(CONTENT_URI, "customer_count");
    public static final Uri ORDER_CONTENT_URI = Uri.withAppendedPath(CONTENT_URI, "order");

    @Override
    public boolean onCreate() {
        mDBH = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // open a read-only database
        SQLiteDatabase db = mDBH.getWritableDatabase();
        String groupBy = null;
        String having = null;
        String rowId;

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)){
            case ORDERS_LIST: queryBuilder.setTables(DBHelper.OrdersTable.TABLE_NAME);
                break;
            case ORDERS_ID: rowId = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(DBHelper.OrdersTable._ID + "=" + rowId);
                break;
            case CUSTOMERS_LIST: queryBuilder.setTables(DBHelper.OrdersTable.TABLE_NAME);
                groupBy = DBHelper.OrdersTable.NAME;
                break;
            case TOTAL_AMOUNT: queryBuilder.setTables(DBHelper.OrdersTable.TABLE_NAME);
                projection = new String[]{"GROUP_CONCAT(" + DBHelper.OrdersTable.PRICE + ") AS amount"};
                break;
            case CUSTOMER_COUNT: queryBuilder.setTables(DBHelper.OrdersTable.TABLE_NAME);
                groupBy = DBHelper.OrdersTable.NAME;
                break;
            case ORDER_COUNT: queryBuilder.setTables(DBHelper.OrdersTable.TABLE_NAME);
                break;
            default: break;
        }

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDBH.getWritableDatabase();
        if (uriMatcher.match(uri) == ORDERS_LIST) {
            long id = db.insertWithOnConflict(DBHelper.OrdersTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            getContext().getContentResolver().notifyChange(uri, null);
            return getUriForId(id, uri);
        }else{
            return null;
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mDBH.getWritableDatabase();
        switch (uriMatcher.match(uri)){
            case ORDERS_LIST:
                int mInsertCount = OrdersInsertHandler.insertOrdersToDatabase(db, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return mInsertCount;
            default:
                throw new IllegalArgumentException("Illegal Uri Exception in MyContentProvider bulk insert method, " + uri.getPath());
        }
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(itemUri, null);
            return itemUri;
        }
        // somehting went wrong
        throw new SQLException("Problem while inserting into uri: " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBH.getWritableDatabase();
        switch (uriMatcher.match(uri)){
            case ORDERS_LIST:
                int deleteCount = db.delete(DBHelper.OrdersTable.TABLE_NAME, selection, selectionArgs);
                Log.d("Riyas", deleteCount + " Rows Deleted!");
                getContext().getContentResolver().notifyChange(uri, null);
                return deleteCount;
            default: break;
        }

        if(selection == null){
            selection = "1";
        }

        int deleteCount = db.delete(DBHelper.OrdersTable.TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBH.getWritableDatabase();
        int updateCount = 0;
        switch (uriMatcher.match(uri)){

            case ORDERS_LIST:
                updateCount = db.update(DBHelper.OrdersTable.TABLE_NAME, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(ORDERS_CONTENT_URI, null);
                break;
            default:
                break;
        }
        return updateCount;
    }
}
