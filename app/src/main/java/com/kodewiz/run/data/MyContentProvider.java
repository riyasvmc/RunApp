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
import android.text.TextUtils;

public class MyContentProvider extends ContentProvider {

    private DBHelper dbh;

    private static final String AUTHORITY = "com.kodewiz.run.provider";
    private static final int ORDER_LIST = 1;
    private static final int ORDER_ID = 2;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "orders", ORDER_LIST);
        uriMatcher.addURI(AUTHORITY, "orders/#", ORDER_ID);

    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case ORDER_LIST: return ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.zeefive.orders";
            case ORDER_ID: return ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.zeefive.orders";
            default: throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri ORDERS_CONTENT_URI = Uri.withAppendedPath(CONTENT_URI, "orders");

    @Override
    public boolean onCreate() {
        dbh = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // open a read-only database
        SQLiteDatabase db = dbh.getWritableDatabase();
        String groupBy = null;
        String having = null;

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)){
            case ORDER_LIST: queryBuilder.setTables(DBHelper.ORDER_TABLE);
                break;
            case ORDER_ID: String rowId = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(DBHelper.ID + "=" + rowId);
                break;
            default: break;
        }

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        if (uriMatcher.match(uri) == ORDER_LIST) {
            long id = db.insert(DBHelper.ORDER_TABLE, null, values);
            getContext().getContentResolver().notifyChange(uri, null);
            return getUriForId(id, uri);
        }else{
            return null;
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (uriMatcher.match(uri) != ORDER_LIST ) {
            throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
        }
        SQLiteDatabase db = dbh.getWritableDatabase();
        if(uriMatcher.match(uri) == ORDER_LIST){
            return 0;
        }else {
            return -1;
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
        SQLiteDatabase db = dbh.getWritableDatabase();
        switch (uriMatcher.match(uri)){
            case ORDER_ID: String rowId = uri.getPathSegments().get(1);
                              selection = DBHelper.ID + "=" + rowId + (!TextUtils.isEmpty(selection) ? "AND (" + selection + ')' : "");
            default: break;
        }

        if(selection == null){
            selection = "1";
        }

        int deleteCount = db.delete(DBHelper.ORDER_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        int updateCount = 0;
        switch (uriMatcher.match(uri)){

            case ORDER_LIST:
                updateCount = db.update(DBHelper.ORDER_TABLE, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(ORDERS_CONTENT_URI, null);
                break;
            default:
                break;
        }
        return updateCount;
    }
}
