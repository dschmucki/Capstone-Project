package com.example.catcha.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.catcha.Catcha;
import com.example.catcha.R;
import com.example.catcha.provider.Departure;

public class DepartureWidgetIntentService extends IntentService {

    public DepartureWidgetIntentService() {
        super(DepartureWidgetIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, DepartureWidgetProvider.class));

        // get top departure from contentprovider
        Departure departure = Departure.getNearestDeparture(getContentResolver());

        if (departure == null) {
            return;
        }

        for (int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.widget_departure;

            RemoteViews remoteViews = new RemoteViews(getPackageName(), layoutId);

            remoteViews.setTextViewText(R.id.widget_start_bp, departure.startBp);
            remoteViews.setTextViewText(R.id.widget_dest_bp, departure.destBp);
            remoteViews.setTextViewText(R.id.widget_departure_time_1, departure.getDepartureTime1AsFormattedString());
            remoteViews.setTextViewText(R.id.widget_departure_time_2, departure.getDepartureTime2AsFormattedString());
            remoteViews.setTextViewText(R.id.widget_departure_time_3, departure.getDepartureTime3AsFormattedString());
            remoteViews.setTextViewText(R.id.widget_departure_time_4, departure.getDepartureTime4AsFormattedString());

            Intent launchIntent = new Intent(this, Catcha.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}
