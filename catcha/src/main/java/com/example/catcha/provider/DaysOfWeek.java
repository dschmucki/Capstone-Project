package com.example.catcha.provider;

import android.content.Context;

import com.example.catcha.R;

import java.text.DateFormatSymbols;
import java.util.HashSet;

/*
 * Days of week code as a single int.
 * 0x00: no day
 * 0x01: Monday
 * 0x02: Tuesday
 * 0x04: Wednesday
 * 0x08: Thursday
 * 0x10: Friday
 * 0x20: Saturday
 * 0x40: Sunday
 */
public final class DaysOfWeek {

    public static final int DAYS_IN_A_WEEK = 7;
    private static final int ALL_DAYS_SET = 0x7f;
    private static final int NO_DAYS_SET = 0;

    private static int convertDayToBitIndex(int day) {
        return (day + 5) % DAYS_IN_A_WEEK;
    }

    private static int convertBitIndexToDay(int bitIndex) {
        return (bitIndex + 1) % DAYS_IN_A_WEEK + 1;
    }

    private int bitSet;

    public DaysOfWeek(int bitSet) {
        this.bitSet = bitSet;
    }

    public String toString(Context context, int firstDay) {
        return toString(context, firstDay, false);
    }

    public String toAccessibilityString(Context context, int firstDay) {
        return toString(context, firstDay, true);
    }

    private String toString(Context context, int firstDay, boolean forAccessibility) {
        StringBuilder stringBuilder = new StringBuilder();

        if (bitSet == NO_DAYS_SET) {
            return "";
        }

        if (bitSet == ALL_DAYS_SET) {
            return context.getText(R.string.every_day).toString();
        }

        int dayCount = 0;
        int bitSet = this.bitSet;
        while (bitSet > 0) {
            if ((bitSet & 1) == 1) dayCount++;
            bitSet >>= 1;
        }

        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] dayList = (forAccessibility || dayCount <= 1) ?
                dfs.getWeekdays() :
                dfs.getShortWeekdays();

        final int startDay = convertDayToBitIndex(firstDay);

        for (int bitIndex = startDay; bitIndex < DAYS_IN_A_WEEK + startDay; ++bitIndex) {
            if ((this.bitSet & (1 << (bitIndex % DAYS_IN_A_WEEK))) != 0) {
                stringBuilder.append(dayList[convertBitIndexToDay(bitIndex)]);
                dayCount -= 1;
                if (dayCount > 0) stringBuilder.append(context.getText(R.string.day_concat));
            }
        }
        return stringBuilder.toString();
    }

    public void setDaysOfWeek(boolean value, int... daysOfWeek) {
        for (int day : daysOfWeek) {
            setBit(convertDayToBitIndex(day), value);
        }
    }

    private boolean isBitEnabled(int bitIndex) {
        return ((this.bitSet & (1 << bitIndex)) > 0);
    }

    private void setBit(int bitIndex, boolean set) {
        if (set) {
            this.bitSet |= (1 << bitIndex);
        } else {
            this.bitSet &= ~(1 << bitIndex);
        }
    }

    public int getBitSet() {
        return this.bitSet;
    }

    public HashSet<Integer> getSetDays() {
        final HashSet<Integer> result = new HashSet<>();
        for (int bitIndex = 0; bitIndex < DAYS_IN_A_WEEK; bitIndex++) {
            if (isBitEnabled(bitIndex)) {
                result.add(convertBitIndexToDay(bitIndex));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "DaysOfWeek{" +
                "mBitSet=" + this.bitSet +
                '}';
    }
}
