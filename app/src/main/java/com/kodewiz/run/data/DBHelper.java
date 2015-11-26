package com.kodewiz.run.data;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // database
    public static final String DATABASE_NAME = "database.db";
    public static final int DATABASE_VERSION = 2;

    // table fields
    public static final String ORDER_TABLE = "order_table";
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String ITEM = "item";
    public static final String QUANTITY = "quantity";
    public static final String FLAT = "flat";
    public static final String LANDMARK = "landmark";
    public static final String ADDRESS = "address";
    public static final String AREA = "area";
    public static final String ZIP = "zip";
    public static final String PRICE = "price";
    public static final String DESCRIPTION = "description";
    public static final String PHONE = "phone";
    public static final String COMMENT = "comment";
    public static final String ORDER_TIME = "order_time";
    public static final String COORDINATES = "coordinates";

    private static final String CREATE_ORDER_TABLE = "CREATE TABLE IF NOT EXISTS "
            + ORDER_TABLE + " ( " + ID + " int PRIMARY KEY, " + NAME + " TEXT, "
            + EMAIL + " TEXT, " + ITEM + " TEXT, " + QUANTITY + " TEXT, "
            + ADDRESS + " TEXT, " + PRICE + " TEXT, " + DESCRIPTION + " TEXT, "
            + PHONE + " TEXT, " + COMMENT + " TEXT," + ORDER_TIME + " TEXT DEFAULT (datetime('now', 'localtime')), " + COORDINATES + " TEXT );";

    private static Context mContext;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ORDER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ORDER_TABLE);
        onCreate(db);
    }
}