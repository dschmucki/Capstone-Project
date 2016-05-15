package com.example.catcha.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;
import java.util.List;

public class Location implements Parcelable, CatchaContract.LocationsColumns {

    private static final String TAG = Location.class.getSimpleName();

    public static final long INVALID_ID = -1;

    public static String buildLocationsEnabledWithTimeAndInDistanceSelection(final int daysOfWeek, final double latitude, final double longitude, final double distanceInKilometers) {

        final double coslat = Math.cos(Math.toRadians(latitude));
        final double sinlat = Math.sin(Math.toRadians(latitude));
        final double coslng = Math.cos(Math.toRadians(longitude));
        final double sinlng = Math.sin(Math.toRadians(longitude));

        final String format = "((%1$s * %2$s * (%3$s * %4$s + %5$s * %6$s) + %7$s * %8$s) > %9$s) AND (%10$s & %11$s) > 0 AND %12$s = 1";
        final String selection = String.format(format,
                coslat, CatchaDatabaseHelper.LOCATIONS_TABLE_NAME + "." + LAT_COS,
                coslng, CatchaDatabaseHelper.LOCATIONS_TABLE_NAME + "." + LON_COS,
                sinlng, CatchaDatabaseHelper.LOCATIONS_TABLE_NAME + "." + LON_SIN,
                sinlat, CatchaDatabaseHelper.LOCATIONS_TABLE_NAME + "." + LAT_SIN,
                Math.cos(distanceInKilometers / 6371.0),
                CatchaDatabaseHelper.LOCATIONS_TABLE_NAME + "." + DAYS_OF_WEEK, daysOfWeek,
                CatchaDatabaseHelper.LOCATIONS_TABLE_NAME + "." + ENABLED
        );

        Log.d(TAG, "Distance query: " + selection);
        return selection;
    }

    private static final String DEFAULT_SORT_ORDER =
            CatchaDatabaseHelper.LOCATIONS_TABLE_NAME + "." + START_BP + " ASC";

    private static final String[] QUERY_COLUMNS = {
            _ID,
            START_BP,
            END_BP,
            DAYS_OF_WEEK,
            ENABLED,
            LAT,
            LON,
            LAT_COS,
            LON_COS,
            LON_SIN,
            LAT_SIN
    };

    private static final int ID_INDEX = 0;
    private static final int START_BP_INDEX = 1;
    private static final int END_BP_INDEX = 2;
    private static final int DAYS_OF_WEEK_INDEX = 3;
    private static final int ENABLED_INDEX = 4;
    private static final int LAT_INDEX = 5;
    private static final int LON_INDEX = 6;
    private static final int LAT_COS_INDEX = 7;
    private static final int LON_COS_INDEX = 8;
    private static final int LON_SIN_INDEX = 9;
    private static final int LAT_SIN_INDEX = 10;

    private static final int COLUMN_COUNT = LAT_SIN_INDEX + 1;

    public static ContentValues createContentValues(Location location) {
        ContentValues contentValues = new ContentValues(COLUMN_COUNT);
        if (location.id != INVALID_ID) {
            contentValues.put(CatchaContract.LocationsColumns._ID, location.id);
        }

        contentValues.put(START_BP, location.startBp);
        contentValues.put(END_BP, location.endBp);
        contentValues.put(DAYS_OF_WEEK, location.daysOfWeek.getBitSet());
        contentValues.put(ENABLED, location.enabled ? 1 : 0);
        contentValues.put(LAT, location.lat);
        contentValues.put(LON, location.lon);
        contentValues.put(LAT_COS, location.latCos);
        contentValues.put(LON_COS, location.lonCos);
        contentValues.put(LON_SIN, location.lonSin);
        contentValues.put(LAT_SIN, location.latSin);

        return contentValues;
    }

    public static Uri getUri(long locationId) {
        return ContentUris.withAppendedId(CONTENT_URI, locationId);
    }

    public static long getId(Uri contentUri) {
        return ContentUris.parseId(contentUri);
    }

    public static CursorLoader getLocationsCursorLoader(Context context) {
        return new CursorLoader(context, CONTENT_URI, QUERY_COLUMNS, null, null, DEFAULT_SORT_ORDER);
    }

    public static Location getLocation(ContentResolver contentResolver, long locationId) {
        try (Cursor cursor = contentResolver.query(getUri(locationId), QUERY_COLUMNS, null, null, null)) {
            if (cursor.moveToFirst()) {
                return new Location(cursor);
            }
        }

        return null;
    }

    public static List<Location> getLocations(ContentResolver contentResolver, String selection, String... selectionArgs) {
        final List<Location> result = new LinkedList<>();
        try (Cursor cursor = contentResolver.query(CONTENT_URI, QUERY_COLUMNS, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    result.add(new Location(cursor));
                } while (cursor.moveToNext());
            }
        }
        return result;
    }

    public static Location addLocation(ContentResolver contentResolver, Location location) {
        Log.d(TAG, "Adding new Location with values: " + location);
        ContentValues contentValues = createContentValues(location);
        Uri uri = contentResolver.insert(CONTENT_URI, contentValues);
        location.id = getId(uri);
        return location;
    }

    public static boolean updateLocation(ContentResolver contentResolver, Location location) {
        if (location.id == Location.INVALID_ID) {
            return false;
        }
        ContentValues contentValues = createContentValues(location);
        long rowsUpdated = contentResolver.update(getUri(location.id), contentValues, null, null);
        return rowsUpdated == 1;
    }

    public static boolean deleteLocation(ContentResolver contentResolver, long locationId) {
        if (locationId == INVALID_ID) {
            return false;
        }
        int deletedRows = contentResolver.delete(getUri(locationId), "", null);
        return deletedRows == 1;
    }

    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        public Location createFromParcel(Parcel p) {
            return new Location(p);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public long id;
    public boolean enabled;
    public String startBp;
    public String endBp;
    public DaysOfWeek daysOfWeek;
    public double lat;
    public double lon;
    public double latCos;
    public double lonCos;
    public double lonSin;
    public double latSin;

    public Location() {
    }

    public Location(com.example.catcha.sync.model.Location startBpLoc, com.example.catcha.sync.model.Location endBpLoc) {
        this.id = INVALID_ID;
        this.startBp = startBpLoc.getName();
        this.endBp = endBpLoc.getName();
        this.daysOfWeek = new DaysOfWeek(0);
        this.lat = startBpLoc.getCoordinate().getLat();
        this.lon = startBpLoc.getCoordinate().getLon();
        this.latCos = Math.cos(Math.toRadians(startBpLoc.getCoordinate().getLat()));
        this.lonCos = Math.cos(Math.toRadians(startBpLoc.getCoordinate().getLon()));
        this.lonSin = Math.sin(Math.toRadians(startBpLoc.getCoordinate().getLon()));
        this.latSin = Math.sin(Math.toRadians(startBpLoc.getCoordinate().getLat()));

    }

    public Location(Cursor c) {
        id = c.getLong(ID_INDEX);
        enabled = c.getInt(ENABLED_INDEX) == 1;
        startBp = c.getString(START_BP_INDEX);
        endBp = c.getString(END_BP_INDEX);
        daysOfWeek = new DaysOfWeek(c.getInt(DAYS_OF_WEEK_INDEX));
        lat = c.getDouble(LAT_INDEX);
        lon = c.getDouble(LON_INDEX);
        latCos = c.getDouble(LAT_COS_INDEX);
        lonCos = c.getDouble(LON_COS_INDEX);
        lonSin = c.getDouble(LON_SIN_INDEX);
        latSin = c.getDouble(LAT_SIN_INDEX);
    }

    Location(Parcel p) {
        id = p.readLong();
        enabled = p.readInt() == 1;
        startBp = p.readString();
        endBp = p.readString();
        daysOfWeek = new DaysOfWeek(p.readInt());
        lat = p.readDouble();
        lon = p.readDouble();
        latCos = p.readDouble();
        lonCos = p.readDouble();
        lonSin = p.readDouble();
        latSin = p.readDouble();
    }

    public int describeContents() {
        return 0;
    }

    public LatLng getLatLng() {
        return new LatLng(this.lat, this.lon);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(enabled ? 1 : 0);
        dest.writeString(startBp);
        dest.writeString(endBp);
        dest.writeInt(daysOfWeek.getBitSet());
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeDouble(latCos);
        dest.writeDouble(lonCos);
        dest.writeDouble(lonSin);
        dest.writeDouble(latSin);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Location)) return false;
        final Location other = (Location) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", enabled=" + enabled +
                ", startBp='" + startBp + '\'' +
                ", endBp='" + endBp + '\'' +
                ", daysOfWeek=" + daysOfWeek +
                ", lat=" + lat +
                ", lon=" + lon +
                ", latCos=" + latCos +
                ", lonCos=" + lonCos +
                ", lonSin=" + lonSin +
                ", latSin=" + latSin +
                '}';
    }
}
