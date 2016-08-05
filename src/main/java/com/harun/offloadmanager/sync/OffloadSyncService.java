package com.harun.offloadmanager.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class OffloadSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static OffloadSyncAdapter sOffloadSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("SunshineSyncService", "onCreate - SunshineSyncService");
        synchronized (sSyncAdapterLock) {
            if (sOffloadSyncAdapter == null) {
                sOffloadSyncAdapter = new OffloadSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sOffloadSyncAdapter.getSyncAdapterBinder();
    }
}