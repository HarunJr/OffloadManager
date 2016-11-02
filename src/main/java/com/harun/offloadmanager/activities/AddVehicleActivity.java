package com.harun.offloadmanager.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.harun.offloadmanager.Constants;
import com.harun.offloadmanager.R;
import com.harun.offloadmanager.models.User;
import com.harun.offloadmanager.data.LocalStore;
import com.harun.offloadmanager.models.Vehicle;
import com.harun.offloadmanager.tasks.ServerRequest;

public class AddVehicleActivity extends AppCompatActivity {
    public static final String LOG_TAG = AddVehicleActivity.class.getSimpleName();

    EditText mRegInput;
    EditText makeInput;
    EditText modelInput;
    EditText YOMInput;
    EditText passCapInput;

    Button mAddVehicleButton;
    LocalStore localStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        Constants.toolbar = (Toolbar) findViewById(R.id.add_vehicle_activity_toolbar);
        setSupportActionBar(Constants.toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.add_vehicle_title);

        localStore = new LocalStore(this);
        declareEditTextViews();

    }

    private void declareEditTextViews(){
        mRegInput = (EditText) findViewById(R.id.vehicle_input);
        makeInput = (EditText) findViewById(R.id.vehicle_make_input);
        modelInput = (EditText) findViewById(R.id.vehicle_model_input);
        YOMInput = (EditText) findViewById(R.id.vehicle_man_year_input);
        passCapInput = (EditText) findViewById(R.id.passenger_cap_input);

        mAddVehicleButton = (Button) findViewById(R.id.create_vehicle_button);


        mAddVehicleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(LOG_TAG, "onClick ");

                if (isNetworkAvailable()){
                    String method = "add_vehicle";
                    String vehicleRegistration = mRegInput.getText().toString();
                    String vehicleMake = makeInput.getText().toString();
                    String vehicleModel = modelInput.getText().toString();
                    String yearOfManufacture = YOMInput.getText().toString();
                    String passengerCapacity = passCapInput.getText().toString();
                    String dateTime = String.valueOf(System.currentTimeMillis());

                    //user data
                    User user = localStore.getLoggedInUser();
                    String user_key = user.phoneNo;

                    Vehicle vehicle = new Vehicle(
                            vehicleRegistration, user_key, vehicleMake, vehicleModel, yearOfManufacture, passengerCapacity, dateTime );

                    Log.w(LOG_TAG, "create button clicked "+vehicleRegistration +": "+user_key);

                    registerVehicle(vehicle);

                    ServerRequest serverRequest = new ServerRequest(AddVehicleActivity.this);
                    serverRequest.execute(
                            method,
                            vehicleRegistration,
                            user_key,
                            vehicleMake,
                            vehicleModel,
                            yearOfManufacture,
                            passengerCapacity,
                            dateTime);

                    startActivity(new Intent(AddVehicleActivity.this, MainActivity.class));

                }else {
                    Toast.makeText(getBaseContext(), R.string.no_internet_message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void registerVehicle(Vehicle vehicle) {
        String registerMethod = "add_vehicle";
        new ServerRequest(this).execute(registerMethod,
                vehicle.registration, vehicle.userKey, vehicle.make, vehicle.model, vehicle.YOM, vehicle.passCap, vehicle.regDate);
    }


    public boolean isNetworkAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
