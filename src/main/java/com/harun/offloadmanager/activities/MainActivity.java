package com.harun.offloadmanager.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.harun.offloadmanager.Constants;
import com.harun.offloadmanager.DateHelper;
import com.harun.offloadmanager.R;
import com.harun.offloadmanager.Utilities;
import com.harun.offloadmanager.data.LocalStore;
import com.harun.offloadmanager.fragments.AddUserFragment;
import com.harun.offloadmanager.fragments.DetailsFragment;
import com.harun.offloadmanager.fragments.VehiclesFragment;
import com.harun.offloadmanager.fragments.VehiclesHistory;
import com.harun.offloadmanager.models.User;
import com.harun.offloadmanager.service.UserServices;
import com.harun.offloadmanager.service.VehicleServices;

import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity implements VehiclesFragment.OnVehicleFragmentInteractionListener,
        AddUserFragment.OnAddUSerFragmentInteractionListener {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    CompactCalendarView compactCalendarView;
    private boolean mTwoPane;
    LocalStore userLocalStore;
    private Boolean exit = false;
    public static String AUTH_TOKEN = null;
    String date = DateHelper.getFormattedDateHyphenString(Constants.dateMilli);
    String day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Utilities.isNetworkAvailable(application)) {
            //TODO: Check if there's transactions not synced
            userLocalStore = new LocalStore(getApplicationContext());
            AUTH_TOKEN = userLocalStore.getToken();
            bus.post(new VehicleServices.VehiclesServerRequest(AUTH_TOKEN, date));
            Log.w(LOG_TAG, "onCreate token=" + AUTH_TOKEN);
        } else {
            //TODO:GET SUM of transactions for today and add to vehicles table
        }

        setContentView(R.layout.activity_main);
        Constants.toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayCalendarView();
        addVehiclesFragment();
        Log.w(LOG_TAG, "onStart ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.w(LOG_TAG, "onRestart ");
        // Activity being restarted from stopped state
    }

    private void displayCalendarView() {
        compactCalendarView = (CompactCalendarView) findViewById(R.id.calendar_view);
        compactCalendarView.setUseThreeLetterAbbreviation(true);

        // Add event 1 on Sun, 07 Jun 2015 18:20:51 GMT
        //https://github.com/SundeepK/CompactCalendarView/issues/107
        Event ev1 = new Event(Color.GREEN, 1433701251000L);
        compactCalendarView.addEvent(ev1, true);

        // Added event 2 GMT: Sun, 07 Jun 2015 19:10:51 GMT
        Event ev2 = new Event(Color.GREEN, 1433704251000L);
        compactCalendarView.addEvent(ev2, true);

        // Query for events on Sun, 07 Jun 2015 GMT.
        // Time is not relevant when querying for events, since events are returned by day.
        // So you can pass in any arbitary DateTime and you will receive all events for that day.
        List<Event> events = compactCalendarView.getEvents(1433701251000L); // can also take a Date object

        // events has size 2 with the 2 events inserted previously
        Log.d(LOG_TAG, "Events: " + events);

        // define a listener to receive callbacks when certain events happen.
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                Log.w(LOG_TAG, "Day was clicked: " + dateClicked + " with events " + events);

                if (dateClicked.getTime() < Constants.dateMilli) {

                    addVehiclesHistoryFragment();
                } else {

                    addVehiclesFragment();
                }
                day = DateHelper.getFormattedDayString(dateClicked.getTime());
                actionBarSubTitle(day);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Log.w(LOG_TAG, "Month was scrolled to: " + firstDayOfNewMonth);
                String month = DateHelper.getFormattedMonthString(firstDayOfNewMonth.getTime());
                Log.w(LOG_TAG, "Month scrolled to: " + month);
                actionBarSubTitle(month);
            }
        });
    }

    private void actionBarTitle(int id) {

        setSupportActionBar(Constants.toolbar);
        assert getSupportActionBar() != null;

        getSupportActionBar().setTitle(id);
    }

    private void actionBarSubTitle(String time) {
        setSupportActionBar(Constants.toolbar);
        assert getSupportActionBar() != null;

        getSupportActionBar().setSubtitle(time);
    }

    private void addVehiclesHistoryFragment() {
//        Log.w(LOG_TAG, "addVehiclesHistoryFragment " + calMilliTime + ": " + dayNext);
//        Bundle args = new Bundle();
//        args.putLong(Constants.CALENDAR_DAY, calMilliTime);
//        args.putLong(Constants.NEXT_DAY, dayNext);

        VehiclesHistory vehiclesHistory = new VehiclesHistory();
//        vehiclesHistory.setArguments(args);
//        Log.w(LOG_TAG, "addVehiclesHistoryFragment " + args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.vehicle_fragment_container, vehiclesHistory)
                .commit();
        int id = R.string.vehicles_history_title_label;
        actionBarTitle(id);
    }

    public void addVehiclesFragment() {
        int id = R.string.vehicles_title_label;
        day = DateHelper.getFormattedDayString(Constants.dateMilli);

        Bundle args = new Bundle();
        args.putString(Constants.CURRENT_DATE, date);
        args.putString(Constants.CURRENT_DAY, day);

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
        actionBarTitle(id);
        actionBarSubTitle(day);
    }

    @Override
    public void onVehicleFragmentInteraction(Uri contentUri) {
        Log.w(LOG_TAG, "onVehicleFragmentInteraction " + contentUri);

        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(Constants.DETAIL_URI, getIntent().getData());

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

    @Override
    public void onOptionItemClicked(String tag) {
        startActivity(new Intent(getApplicationContext(), AddActivity.class)
                .putExtra(Constants.LISTENER_TAG, tag));
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

    @Override
    public void onAddUserFragmentInteraction(User user) {
        Log.w(LOG_TAG, "Add User " + user.getPhoneNo());

        userLocalStore = new LocalStore(getApplicationContext());
        AUTH_TOKEN = userLocalStore.getToken();
        bus.post(new UserServices.UserServerRequest(AUTH_TOKEN, user));
    }
}
