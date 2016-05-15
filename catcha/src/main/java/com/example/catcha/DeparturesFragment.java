package com.example.catcha;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.catcha.departures.dataadapter.DeparturesAdapter;
import com.example.catcha.provider.Departure;
import com.example.catcha.widget.EmptyViewController;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeparturesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.departures_main)
    ViewGroup mainLayout;
    @BindView(R.id.departures_recycler_view)
    RecyclerView recyclerView;

    private DeparturesAdapter departuresAdapter;
    private EmptyViewController emptyViewController;
    private LinearLayoutManager layoutManager;
    private ScheduledFuture updateFuture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_departures, container, false);
        ButterKnife.bind(this, view);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        emptyViewController = new EmptyViewController(recyclerView, view.findViewById(R.id.departures_empty_view));
        departuresAdapter = new DeparturesAdapter(getActivity());
        recyclerView.setAdapter(departuresAdapter);

        scheduleDepartureUpdates();

        return view;
    }

    private void scheduleDepartureUpdates() {
        if (updateFuture == null) {
            final Handler mainHandler = new Handler(Looper.getMainLooper());
            long currentTimeMillis = System.currentTimeMillis();
            long secondsUntilNextMinute = ((1 * 60 * 1000 + 0 * 1000 + currentTimeMillis + 59999) / 60000 * 60000 - (1 * 60 * 1000 + 0 * 1000 + currentTimeMillis)) / 1000;
            updateFuture = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    final int firstVisible = layoutManager.findFirstVisibleItemPosition();
                    final int lastVisible = layoutManager.findLastVisibleItemPosition();

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            departuresAdapter.notifyItemRangeChanged(firstVisible, lastVisible - firstVisible + 1);
                        }
                    });
                }
            }, secondsUntilNextMinute, 60, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onDestroyView() {
        if (updateFuture != null) {
            updateFuture.cancel(true);
            updateFuture = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return Departure.getDeparturesCursorLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        emptyViewController.setEmpty(cursor.getCount() == 0);
        departuresAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        departuresAdapter.swapCursor(null);
    }
}
