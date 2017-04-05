package com.example.zs.ipc.db;

import android.net.Uri;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public class CountryCode {
    public static final String DB_NAME = "code.db";
    public static final String TB_NAME = "countrycode";
    public static final int VERSION = 1;
    public static final String ID = "_id";
    public static final String COUNTRY = "country";
    public static final String CODE = "code";
    public static final String AUTHORITY = "com.studio.andriod.provider.countrycode";
    public static final int ITEM = 1;
    public static final int ITEM_ID = 2;
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.studio.android.countrycode";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.studio.android.countrycode";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/item");
}
