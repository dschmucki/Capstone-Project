package com.example.catcha.locations.utils;

import com.example.catcha.provider.DaysOfWeek;

import java.util.Calendar;

public final class DayOrderUtils {

    private static final int[] DAY_ORDER = {
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
            Calendar.SUNDAY
    };

    public static int[] getDayOrder() {
        return DAY_ORDER;
    }

    public static int getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        DaysOfWeek daysOfWeek = new DaysOfWeek(0);
        daysOfWeek.setDaysOfWeek(true, calendar.get(Calendar.DAY_OF_WEEK));
        return daysOfWeek.getBitSet();
    }

}
