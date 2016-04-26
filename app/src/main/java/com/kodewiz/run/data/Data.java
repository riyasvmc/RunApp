package com.kodewiz.run.data;

public class Data {

    // ZEEFIVE API Endpoints
    public static String URL_HOST_ZEEFIVE = "http://api.zeefive.com/chicken/v1";
    public static final String REQUEST_NOTIFY_ORDER = URL_HOST_ZEEFIVE + "/notifyOrder"; // POST

    // C@D API Endpoints
    public static final String URL_HOST_CHICKENATDOOR = "http://www.chickenatdoor.com/";
    public static final String URL_UPDATE_ORDER_STATE = URL_HOST_CHICKENATDOOR + "admin/update_delivery.php/"; // POST
    public static final String URL_POST_SYNC = "http://chickenatdoor.com/api/v1/sync"; // POST
    public static final String URL_NOTIFY_ORDER = "http://api.zeefive.com/chicken/v1/notifyOrder";

    public static final String GOOGLE_API_KEY = "AIzaSyBiegcjNhIKf7L6XHnQX4dVuhl9NUP-IDM";
    public static final String SENDER_ID = "218615235141";

    // DELIVERY STATE UPDATE KEYS
    public static final String API_KEY_OID = "oid";
    public static final String API_KEY_MODE = "mode";
    public static final String API_KEY_TIMESTAMP = "time_stamp";

}
