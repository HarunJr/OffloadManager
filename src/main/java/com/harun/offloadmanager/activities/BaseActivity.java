package com.harun.offloadmanager.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.harun.offloadmanager.infrastructure.OffloadManagerApplication;
import com.squareup.otto.Bus;

/**
 * Created by HARUN on 12/18/2016.
 */

public class BaseActivity extends AppCompatActivity {
    protected OffloadManagerApplication application;
    protected Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (OffloadManagerApplication) getApplication();
        bus = application.getBus();
        bus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }
}
