package com.example.catcha.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import static com.example.catcha.provider.CatchaDatabaseHelper.DEPARTURES_TABLE_NAME;
import static com.example.catcha.provider.CatchaDatabaseHelper.LOCATIONS_TABLE_NAME;

public class CatchaProvider extends ContentProvider {

    private static final String TAG = CatchaProvider.class.getSimpleName();

    private CatchaDatabaseHelper openHelper;

    static final int DEPARTURES = 100;
    static final int DEPARTURES_ID = 101;
    static final int LOCATIONS = 200;
    static final int LOCATIONS_ID = 201;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(CatchaContract.CONTENT_AUTHORITY, "locations", LOCATIONS);
        uriMatcher.addURI(CatchaContract.CONTENT_AUTHORITY, "locations/#", LOCATIONS_ID);

        uriMatcher.addURI(CatchaContract.CONTENT_AUTHORITY, "departures", DEPARTURES);
        uriMatcher.addURI(CatchaContract.CONTENT_AUTHORITY, "departures/#", DEPARTURES_ID);
    }

    public CatchaProvider() {
    }

    @Override
    public boolean onCreate() {
        openHelper = new CatchaDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        SQLiteDatabase database = openHelper.getReadableDatabase();

        int match = uriMatcher.match(uri);
        switch (match) {
            case LOCATIONS:
                queryBuilder.setTables(LOCATIONS_TABLE_NAME);
                break;
            case LOCATIONS_ID:
                queryBuilder.setTables(LOCATIONS_TABLE_NAME);
                queryBuilder.appendWhere(CatchaContract.LocationsColumns._ID + "=");
                queryBuilder.appendWhere(uri.getLastPathSegment());
                break;
            case DEPARTURES:
                queryBuilder.setTables(DEPARTURES_TABLE_NAME);
                break;
            case DEPARTURES_ID:
                queryBuilder.setTables(DEPARTURES_TABLE_NAME);
                queryBuilder.appendWhere(CatchaContract.DeparturesColumns._ID + "=");
                queryBuilder.appendWhere(uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor ret = queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);

        if (ret == null) {
            Log.e(TAG, "Query failed");
        } else {
            ret.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return ret;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            case LOCATIONS:
                return "vnd.android.cursor.dir/locations";
            case LOCATIONS_ID:
                return "vnd.android.cursor.item/locations";
            case DEPARTURES:
                return "vnd.android.cursor.dir/departures";
            case DEPARTURES_ID:
                return "vnd.android.cursor.item/departures";
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowId;
        final SQLiteDatabase database = openHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case LOCATIONS:
                rowId = database.insert(LOCATIONS_TABLE_NAME, null, values);
                break;
            case DEPARTURES:
                rowId = database.insert(DEPARTURES_TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Cannot insert from URI: " + uri);
        }

        Uri uriResult = ContentUris.withAppendedId(CatchaContract.LocationsColumns.CONTENT_URI, rowId);
        notifyChange(getContext().getContentResolver(), uriResult);
        return uriResult;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;
        String primaryKey;
        SQLiteDatabase database = openHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case LOCATIONS:
                count = database.delete(LOCATIONS_TABLE_NAME, selection, selectionArgs);
                break;
            case LOCATIONS_ID:
                primaryKey = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = CatchaContract.LocationsColumns._ID + "=" + primaryKey;
                } else {
                    selection = CatchaContract.LocationsColumns._ID + "=" + primaryKey + " AND (" + selection + ")";
                }
                count = database.delete(LOCATIONS_TABLE_NAME, selection, selectionArgs);
                break;
            case DEPARTURES:
                count = database.delete(DEPARTURES_TABLE_NAME, selection, selectionArgs);
                break;
            case DEPARTURES_ID:
                primaryKey = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = CatchaContract.DeparturesColumns._ID + "=" + primaryKey;
                } else {
                    selection = CatchaContract.DeparturesColumns._ID + "=" + primaryKey + " AND (" + selection + ")";
                }
                count = database.delete(DEPARTURES_TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot insert from URI: " + uri);
        }

        notifyChange(getContext().getContentResolver(), uri);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        String id;
        SQLiteDatabase database = openHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case LOCATIONS_ID:
                id = uri.getLastPathSegment();
                count = database.update(LOCATIONS_TABLE_NAME, values, CatchaContract.LocationsColumns._ID + "=" + id, null);
                break;
            case DEPARTURES_ID:
                id = uri.getLastPathSegment();
                count = database.update(DEPARTURES_TABLE_NAME, values, CatchaContract.DeparturesColumns._ID + "=" + id, null);
                break;
            default:
                throw new UnsupportedOperationException("Cannot update URI: " + uri);
        }

        notifyChange(getContext().getContentResolver(), uri);
        return count;
    }

    private void notifyChange(ContentResolver contentResolver, Uri uri) {
        contentResolver.notifyChange(uri, null);
    }
}
