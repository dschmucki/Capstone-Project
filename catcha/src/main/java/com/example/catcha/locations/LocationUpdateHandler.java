package com.example.catcha.locations;

import android.content.Context;
import android.os.AsyncTask;

import com.example.catcha.provider.Location;

public class LocationUpdateHandler {

    private final Context context;
    private final ScrollHandler scrollHandler;

    public LocationUpdateHandler(Context context, ScrollHandler scrollHandler) {
        this.context = context;
        this.scrollHandler = scrollHandler;
    }

    public void asyncAddLocation(final Location location) {
        final AsyncTask<Void, Void, Location> addTask = new AsyncTask<Void, Void, Location>() {

            @Override
            protected Location doInBackground(Void... params) {
                if (location != null) {
                    Location newLocation = Location.addLocation(context.getContentResolver(), location);
                    scrollHandler.setSmoothScrollStableId(newLocation.id);
                    return location;
                }
                return null;
            }
        };
        addTask.execute();
    }

    public void asyncUpdateLocation(final Location location) {
        final AsyncTask<Void, Void, Location> updateTask = new AsyncTask<Void, Void, Location>() {

            @Override
            protected Location doInBackground(Void... params) {
                Location.updateLocation(context.getContentResolver(), location);
                return location;
            }
        };
        updateTask.execute();
    }

    public void asyncDeleteLocation(final Location location) {
        final AsyncTask<Void, Void, Boolean> deleteTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                if (location == null) {
                    return false;
                }
                return Location.deleteLocation(context.getContentResolver(), location.id);
            }
        };
        deleteTask.execute();
    }
}
