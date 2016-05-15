package com.example.catcha.locations;

import android.os.Bundle;
import android.util.Log;

import com.example.catcha.locations.utils.DayOrderUtils;
import com.example.catcha.provider.Location;

public class LocationClickHandler {

    private static final String TAG = LocationClickHandler.class.getSimpleName();
    private static final String KEY_PREVIOUS_DAY_MAP = "previousDayMap";

    private final LocationUpdateHandler locationUpdateHandler;

    private Bundle previousDaysOfWeekMap;
    private int[] dayOrder;

    public LocationClickHandler(Bundle savedState, LocationUpdateHandler locationUpdateHandler) {
        this.locationUpdateHandler = locationUpdateHandler;
        if (savedState != null) {
            this.previousDaysOfWeekMap = savedState.getBundle(KEY_PREVIOUS_DAY_MAP);
        }
        if (previousDaysOfWeekMap == null) {
            previousDaysOfWeekMap = new Bundle();
        }
        dayOrder = DayOrderUtils.getDayOrder();
    }

    public void saveInstance(Bundle outState) {
        outState.putBundle(KEY_PREVIOUS_DAY_MAP, previousDaysOfWeekMap);
    }

    public void setLocationEnabled(Location location, boolean newState) {
        if (newState != location.enabled) {
            location.enabled = newState;
            locationUpdateHandler.asyncUpdateLocation(location);
            Log.d(TAG, "Updating location enabled state to " + newState);
        }
    }

    public void setDayOfWeekEnabled(Location location, boolean checked, int index) {
        location.daysOfWeek.setDaysOfWeek(checked, dayOrder[index]);
        locationUpdateHandler.asyncUpdateLocation(location);
    }

    public void onDeleteClicked(Location location) {
        locationUpdateHandler.asyncDeleteLocation(location);
        Log.d(TAG, "Deleting location.");
    }
}
