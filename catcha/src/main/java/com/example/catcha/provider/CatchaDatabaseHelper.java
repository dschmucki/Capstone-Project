package com.example.catcha.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CatchaDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = CatchaDatabaseHelper.class.getSimpleName();

    private static final int VERSION = 6;

    static final String DATABASE_NAME = "catcha.db";
    static final String LOCATIONS_TABLE_NAME = "locations";
    static final String DEPARTURES_TABLE_NAME = "departures";

    private static void createLocationsTable(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + LOCATIONS_TABLE_NAME + " (" +
                CatchaContract.LocationsColumns._ID + " INTEGER PRIMARY KEY," +
                CatchaContract.LocationsColumns.START_BP + " TEXT NOT NULL," +
                CatchaContract.LocationsColumns.END_BP + " TEXT NOT NULL," +
                CatchaContract.LocationsColumns.DAYS_OF_WEEK + " INTEGER NOT NULL," +
                CatchaContract.LocationsColumns.ENABLED + " INTEGER NOT NULL," +
                CatchaContract.LocationsColumns.LAT + " DOUBLE NOT NULL," +
                CatchaContract.LocationsColumns.LON + " DOUBLE NOT NULL," +
                CatchaContract.LocationsColumns.LAT_COS + " DOUBLE NOT NULL," +
                CatchaContract.LocationsColumns.LON_COS + " DOUBLE NOT NULL," +
                CatchaContract.LocationsColumns.LON_SIN + " DOUBLE NOT NULL," +
                CatchaContract.LocationsColumns.LAT_SIN + " DOUBLE NOT NULL);");
        Log.i(TAG, "Locations Table created");
    }

    private static void createDeparturesTable(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + DEPARTURES_TABLE_NAME + " (" +
                CatchaContract.DeparturesColumns._ID + " INTEGER PRIMARY KEY," +
                CatchaContract.DeparturesColumns.LOCATION_ID + " INTEGER NOT NULL," +
                CatchaContract.DeparturesColumns.START_BP + " TEXT NOT NULL," +
                CatchaContract.DeparturesColumns.DEST_BP + " TEXT NOT NULL," +
                CatchaContract.DeparturesColumns.DEPARTURE_TIME_1 + " TEXT," +
                CatchaContract.DeparturesColumns.DEPARTURE_TIME_2 + " TEXT," +
                CatchaContract.DeparturesColumns.DEPARTURE_TIME_3 + " TEXT," +
                CatchaContract.DeparturesColumns.DEPARTURE_TIME_4 + " TEXT," +
                CatchaContract.DeparturesColumns.TRACK_1 + " TEXT," +
                CatchaContract.DeparturesColumns.TRACK_2 + " TEXT," +
                CatchaContract.DeparturesColumns.TRACK_3 + " TEXT," +
                CatchaContract.DeparturesColumns.TRACK_4 + " TEXT," +
                CatchaContract.DeparturesColumns.DISTANCE + " INTEGER);");
        Log.i(TAG, "Departures Table created");
    }

    public CatchaDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createLocationsTable(db);
        createDeparturesTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LOCATIONS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DEPARTURES_TABLE_NAME);
        createLocationsTable(db);
        createDeparturesTable(db);
    }
}
