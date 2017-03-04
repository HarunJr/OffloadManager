package com.harun.offloadmanager.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.Toast;

import com.harun.offloadmanager.Constants;
import com.harun.offloadmanager.DateHelper;
import com.harun.offloadmanager.R;
import com.harun.offloadmanager.data.LocalStore;
import com.harun.offloadmanager.fragments.DetailsFragment;
import com.harun.offloadmanager.fragments.VehiclesFragment;
import com.harun.offloadmanager.fragments.VehiclesHistory;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements VehiclesFragment.OnFragmentInteractionListener {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    CollapsingToolbarLayout collapsingToolbarLayout;
    CalendarView calendarView;
    private boolean mTwoPane;
    LocalStore userLocalStore;
    long dateTime;
    private Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Constants.toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);

        userLocalStore = new LocalStore(getApplicationContext());

        //TODO: Create *calendarView* or use library https://github.com/GerritHoekstra/CompactCalendarViewToolbar

        checkPane(savedInstanceState);
    }

    private void checkPane(Bundle savedInstanceState) {
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
            //Proceeds to onStart

            if (authenticate()) {
                displayCalendarView();

                dateTime = System.currentTimeMillis();
                addVehiclesFragment(dateTime);

            } else {
                startActivity(new Intent(this, LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(LOG_TAG, "onStart ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.w(LOG_TAG, "onRestart ");
        // Activity being restarted from stopped state
    }

    private boolean authenticate() {
        Log.w(LOG_TAG, String.valueOf(userLocalStore.getUserLoggedIn()));
        return userLocalStore.getUserLoggedIn();
    }

    private void displayCalendarView() {
        calendarView = (CalendarView) findViewById(R.id.calendar_view);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
//                Toast.makeText(getApplicationContext(), dayOfMonth, Toast.LENGTH_SHORT).show();

                //TODO: Check how day, time and calendarDay coiniside at midnight
                String day = DateHelper.getFormattedDayString(dateTime);
                String time = DateHelper.getFormattedTimeString(dateTime);

                String calendarDay = dayOfMonth + "/" + (month) + "/" + year;
                String parts[] = calendarDay.split("/");

                dayOfMonth = Integer.parseInt(parts[0]);
                month = Integer.parseInt(parts[1]);
                year = Integer.parseInt(parts[2]);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                long calMilliTime = calendar.getTimeInMillis();
                String calDayDate = DateHelper.getFormattedDayString(calMilliTime);
                long dayInMilli = (1000 * 60 * 60 * 24);
                long dayNext = (calMilliTime + dayInMilli);

                Log.w(LOG_TAG, "onSelectedDayChange " + calDayDate + "-------------" + day + "-" + time);
                Log.w(LOG_TAG, "onSelectedDayChange " + calMilliTime + "---" + dayNext);

                if (!calDayDate.equals(day)) {
                    addVehiclesHistoryFragment(calMilliTime, dayNext);
                } else {
                    addVehiclesFragment(dateTime);
                }

                Log.w(LOG_TAG, "calendarDay " + calDayDate);

//                PostToServerTask postToServerTask = new PostToServerTask(getApplicationContext());
//                postToServerTask.execute(method, calendarDay);
            }
        });
    }

    private void addVehiclesHistoryFragment(long calMilliTime, long dayNext) {
        Log.w(LOG_TAG, "addVehiclesHistoryFragment " + calMilliTime + ": " + dayNext);
        Bundle args = new Bundle();
        args.putLong(Constants.CALENDAR_DAY, calMilliTime);
        args.putLong(Constants.NEXT_DAY, dayNext);

        VehiclesHistory vehiclesHistory = new VehiclesHistory();
        vehiclesHistory.setArguments(args);
        Log.w(LOG_TAG, "addVehiclesHistoryFragment " + args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.vehicle_fragment_container, vehiclesHistory)
                .commit();

        setSupportActionBar(Constants.toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.vehicles_history_title_label);
    }

    public void addVehiclesFragment(long dateMilli) {
        //TODO: Learn on use of 'putParcelable' for URI (Sunshine detail activity)
        Bundle args = new Bundle();
        args.putLong(Constants.CURRENT_DAY, dateMilli);

        VehiclesFragment vehiclesFragment = new VehiclesFragment();
        vehiclesFragment.setArguments(args);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final android.support.v4.app.Fragment fragment = fragmentManager.findFragmentById(R.id.vehicle_fragment_container);

        if (fragment == null) {
            Log.w(LOG_TAG, "addVehiclesFragment without fragment Vehicles " + fragment);
            fragmentManager.beginTransaction()
                    .add(R.id.vehicle_fragment_container, vehiclesFragment)
                    .commitAllowingStateLoss();
        } else {
            Log.w(LOG_TAG, "replace with fragment " + fragment);
            fragmentManager.beginTransaction()
                    .replace(R.id.vehicle_fragment_container, vehiclesFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }
        setSupportActionBar(Constants.toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.vehicles_title_label);
    }

    @Override
    public void onFragmentInteraction(Uri contentUri, long dateTime) {
        Log.w(LOG_TAG, "onFragmentInteraction " + contentUri);

        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(Constants.DETAIL_URI, getIntent().getData());
            args.putLong(Constants.CURRENT_DAY, dateTime);

            DetailsFragment detailsFragment = new DetailsFragment();
            detailsFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.vehicle_detail_container, detailsFragment)
                    .commit();
        } else {
            startActivity(new Intent(getApplicationContext(), DetailsActivity.class)
                    .putExtra(Constants.CURRENT_DAY, dateTime)
                    .setData(contentUri));
        }
    }

    @Override
    public void onBackPressed() {
        //do nothing
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }

}
