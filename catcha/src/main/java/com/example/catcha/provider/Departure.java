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
import android.text.TextUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Departure implements Parcelable, CatchaContract.DeparturesColumns {

    private static final long TIMEZONE_OFFSET = TimeZone.getTimeZone("GMT+2:00").getRawOffset();
    private static final long MILLIS_IN_MINUTE = 1000 * 60;
    private static final long MILLIS_IN_HOUR = 1000 * 60 * 60;
    private static final long TIME_THRESHOLD = 1000 * 60 * 15;

    private static final long INVALID_ID = -1;

    public static final String DEPARTURES_BY_LOCATION_SELECTION = CatchaDatabaseHelper.DEPARTURES_TABLE_NAME + "." + LOCATION_ID + " = ?";

    public static String buildDeparturesNotInLocationSelection(List<String> locationIds) {
        return CatchaDatabaseHelper.DEPARTURES_TABLE_NAME + "." + LOCATION_ID + " NOT IN (" + TextUtils.join(",", locationIds) + ")";
    }

    private static final String DEFAULT_SORT_ORDER = CatchaDatabaseHelper.DEPARTURES_TABLE_NAME + "." + DISTANCE + " ASC";

    private static final String NEAREST_DEPARTURE_SORT_ORDER = CatchaDatabaseHelper.DEPARTURES_TABLE_NAME + "." + DISTANCE + " ASC LIMIT 1";

    private static final String[] QUERY_COLUMNS = {
            _ID,
            LOCATION_ID,
            START_BP,
            DEST_BP,
            DEPARTURE_TIME_1,
            DEPARTURE_TIME_2,
            DEPARTURE_TIME_3,
            DEPARTURE_TIME_4,
            TRACK_1,
            TRACK_2,
            TRACK_3,
            TRACK_4,
            DISTANCE
    };

    private static final int ID_INDEX = 0;
    private static final int LOCATION_ID_INDEX = 1;
    private static final int START_BP_INDEX = 2;
    private static final int DEST_BP_INDEX = 3;
    private static final int DEPARTURE_TIME_1_INDEX = 4;
    private static final int DEPARTURE_TIME_2_INDEX = 5;
    private static final int DEPARTURE_TIME_3_INDEX = 6;
    private static final int DEPARTURE_TIME_4_INDEX = 7;
    private static final int TRACK_1_INDEX = 8;
    private static final int TRACK_2_INDEX = 9;
    private static final int TRACK_3_INDEX = 10;
    private static final int TRACK_4_INDEX = 11;
    private static final int DISTANCE_INDEX = 12;

    private static final int COLUMN_COUNT = DISTANCE_INDEX + 1;

    private static ContentValues createContentValues(Departure departure) {
        ContentValues contentValues = new ContentValues(COLUMN_COUNT);
        if (departure.id != INVALID_ID) {
            contentValues.put(CatchaContract.DeparturesColumns._ID, departure.id);
        }

        contentValues.put(LOCATION_ID, departure.locationId);
        contentValues.put(START_BP, departure.startBp);
        contentValues.put(DEST_BP, departure.destBp);
        contentValues.put(DEPARTURE_TIME_1, departure.departureTime1);
        contentValues.put(DEPARTURE_TIME_2, departure.departureTime2);
        contentValues.put(DEPARTURE_TIME_3, departure.departureTime3);
        contentValues.put(DEPARTURE_TIME_4, departure.departureTime4);
        contentValues.put(TRACK_1, departure.track1);
        contentValues.put(TRACK_2, departure.track2);
        contentValues.put(TRACK_3, departure.track3);
        contentValues.put(TRACK_4, departure.track4);
        contentValues.put(DISTANCE, departure.distance);

        return contentValues;
    }

    private static Uri getUri(long departureId) {
        return ContentUris.withAppendedId(CONTENT_URI, departureId);
    }

    private static long getId(Uri contentUri) {
        return ContentUris.parseId(contentUri);
    }

    public static CursorLoader getDeparturesCursorLoader(Context context) {
        return new CursorLoader(context, CONTENT_URI, QUERY_COLUMNS, null, null, DEFAULT_SORT_ORDER);
    }

    public static Departure getNearestDeparture(ContentResolver contentResolver) {
        try (Cursor cursor = contentResolver.query(CONTENT_URI, QUERY_COLUMNS, null, null, NEAREST_DEPARTURE_SORT_ORDER)) {
            if (cursor != null && cursor.moveToFirst()) {
                return new Departure(cursor);
            }
        }

        return null;
    }

    public static List<Departure> getDepartures(ContentResolver contentResolver, String selection, String... selectionArgs) {
        final List<Departure> result = new LinkedList<>();
        try (Cursor cursor = contentResolver.query(CONTENT_URI, QUERY_COLUMNS, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    result.add(new Departure(cursor));
                } while (cursor.moveToNext());
            }
        }
        return result;
    }

    public static Departure addDeparture(ContentResolver contentResolver, Departure departure) {
        ContentValues contentValues = createContentValues(departure);
        Uri uri = contentResolver.insert(CONTENT_URI, contentValues);
        departure.id = getId(uri);
        return departure;
    }

    public static boolean updateDeparture(ContentResolver contentResolver, Departure departure) {
        if (departure.id == Departure.INVALID_ID) {
            return false;
        }
        ContentValues contentValues = createContentValues(departure);
        long rowsUpdated = contentResolver.update(getUri(departure.id), contentValues, null, null);
        return rowsUpdated == 1;
    }

    public static boolean deleteDeparture(ContentResolver contentResolver, long departureId) {
        if (departureId == INVALID_ID) {
            return false;
        }
        int deletedRows = contentResolver.delete(getUri(departureId), "", null);
        return deletedRows == 1;
    }

    public static final Creator<Departure> CREATOR = new Creator<Departure>() {
        public Departure createFromParcel(Parcel p) {
            return new Departure(p);
        }

        public Departure[] newArray(int size) {
            return new Departure[size];
        }
    };

    public long id;
    public long locationId;
    public String startBp;
    public String destBp;
    private String departureTime1;
    private String departureTime2;
    private String departureTime3;
    private String departureTime4;
    public String track1;
    public String track2;
    public String track3;
    public String track4;
    public int distance;

    public Departure() {
    }

    public Departure(long locationId, String startBp, String destBp, List<String> departureTimes, List<String> tracks, int distance) {
        this.id = INVALID_ID;
        this.locationId = locationId;
        this.startBp = startBp;
        this.destBp = destBp;
        if (departureTimes.size() > 0)
            this.departureTime1 = departureTimes.get(0);
        if (departureTimes.size() > 1)
            this.departureTime2 = departureTimes.get(1);
        if (departureTimes.size() > 2)
            this.departureTime3 = departureTimes.get(2);
        if (departureTimes.size() > 3)
            this.departureTime4 = departureTimes.get(3);
        if (tracks.size() > 0)
            this.track1 = tracks.get(0);
        if (tracks.size() > 1)
            this.track2 = tracks.get(1);
        if (tracks.size() > 2)
            this.track3 = tracks.get(2);
        if (tracks.size() > 3)
            this.track4 = tracks.get(3);
        this.distance = distance;
    }

    public Departure(Cursor c) {
        id = c.getLong(ID_INDEX);
        locationId = c.getLong(LOCATION_ID_INDEX);
        startBp = c.getString(START_BP_INDEX);
        destBp = c.getString(DEST_BP_INDEX);
        departureTime1 = c.getString(DEPARTURE_TIME_1_INDEX);
        departureTime2 = c.getString(DEPARTURE_TIME_2_INDEX);
        departureTime3 = c.getString(DEPARTURE_TIME_3_INDEX);
        departureTime4 = c.getString(DEPARTURE_TIME_4_INDEX);
        track1 = c.getString(TRACK_1_INDEX);
        track2 = c.getString(TRACK_2_INDEX);
        track3 = c.getString(TRACK_3_INDEX);
        track4 = c.getString(TRACK_4_INDEX);
        distance = c.getInt(DISTANCE_INDEX);
    }

    private Departure(Parcel p) {
        id = p.readLong();
        locationId = p.readLong();
        startBp = p.readString();
        destBp = p.readString();
        departureTime1 = p.readString();
        departureTime2 = p.readString();
        departureTime3 = p.readString();
        departureTime4 = p.readString();
        track1 = p.readString();
        track2 = p.readString();
        track3 = p.readString();
        track4 = p.readString();
        distance = p.readInt();
    }


    public int describeContents() {
        return 0;
    }

    public long getDepartureTime1InMillis() {
        return timeStringToLong(departureTime1);
    }

    private long getDepartureTime2InMillis() {
        return timeStringToLong(departureTime2);
    }

    private long getDepartureTime3InMillis() {
        return timeStringToLong(departureTime3);
    }

    private long getDepartureTime4InMillis() {
        return timeStringToLong(departureTime4);
    }

    private long timeStringToLong(String time) {
        return Long.valueOf(time) + TIMEZONE_OFFSET;
    }

    public String getDepartureTime1AsFormattedString() {
        return getDepartureTimeAsString(getDepartureTime1InMillis());
    }

    public String getDepartureTime2AsFormattedString() {
        return getDepartureTimeAsString(getDepartureTime2InMillis());
    }

    public String getDepartureTime3AsFormattedString() {
        return getDepartureTimeAsString(getDepartureTime3InMillis());
    }

    public String getDepartureTime4AsFormattedString() {
        return getDepartureTimeAsString(getDepartureTime4InMillis());
    }

    private String getDepartureTimeAsString(long departureTime) {
        long delta = departureTime - (System.currentTimeMillis() + Departure.TIMEZONE_OFFSET);

        if (delta < 60 * 1000) {
            return "Now";
        }
        if (delta > TIME_THRESHOLD) {
            return String.format(Locale.GERMAN, "%02d:%02d", (departureTime / MILLIS_IN_HOUR) % 24, (departureTime / MILLIS_IN_MINUTE) % 60);
        } else {
            // ceil: http://stackoverflow.com/questions/7139382/java-rounding-up-to-an-int-using-math-ceil
            return String.valueOf((delta - 1) / MILLIS_IN_MINUTE + 1) + " Min";
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(locationId);
        dest.writeString(startBp);
        dest.writeString(destBp);
        dest.writeString(departureTime1);
        dest.writeString(departureTime2);
        dest.writeString(departureTime3);
        dest.writeString(departureTime4);
        dest.writeString(track1);
        dest.writeString(track2);
        dest.writeString(track3);
        dest.writeString(track4);
        dest.writeInt(distance);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Departure)) return false;
        final Departure other = (Departure) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    @Override
    public String toString() {
        return "Departure{" +
                "id=" + id +
                ", locationId=" + locationId +
                ", startBp='" + startBp + '\'' +
                ", destBp='" + destBp + '\'' +
                ", departureTime1='" + departureTime1 + '\'' +
                ", departureTime2='" + departureTime2 + '\'' +
                ", departureTime3='" + departureTime3 + '\'' +
                ", departureTime4='" + departureTime4 + '\'' +
                ", track1='" + track1 + '\'' +
                ", track2='" + track2 + '\'' +
                ", track3='" + track3 + '\'' +
                ", track4='" + track4 + '\'' +
                ", distance='" + distance + '\'' +
                '}';
    }
}
