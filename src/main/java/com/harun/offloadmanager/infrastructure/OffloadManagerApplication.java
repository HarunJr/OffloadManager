package com.harun.offloadmanager.infrastructure;

import android.app.Application;

import com.harun.offloadmanager.live.Module;
import com.squareup.otto.Bus;

/**
 * Created by HARUN on 12/18/2016.
 */

public class OffloadManagerApplication extends Application {
    public static final String ONLINE_URL = "http://transit.gemilab.com/";
//    public static final String BASE_URL = "http://192.168.245.2/";
//    public static final String BASE_WIFI_URL = "http://192.168.0.17/"; //Ipconfig LAN
    private Bus bus;

    public OffloadManagerApplication(){
        bus = new Bus();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Module.Register(this);
    }

    public Bus getBus() {
        return bus;
    }

}
