package com.example.catcha.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class CatchaContract {

    public static final String CONTENT_AUTHORITY = "com.example.catcha";

    protected interface LocationsColumns extends BaseColumns {
        Uri CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY + "/locations");
        String START_BP = "startbp";
        String END_BP = "endbp";
        String DAYS_OF_WEEK = "daysofweek";
        String ENABLED = "enabled";
        String LAT = "lat";
        String LON = "lon";
        String LAT_COS = "latcos";
        String LON_COS = "loncos";
        String LON_SIN = "lonsin";
        String LAT_SIN = "latsin";
    }

    protected interface DeparturesColumns extends BaseColumns {
        Uri CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY + "/departures");
        String LOCATION_ID = "locationid";
        String START_BP = "startbp";
        String DEST_BP = "destbp";
        String DEPARTURE_TIME_1 = "departuretime1";
        String DEPARTURE_TIME_2 = "departuretime2";
        String DEPARTURE_TIME_3 = "departuretime3";
        String DEPARTURE_TIME_4 = "departuretime4";
        String TRACK_1 = "track1";
        String TRACK_2 = "track2";
        String TRACK_3 = "track3";
        String TRACK_4 = "track4";
        String DISTANCE = "distance";
    }

}
