package com.kodewiz.run.data;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

public class DBHelper extends SQLiteOpenHelper {

    // database
    public static final String DATABASE_NAME = "database.db";
    public static final int DATABASE_VERSION = 3;

    private static final String CREATE_ORDERS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + OrdersTable.TABLE_NAME
                    + " ( "
                    + OrdersTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + OrdersTable.ORDER_ID + " TEXT UNIQUE, "
                    + OrdersTable.NAME + " TEXT, "
                    + OrdersTable.MOBILE + " TEXT, "
                    + OrdersTable.ITEM + " TEXT, "
                    + OrdersTable.PRICE + " TEXT, "
                    + OrdersTable.QUANTITY + " TEXT, "
                    + OrdersTable.ADDRESS + " TEXT, "
                    + OrdersTable.DESCRIPTION + " TEXT, "
                    + OrdersTable.COMMENT + " TEXT, "
                    + OrdersTable.COORDINATES + " TEXT, "
                    + OrdersTable.DATE + " TEXT "
                    + ");";

    private static Context mContext;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ORDERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + OrdersTable.TABLE_NAME);
        onCreate(db);
    }

    public static final class OrdersTable implements BaseColumns {
        private OrdersTable() {}
        public static final String TABLE_NAME = "orders_table";
        public static final String FULL_ID =  TABLE_NAME + "." + _ID;
        public static final String ORDER_ID =  "order_id";
        public static final String FULL_ORDER_ID =  TABLE_NAME + "." + ORDER_ID;
        public static final String NAME =  "name";
        public static final String FULL_NAME =  TABLE_NAME + "." + NAME;
        public static final String MOBILE =  "mobile";
        public static final String FULL_MOBILE =  TABLE_NAME + "." + MOBILE;
        public static final String ITEM =  "item";
        public static final String FULL_ITEM =  TABLE_NAME + "." + ITEM;
        public static final String PRICE =  "price";
        public static final String FULL_PRICE =  TABLE_NAME + "." + PRICE;
        public static final String QUANTITY =  "quantity";
        public static final String FULL_QUANTITY =  TABLE_NAME + "." + QUANTITY;
        public static final String ADDRESS =  "address";
        public static final String FULL_ADDRESS =  TABLE_NAME + "." + ADDRESS;
        public static final String DESCRIPTION =  "description";
        public static final String FULL_DESCRIPTION =  TABLE_NAME + "." + DESCRIPTION;
        public static final String COMMENT =  "comment";
        public static final String FULL_COMMENT =  TABLE_NAME + "." + COMMENT;
        public static final String COORDINATES =  "coordinates";
        public static final String FULL_COORDINATES =  TABLE_NAME + "." + COORDINATES;
        public static final String DATE =  "date";
        public static final String FULL_DATE =  TABLE_NAME + "." + DATE;
    }
}