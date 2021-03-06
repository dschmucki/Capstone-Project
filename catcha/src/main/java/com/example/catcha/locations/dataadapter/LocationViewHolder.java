package com.example.catcha.locations.dataadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.catcha.locations.LocationClickHandler;
import com.example.catcha.R;
import com.example.catcha.provider.Location;

public abstract class LocationViewHolder extends RecyclerView.ViewHolder {

    private static final float LOCATION_ENABLED_ALPHA = 1f;
    private static final float LOCATION_DISABLED_ALPHA = 0.69f;

    private final TextView startBp;
    private final TextView endBp;
    private final CompoundButton enabled;
    public final View arrow;

    Location location;

    LocationViewHolder(View itemView, final LocationClickHandler locationClickHandler) {
        super(itemView);
        startBp = (TextView) itemView.findViewById(R.id.start_bp);
        endBp = (TextView) itemView.findViewById(R.id.end_bp);
        enabled = (CompoundButton) itemView.findViewById(R.id.enabled);
        arrow = itemView.findViewById(R.id.arrow);

        enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                locationClickHandler.setLocationEnabled(location, isChecked);
            }
        });
    }

    void setData(Location location) {
        this.location = location;
    }

    public void clearData() {
        this.location = null;
    }

    public abstract void bindLocation(Context context, Location location);

    void bindEnabledSwitch(Context context, Location location) {
        enabled.setChecked(location.enabled);
        ((SwitchCompat) enabled).setTextOn(context.getString(R.string.on_switch));
        ((SwitchCompat) enabled).setTextOff(context.getString(R.string.off_switch));
    }

    void bindStartBp(Location location) {
        startBp.setAlpha(location.enabled ? LOCATION_ENABLED_ALPHA : LOCATION_DISABLED_ALPHA);
        startBp.setText(location.startBp);
    }

    void bindEndBp(Location location) {
        endBp.setAlpha(location.enabled ? LOCATION_ENABLED_ALPHA : LOCATION_DISABLED_ALPHA);
        endBp.setText(location.endBp);
    }
}
