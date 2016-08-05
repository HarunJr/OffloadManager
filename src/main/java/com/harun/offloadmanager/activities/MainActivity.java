package com.harun.offloadmanager.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.harun.offloadmanager.Constants;
import com.harun.offloadmanager.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Constants.toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(Constants.toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.vehicles_title_label);
    }
}
