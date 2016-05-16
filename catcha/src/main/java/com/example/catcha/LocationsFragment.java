package com.example.catcha;


import android.app.DialogFragment;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.catcha.locations.LocationClickHandler;
import com.example.catcha.locations.LocationUpdateHandler;
import com.example.catcha.locations.ScrollHandler;
import com.example.catcha.locations.dataadapter.LocationsAdapter;
import com.example.catcha.provider.Location;
import com.example.catcha.widget.EmptyViewController;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ScrollHandler, LocationDialogFragment.LocationDialogListener {

    // Views
    @BindView(R.id.locations_recycler_view)
    RecyclerView recyclerView;

    // Data
    private long scrollToLocationId = Location.INVALID_ID;

    // Controllers
    private LocationsAdapter locationsAdapter;
    private LocationUpdateHandler locationUpdateHandler;
    private EmptyViewController emptyViewController;
    private LocationClickHandler locationClickHandler;
    private LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_locations, container, false);
        ButterKnife.bind(this, view);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        locationUpdateHandler = new LocationUpdateHandler(getActivity(), this);
        emptyViewController = new EmptyViewController(recyclerView, view.findViewById(R.id.locations_empty_view));
        locationClickHandler = new LocationClickHandler(savedInstanceState, locationUpdateHandler);
        locationsAdapter = new LocationsAdapter(getActivity(), savedInstanceState, locationClickHandler, this);
        recyclerView.setAdapter(locationsAdapter);

        getLoaderManager().initLoader(0, null, this);

        return view;
    }

    @Override
    public void smoothScrollTo(int position) {
        layoutManager.scrollToPositionWithOffset(position, 20);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        locationsAdapter.saveInstance(outState);
        locationClickHandler.saveInstance(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return Location.getLocationsCursorLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        emptyViewController.setEmpty(cursor.getCount() == 0);
        locationsAdapter.swapCursor(cursor);
        if (scrollToLocationId != Location.INVALID_ID) {
            scrollToLocation(scrollToLocationId);
            setSmoothScrollStableId(Location.INVALID_ID);
        }
    }

    private void scrollToLocation(long locationId) {
        final int locationCount = locationsAdapter.getItemCount();
        int locationPosition = -1;
        for (int i = 0; i < locationCount; i++) {
            long id = locationsAdapter.getItemId(i);
            if (id == locationId) {
                locationPosition = i;
                break;
            }
        }
        if (locationPosition >= 0) {
            locationsAdapter.expand(locationPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        locationsAdapter.swapCursor(null);
    }

    @Override
    public void setSmoothScrollStableId(long stableId) {
        scrollToLocationId = stableId;
    }

    public void onFabClick(View view) {
        startCreatingLocation();
    }

    private void startCreatingLocation() {
        DialogFragment createLocationDialog = new LocationDialogFragment();
        createLocationDialog.setTargetFragment(this, 0);
        createLocationDialog.show(this.getFragmentManager(), "CreateLocationDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(com.example.catcha.sync.model.Location startBp, com.example.catcha.sync.model.Location endBp) {
        Location location = new Location(startBp, endBp);
        locationUpdateHandler.asyncAddLocation(location);
    }
}
