package com.example.catcha.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class CatchaAuthenticatorService extends Service {

    private CatchaAuthenticator catchaAuthenticator;

    @Override
    public void onCreate() {
        catchaAuthenticator = new CatchaAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return catchaAuthenticator.getIBinder();
    }
}
