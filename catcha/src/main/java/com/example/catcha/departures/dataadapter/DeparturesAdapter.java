package com.example.catcha.departures.dataadapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.catcha.R;
import com.example.catcha.provider.Departure;

public class DeparturesAdapter extends RecyclerView.Adapter<DepartureViewHolder> {

    private static final String TAG = DeparturesAdapter.class.getSimpleName();

    private final Context context;
    private final LayoutInflater inflater;

    private Cursor cursor;

    public DeparturesAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);

        setHasStableIds(true);
    }

    @Override
    public DepartureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = inflater.inflate(viewType, parent, false);
        return new DepartureViewHolder(v);
    }

    @Override
    public void onViewRecycled(DepartureViewHolder holder) {
        super.onViewRecycled(holder);
        holder.clearData();
    }

    @Override
    public void onBindViewHolder(DepartureViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            Log.e(TAG, "Failed to bind departure " + position);
            return;
        }
        final Departure departure = new Departure(cursor);
        holder.bindDeparture(departure);
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
        return new Departure(cursor).id;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.departure;
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
