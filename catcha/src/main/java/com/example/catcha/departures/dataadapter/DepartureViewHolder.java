package com.example.catcha.departures.dataadapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.catcha.R;
import com.example.catcha.provider.Departure;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DepartureViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.start_bp)
    TextView startBp;
    @BindView(R.id.dest_bp)
    TextView destBp;
    @BindView(R.id.departure_time_1)
    TextView departureTime1;
    @BindView(R.id.departure_time_2)
    TextView departureTime2;
    @BindView(R.id.departure_time_3)
    TextView departureTime3;
    @BindView(R.id.departure_time_4)
    TextView departureTime4;
    @BindView(R.id.track_1)
    TextView track1;
    @BindView(R.id.track_2)
    TextView track2;
    @BindView(R.id.track_3)
    TextView track3;
    @BindView(R.id.track_4)
    TextView track4;
    @BindView(R.id.distance)
    TextView distance;
    @BindView(R.id.tracks)
    LinearLayout tracksLayout;

    private Departure departure;

    private final View view;

    public DepartureViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.view = itemView;
    }

    public void setData(Departure departure) {
        this.departure = departure;
    }

    public void clearData() {
        this.departure = null;
    }

    public void bindDeparture(Departure departure) {
        startBp.setText(departure.startBp);
        destBp.setText(departure.destBp);
        departureTime1.setText(departure.getDepartureTime1AsFormattedString());
        departureTime2.setText(departure.getDepartureTime2AsFormattedString());
        departureTime3.setText(departure.getDepartureTime3AsFormattedString());
        departureTime4.setText(departure.getDepartureTime4AsFormattedString());
        if (TextUtils.isEmpty(departure.track1) && TextUtils.isEmpty(departure.track2) && TextUtils.isEmpty(departure.track3) && TextUtils.isEmpty(departure.track4)) {
            tracksLayout.setVisibility(View.GONE);
        } else {
            track1.setText(departure.track1);
            track2.setText(departure.track2);
            track3.setText(departure.track3);
            track4.setText(departure.track4);
            tracksLayout.setVisibility(View.VISIBLE);
        }
        distance.setText(String.format(view.getResources().getString(R.string.distance), departure.distance));
    }
}
