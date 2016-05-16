package com.example.catcha.locations.dataadapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.example.catcha.R;
import com.example.catcha.locations.LocationClickHandler;
import com.example.catcha.provider.Location;

public class CollapsedLocationViewHolder extends LocationViewHolder {

    private final TextView daysOfWeek;
    private final View hairLine;

    public CollapsedLocationViewHolder(View itemView, final LocationClickHandler locationClickHandler, final LocationsAdapter locationsAdapter) {
        super(itemView, locationClickHandler);
        this.daysOfWeek = (TextView) itemView.findViewById(R.id.days_of_week);
        this.hairLine = itemView.findViewById(R.id.hairline);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationsAdapter.expand(getAdapterPosition());
            }
        });

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationsAdapter.expand(getAdapterPosition());
            }
        });
    }

    @Override
    public void bindLocation(Context context, Location location) {
        setData(location);
        bindEnabledSwitch(context, location);
        bindStartBp(location);
        bindEndBp(location);
        bindRepeatText(context, location);
        hairLine.setVisibility(View.VISIBLE);
    }

    private void bindRepeatText(Context context, Location location) {
        final String daysOfWeekText = location.daysOfWeek.toString(context, 2);
        if (!TextUtils.isEmpty(daysOfWeekText)) {
            daysOfWeek.setText(daysOfWeekText);
            daysOfWeek.setContentDescription(location.daysOfWeek.toAccessibilityString(context, 2));
            daysOfWeek.setVisibility(View.VISIBLE);
        } else {
            daysOfWeek.setVisibility(View.GONE);
        }
    }


}
