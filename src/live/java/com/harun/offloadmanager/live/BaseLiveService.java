package com.harun.offloadmanager.live;

import android.util.Log;

import com.harun.offloadmanager.infrastructure.OffloadManagerApplication;
import com.squareup.otto.Bus;

/**
 * Created by HARUN on 12/18/2016.
 */

public class BaseLiveService {
    public static final String LOG_TAG = BaseLiveService.class.getSimpleName();
    protected OffloadManagerApplication application;
    protected Bus bus;
    protected OffloadWebServices api;

    public BaseLiveService(OffloadManagerApplication application, OffloadWebServices api){
        this.application = application;
        this.api = api;
        bus = application.getBus();
        bus.register(this);
        Log.w(LOG_TAG, "BaseLiveService bus.post" );
    }
}
