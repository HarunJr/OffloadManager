package com.harun.offloadmanager.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CalendarView;

import com.harun.offloadmanager.Constants;
import com.harun.offloadmanager.DateHelper;
import com.harun.offloadmanager.R;
import com.harun.offloadmanager.UserLocalStore;
import com.harun.offloadmanager.fragments.DetailsFragment;
import com.harun.offloadmanager.fragments.VehiclesFragment;
import com.harun.offloadmanager.fragments.VehiclesHistory;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements VehiclesFragment.OnFragmentInteractionListener {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    CollapsingToolbarLayout collapsingToolbarLayout;
    CalendarView calendarView;
    private boolean mTwoPane;
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Constants.toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);

        userLocalStore = new UserLocalStore(getApplicationContext());
        //TODO: Create *calendarView* or use library https://github.com/GerritHoekstra/CompactCalendarViewToolbar

        checkPane(savedInstanceState);
        displayCalendarView();

    }

    private void checkPane(Bundle savedInstanceState){
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
            authenticate();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(LOG_TAG, "onStart ");

        if (authenticate()){
            addVehiclesFragment();
        }else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }


    private boolean authenticate(){
        Log.w(LOG_TAG, String.valueOf(userLocalStore.getUserLoggedIn()));
        return userLocalStore.getUserLoggedIn();
    }

    private void displayCalendarView() {
        calendarView = (CalendarView) findViewById(R.id.calendar_view);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth) {
//                Toast.makeText(getApplicationContext(), dayOfMonth, Toast.LENGTH_SHORT).show();

                //TODO: Check how day, time and calendarDay coiniside at midnight
                long dateTime = System.currentTimeMillis();
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
                    addVehiclesFragment();
                }

                Log.w(LOG_TAG, "calendarDay " + calDayDate);

//                PostToServerTask postToServerTask = new PostToServerTask(getApplicationContext());
//                postToServerTask.execute(method, calendarDay);

            }
        });
    }

    private void addVehiclesHistoryFragment(long calMilliTime, long dayNext) {
        //TODO: Learn on use of 'putParcelable' for URI (Sunshine detail activity)
        Log.w(LOG_TAG, "addVehiclesHistoryFragment " + calMilliTime + ": " + dayNext);
        Bundle args = new Bundle();
        args.putLong(Constants.CALENDAR_DAY, calMilliTime);
        args.putLong(Constants.NEXT_DAY, dayNext);

        VehiclesHistory vehiclesHistory = new VehiclesHistory();
        vehiclesHistory.setArguments(args);
        Log.w(LOG_TAG, "addVehiclesHistoryFragment " + args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_vehicles, vehiclesHistory)
                .commit();

        setSupportActionBar(Constants.toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.vehicles_history_title_label);
    }

    public void addVehiclesFragment() {
        //TODO: Learn on use of 'putParcelable' for URI (Sunshine detail activity)
        Log.w(LOG_TAG, "addVehiclesFragment ");

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_vehicles, new VehiclesFragment())
                .commit();

        setSupportActionBar(Constants.toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.vehicles_title_label);
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        Log.w(LOG_TAG, "onItemSelected " + contentUri);

        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(DetailsFragment.DETAIL_URI, getIntent().getData());
            DetailsFragment detailsFragment = new DetailsFragment();
            detailsFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.vehicle_detail_container, detailsFragment)
                    .commit();

        } else {
            startActivity(new Intent(getApplicationContext(), DetailsActivity.class)
                    .setData(contentUri));
        }
    }
}
