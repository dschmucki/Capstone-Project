package com.example.catcha.locations.dataadapter;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.catcha.R;
import com.example.catcha.locations.LocationClickHandler;
import com.example.catcha.locations.utils.DayOrderUtils;
import com.example.catcha.provider.DaysOfWeek;
import com.example.catcha.provider.Location;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Locale;

public class ExpandedLocationViewHolder extends LocationViewHolder {

    private static final String TAG = ExpandedLocationViewHolder.class.getSimpleName();

    public final LinearLayout repeatDays;
    public final CompoundButton[] dayButtons = new CompoundButton[7];
    public final ImageButton delete;

    private final int[] dayOrder;

    public ExpandedLocationViewHolder(View itemView, final LocationClickHandler locationClickHandler, final LocationsAdapter locationsAdapter) {
        super(itemView, locationClickHandler);
        final Context context = itemView.getContext();
        dayOrder = DayOrderUtils.getDayOrder();
        final Resources.Theme theme = context.getTheme();
        int[] attrs = new int[]{android.R.attr.selectableItemBackground};

        final TypedArray typedArray = theme.obtainStyledAttributes(attrs);
        final LayerDrawable background = new LayerDrawable(new Drawable[]{
                context.getResources().getDrawable(R.drawable.location_background_expanded), typedArray.getDrawable(0)});
        itemView.setBackground(background);
        typedArray.recycle();

        final int firstDay = 1;
        delete = (ImageButton) itemView.findViewById(R.id.delete);
        repeatDays = (LinearLayout) itemView.findViewById(R.id.repeat_days);

        LayoutInflater inflater = LayoutInflater.from(context);
        for (int i = 0; i < DaysOfWeek.DAYS_IN_A_WEEK; i++) {
            final CompoundButton dayButton = (CompoundButton) inflater.inflate(R.layout.day_button, repeatDays, false);
            dayButton.setText(getShortWeekday(i, firstDay));
            dayButton.setContentDescription(getLongWeekday(i, firstDay));
            repeatDays.addView(dayButton);
            dayButtons[i] = dayButton;
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationsAdapter.collapse(getAdapterPosition());
            }
        });

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationsAdapter.collapse(getAdapterPosition());
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationClickHandler.onDeleteClicked(location);
            }
        });
        for (int i = 0; i < DaysOfWeek.DAYS_IN_A_WEEK; i++) {
            final int buttonIndex = i;
            dayButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final boolean isChecked = ((CompoundButton) view).isChecked();
                    locationClickHandler.setDayOfWeekEnabled(location, isChecked, buttonIndex);
                }
            });
        }

    }

    @Override
    public void bindLocation(Context context, Location location) {
        Log.d(TAG, "Binding location: " + location);
        setData(location);
        bindEnabledSwitch(context, location);
        bindStartBp(location);
        bindEndBp(location);
        bindDaysOfWeekButtons(location);
    }

    private void bindDaysOfWeekButtons(Location location) {
        HashSet<Integer> setDays = location.daysOfWeek.getSetDays();
        for (int i = 0; i < DaysOfWeek.DAYS_IN_A_WEEK; i++) {
            final CompoundButton dayButton = dayButtons[i];
            if (setDays.contains(dayOrder[i])) {
                dayButton.setChecked(true);
                dayButton.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
            } else {
                dayButton.setChecked(false);
                dayButton.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
            }
        }
        repeatDays.setVisibility(View.VISIBLE);
    }

    private static String[] shortWeekdays = null;
    private static final String DATE_FORMAT_SHORT = "ccccc";

    private static String[] longWeekdays = null;
    private static final String DATE_FORMAT_LONG = "EEEE";

    private static Locale localeUsedForWeekdays;

    private static String getShortWeekday(int position, int firstDay) {
        generateShortAndLongWeekdaysIfNeeded();
        return shortWeekdays[(position + firstDay) % DaysOfWeek.DAYS_IN_A_WEEK];
    }

    private static String getLongWeekday(int position, int firstDay) {
        generateShortAndLongWeekdaysIfNeeded();
        return longWeekdays[(position + firstDay) % DaysOfWeek.DAYS_IN_A_WEEK];
    }

    private static void generateShortAndLongWeekdaysIfNeeded() {
        if (shortWeekdays != null && longWeekdays != null && !localeHasChanged()) {
            // nothing to do
            return;
        }
        if (shortWeekdays == null) {
            shortWeekdays = new String[DaysOfWeek.DAYS_IN_A_WEEK];
        }
        if (longWeekdays == null) {
            longWeekdays = new String[DaysOfWeek.DAYS_IN_A_WEEK];
        }

        final SimpleDateFormat shortFormat = new SimpleDateFormat(DATE_FORMAT_SHORT, Locale.GERMAN);
        final SimpleDateFormat longFormat = new SimpleDateFormat(DATE_FORMAT_LONG, Locale.GERMAN);

        // Create a date (2014/07/20) that is a Sunday
        final long aSunday = new GregorianCalendar(2014, Calendar.JULY, 20).getTimeInMillis();

        for (int i = 0; i < DaysOfWeek.DAYS_IN_A_WEEK; i++) {
            final long dayMillis = aSunday + i * DateUtils.DAY_IN_MILLIS;
            shortWeekdays[i] = shortFormat.format(new Date(dayMillis));
            longWeekdays[i] = longFormat.format(new Date(dayMillis));
        }

        // Track the Locale used to generate these weekdays
        localeUsedForWeekdays = Locale.getDefault();
    }

    private static boolean localeHasChanged() {
        return localeUsedForWeekdays != Locale.getDefault();
    }
}
