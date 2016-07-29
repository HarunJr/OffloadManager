package com.harun.offloadmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class AddVehicle extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        Constants.toolbar = (Toolbar) findViewById(R.id.add_vehicle_activity_toolbar);
        setSupportActionBar(Constants.toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.add_vehicle_title);
    }
}
