package com.harun.offloadmanager.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.harun.offloadmanager.Constants;
import com.harun.offloadmanager.R;
import com.harun.offloadmanager.data.LocalStore;
import com.harun.offloadmanager.fragments.AddUserFragment;
import com.harun.offloadmanager.fragments.AddVehicleFragment;
import com.harun.offloadmanager.models.Vehicle;
import com.harun.offloadmanager.service.VehicleServices;

public class AddActivity extends BaseActivity implements AddVehicleFragment.OnAddVehicleFragmentInteractionListener{
    public static final String LOG_TAG = AddActivity.class.getSimpleName();
    public static String AUTH_TOKEN = null;
    LocalStore localStore;
    String listenerTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        Constants.toolbar = (Toolbar) findViewById(R.id.add_vehicle_activity_toolbar);
        setSupportActionBar(Constants.toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.add_vehicle_title);

        localStore = new LocalStore(this);
        listenerTag = getDataFromMainActivity();

        pickFragment(listenerTag);

//        declareEditTextViews();
    }

    private String getDataFromMainActivity() {
        listenerTag = getIntent().getStringExtra(Constants.LISTENER_TAG);
        return listenerTag;
    }

    private void pickFragment(String listenerTag) {
        if (this.listenerTag.equals("add_vehicle")){
            startVehicleInputFragment();
        }else if (listenerTag.equals("add_user")){
            startUserInputFragment();
        }
    }

    private void startVehicleInputFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.add_vehicle_fragment_container, new AddVehicleFragment())
                .commit();
    }
    private void startUserInputFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.add_vehicle_fragment_container, new AddUserFragment())
                .commit();
    }

    @Override
    public void onAddVehicleFragmentInteraction(Vehicle vehicle) {
        //send new vehicle to server api
        AUTH_TOKEN = localStore.getToken();
        bus.post(new VehicleServices.VehiclesServerRequest(AUTH_TOKEN, vehicle));
    }

    public boolean isNetworkAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
