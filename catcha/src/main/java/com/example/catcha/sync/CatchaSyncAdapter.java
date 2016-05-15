package com.example.catcha.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.catcha.R;
import com.example.catcha.locations.utils.DayOrderUtils;
import com.example.catcha.provider.Departure;
import com.example.catcha.provider.Location;
import com.example.catcha.sync.model.Connection;
import com.example.catcha.sync.model.Connections;
import com.example.catcha.sync.rest.TransportService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CatchaSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = CatchaSyncAdapter.class.getSimpleName();

    public static final String ACTION_DATA_UPDATED = "com.example.catcha.ACTION_DATA_UPDATED";

    // seconds
    public static final int SYNC_INTERVAL = 60;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    private TransportService transportService;

    private long nextSyncTime = Long.MAX_VALUE;

    public CatchaSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        init();
    }

    private void init() {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://transport.opendata.ch/v1/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        transportService = retrofit.create(TransportService.class);

        Log.d(TAG, "CatchaSyncAdapter initialized");
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "Starting sync...");

        GpsLocationProvider gpsLocationProvider = new GpsLocationProvider();
        android.location.Location gpsLocation = gpsLocationProvider.getLocation(getContext());
        if (gpsLocation == null) {
            Log.e(TAG, "Could not retrieve location. Skipping synchronisation.");
            return;
        }
        Log.d(TAG, "Locked on location " + gpsLocation);

        List<Location> currentLocations = getLocationByEnabledCurrentDayInRange(gpsLocation);
        Log.d(TAG, "Found " + currentLocations.size() + " relevant locations");

        deleteOutOfRangeDepartures(currentLocations);

        nextSyncTime = Long.MAX_VALUE;

        Log.d(TAG, "Fetching and storing Departures for " + currentLocations.size() + " Locations");
        for (Location l : currentLocations) {
            Log.d(TAG, "Fetching departures for Location: " + l);

            float[] distanceResults = new float[1];
            android.location.Location.distanceBetween(gpsLocation.getLatitude(), gpsLocation.getLongitude(), l.lat, l.lon, distanceResults);

            fetchAndStoreDeparture(l.startBp, l.endBp, l.id, distanceResults[0]);
        }
    }

    /**
     * Reads all user defined {@link Location} which are currently valid (enabled, today and in range) and returns them.
     *
     * @return list of LocationEntry
     */
    private List<Location> getLocationByEnabledCurrentDayInRange(android.location.Location gpsLocation) {
        Log.d(TAG, "Getting valid locations for now ...");

        return Location.getLocations(getContext().getContentResolver(),
                Location.buildLocationsEnabledWithTimeAndInDistanceSelection(
                        DayOrderUtils.getDayOfWeek(),
                        gpsLocation.getLatitude(),
                        gpsLocation.getLongitude(), 1.0));
    }

    private void deleteOutOfRangeDepartures(List<Location> locations) {
        List<String> locationIds = new ArrayList<>();
        for (Location l : locations) {
            locationIds.add(String.valueOf(l.id));
        }
        List<Departure> obsoleteDepartures = Departure.getDepartures(getContext().getContentResolver(), Departure.buildDeparturesNotInLocationSelection(locationIds));
        for (Departure d : obsoleteDepartures) {
            Departure.deleteDeparture(getContext().getContentResolver(), d.id);
        }
        Log.d(TAG, "Deleted " + obsoleteDepartures.size() + " obsolete Departures.");
    }

    private void fetchAndStoreDeparture(String from, String to, long locationId, float distance) {
        Call<Connections> connections = transportService.findConnections(from, to);

        try {
            Response<Connections> response = connections.execute();

            if (response.isSuccessful()) {
                Connections result = response.body();
                storeConnections(result.getConnections(), locationId, distance);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void storeConnections(List<Connection> connections, final long locationId, final float distance) {

        List<Departure> departures = Departure.getDepartures(getContext().getContentResolver(), Departure.DEPARTURES_BY_LOCATION_SELECTION, Long.toString(locationId));

        if (departures.size() > 1) {
            for (int i = 1; i < departures.size(); i++) {
                Departure.deleteDeparture(getContext().getContentResolver(), departures.get(i).id);
            }
        }

        Set<String> startBps = new HashSet<>();
        Set<String> destBps = new HashSet<>();
        List<String> departureTimes = new ArrayList<>();
        List<String> tracks = new ArrayList<>();

        for (Connection connection : connections) {
            startBps.add(connection.getFrom().getStation().getName());
            destBps.add(connection.getTo().getStation().getName());
            long departureTime = connection.getFrom().getDeparture().getTime();
            departureTimes.add(String.valueOf(departureTime));
            tracks.add(connection.getFrom().getPlatform());
        }

        String startBp = "";
        String destBp = "";

        if (startBps.size() >= 1) {
            startBp = startBps.iterator().next();
        }
        if (destBps.size() >= 1) {
            destBp = destBps.iterator().next();
        }

        Departure departure = new Departure(locationId, startBp, destBp, departureTimes, tracks, (int) distance);
        Log.d(TAG, "Attempting to store new Departure: " + departure);

        if (departures.size() == 0) {
            Departure.addDeparture(getContext().getContentResolver(), departure);
        } else {
            departure.id = departures.get(0).id;
            Departure.updateDeparture(getContext().getContentResolver(), departure);
        }

        if (departure.getDepartureTime1InMillis() < nextSyncTime) {
            nextSyncTime = departure.getDepartureTime1InMillis();
        }
    }

    public static void configurePeriodicSync(Context context) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(SYNC_INTERVAL, SYNC_FLEXTIME)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle())
                    .build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), SYNC_INTERVAL);
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    public static void cancelSync(Context context) {
        ContentResolver.cancelSync(getSyncAccount(context), context.getString(R.string.content_authority));
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if (null == accountManager.getPassword(newAccount)) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        CatchaSyncAdapter.configurePeriodicSync(context);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }
}
