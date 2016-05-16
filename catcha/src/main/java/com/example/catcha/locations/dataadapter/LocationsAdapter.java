package com.example.catcha.locations.dataadapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.catcha.R;
import com.example.catcha.locations.LocationClickHandler;
import com.example.catcha.locations.ScrollHandler;
import com.example.catcha.provider.Location;

public class LocationsAdapter extends RecyclerView.Adapter<LocationViewHolder> {

    private static final String TAG = LocationsAdapter.class.getSimpleName();
    private static final String KEY_EXPANDED_ID = "expandedId";
    private static final int VIEW_TYPE_LOCATION_COLLAPSED = R.layout.location_collapsed;
    private static final int VIEW_TYPE_LOCATION_EXPANDED = R.layout.location_expanded;

    private final Context context;
    private final LayoutInflater inflater;

    private final LocationClickHandler locationClickHandler;
    private final ScrollHandler scrollHandler;

    private int expandedPosition = -1;
    private long expandedId = Location.INVALID_ID;
    private Cursor cursor;

    public LocationsAdapter(Context context, Bundle savedState, LocationClickHandler locationClickHandler, ScrollHandler smoothScrollController) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.scrollHandler = smoothScrollController;
        this.locationClickHandler = locationClickHandler;
        if (savedState != null) {
            expandedId = savedState.getLong(KEY_EXPANDED_ID, Location.INVALID_ID);
        }

        setHasStableIds(true);
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = inflater.inflate(viewType, parent, false);
        if (viewType == VIEW_TYPE_LOCATION_COLLAPSED) {
            return new CollapsedLocationViewHolder(v, locationClickHandler, this);
        } else {
            return new ExpandedLocationViewHolder(v, locationClickHandler, this);
        }
    }

    @Override
    public void onViewRecycled(LocationViewHolder holder) {
        super.onViewRecycled(holder);
        holder.clearData();
    }

    @Override
    public void onBindViewHolder(LocationViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            Log.e(TAG, "Failed to bind location " + position);
            return;
        }
        final Location location = new Location(cursor);
        holder.bindLocation(context, location);
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        if (cursor == null || !cursor.moveToPosition(position)) {
            return RecyclerView.NO_ID;
        }
        return new Location(cursor).id;
    }

    @Override
    public int getItemViewType(int position) {
        final long stableId = getItemId(position);
        return stableId != RecyclerView.NO_ID && stableId == expandedId ? VIEW_TYPE_LOCATION_EXPANDED : VIEW_TYPE_LOCATION_COLLAPSED;
    }

    public void saveInstance(Bundle outState) {
        outState.putLong(KEY_EXPANDED_ID, expandedId);
    }

    public void expand(int position) {
        final long stableId = getItemId(position);
        if (expandedId == stableId) {
            return;
        }
        expandedId = stableId;
        scrollHandler.smoothScrollTo(position);
        if (expandedPosition >= 0) {
            notifyItemChanged(expandedPosition);
        }
        expandedPosition = position;
        notifyItemChanged(position);
    }

    public void collapse(int position) {
        expandedId = Location.INVALID_ID;
        expandedPosition = -1;
        notifyItemChanged(position);
    }

    public void swapCursor(Cursor cursor) {
        if (this.cursor == cursor) {
            return;
        }
        if (this.cursor != null) {
            this.cursor.close();
        }
        this.cursor = cursor;
        notifyDataSetChanged();
    }
}
