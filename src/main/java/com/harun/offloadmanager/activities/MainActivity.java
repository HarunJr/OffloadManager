package com.harun.offloadmanager.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.harun.offloadmanager.Constants;
import com.harun.offloadmanager.R;
import com.harun.offloadmanager.fragments.VehiclesFragment;

public class MainActivity extends AppCompatActivity implements VehiclesFragment.OnFragmentInteractionListener {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    CollapsingToolbarLayout collapsingToolbarLayout;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: Create *calendarView* or use library https://github.com/GerritHoekstra/CompactCalendarViewToolbar
        Constants.toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(Constants.toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.vehicles_title_label);
    }

    @Override
    public void onItemSelected( Uri contentUri) {
        Log.w(LOG_TAG, "onItemSelected " + contentUri);
        startActivity(new Intent(getApplicationContext(), DetailsActivity.class)
                .setData(contentUri));
    }
}
