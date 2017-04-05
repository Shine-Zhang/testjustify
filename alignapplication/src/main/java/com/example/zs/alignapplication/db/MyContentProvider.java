package com.example.zs.ipc.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

public class MyContentProvider extends ContentProvider {

    MyDBHelper dbHelper;
    private static final UriMatcher sMatcher;
    static {
        sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sMatcher.addURI(CountryCode.AUTHORITY,
                "item",CountryCode.ITEM);
        sMatcher.addURI(CountryCode.AUTHORITY,
                "item/#", CountryCode.ITEM_ID);
    }
    public MyContentProvider() {
       // getContentResolver().registerContentObserver(uri, true, new MyObserver(new Handler()));
        //getContext().getContentResolver().notifyChange(uri, null);
    }



    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sMatcher.match(uri)) {
            case CountryCode.ITEM:
                count = db.delete(CountryCode.TB_NAME, selection,selectionArgs);
                break;
            case CountryCode.ITEM_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(CountryCode.TB_NAME, CountryCode.ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        switch (sMatcher.match(uri)) {
            case CountryCode.ITEM:
                return CountryCode.CONTENT_TYPE;
            case CountryCode.ITEM_ID:
                return CountryCode.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId;
        if (sMatcher.match(uri) != CountryCode.ITEM) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        rowId = db.insert(CountryCode.TB_NAME,CountryCode.ID,values);
        if (rowId > 0) {
            Uri noteUri =
                    ContentUris.withAppendedId(CountryCode.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        dbHelper = new MyDBHelper(getContext(), CountryCode.DB_NAME,
                null,CountryCode.VERSION);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c;
        switch (sMatcher.match(uri)) {
            case CountryCode.ITEM:
                c = db.query(CountryCode.TB_NAME, projection, selection,
                        selectionArgs,null,null,sortOrder);
                break;
            case CountryCode.ITEM_ID:
                String id = uri.getPathSegments().get(1);
                c = db.query(CountryCode.TB_NAME, projection, CountryCode.ID + "="
                                + id
                                + (!TextUtils.isEmpty(selection)
                                ? " AND (" + selection + ')' : ""),
                        selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sMatcher.match(uri)) {
            case CountryCode.ITEM:
                count = db.update(CountryCode.TB_NAME,values, selection,selectionArgs);
                break;
            case CountryCode.ITEM_ID:
                String id = uri.getPathSegments().get(1);
                count = db.update(CountryCode.TB_NAME,values,CountryCode.ID+"=" + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
