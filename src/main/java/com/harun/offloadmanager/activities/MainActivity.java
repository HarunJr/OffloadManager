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
import com.harun.offloadmanager.fragments.DetailsFragment;
import com.harun.offloadmanager.fragments.VehiclesFragment;

public class MainActivity extends AppCompatActivity implements VehiclesFragment.OnFragmentInteractionListener {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    CollapsingToolbarLayout collapsingToolbarLayout;
    RecyclerView recyclerView;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: Create *calendarView* or use library https://github.com/GerritHoekstra/CompactCalendarViewToolbar
        Constants.toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(Constants.toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.vehicles_title_label);

        if (findViewById(R.id.vehicle_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.vehicle_detail_container, new DetailsFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public void onItemSelected( Uri contentUri) {
        Log.w(LOG_TAG, "onItemSelected " + contentUri);
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(DetailsFragment.DETAIL_URI, getIntent().getData());
            DetailsFragment detailsFragment = new DetailsFragment();
            detailsFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.vehicle_detail_container, detailsFragment)
                    .commit();

        }else {
            startActivity(new Intent(getApplicationContext(), DetailsActivity.class)
                    .setData(contentUri));

        }
    }
}
