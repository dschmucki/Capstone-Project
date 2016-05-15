package com.example.catcha.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class CatchaSyncService extends Service {

    private static final String TAG = CatchaSyncAdapter.class.getSimpleName();

    private static final Object syncAdapterLock = new Object();
    private static CatchaSyncAdapter catchaSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        synchronized (syncAdapterLock) {
            if (catchaSyncAdapter == null) {
                catchaSyncAdapter = new CatchaSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return catchaSyncAdapter.getSyncAdapterBinder();
    }
}
