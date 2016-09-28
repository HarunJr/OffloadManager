package com.harun.offloadmanager.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.harun.offloadmanager.Constants;
import com.harun.offloadmanager.R;
import com.harun.offloadmanager.data.OffloadContract;
import com.harun.offloadmanager.fragments.DetailsFragment;
import com.harun.offloadmanager.fragments.TransactionsFragment;

public class DetailsActivity extends AppCompatActivity{
    public static final String LOG_TAG = DetailsActivity.class.getSimpleName();
    //From mainActivity
    Uri mUri;
    String vehicleReg;
    Bundle args = new Bundle();

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Log.w(LOG_TAG, "onCreate " + getIntent().getData());

        getDataFromMainActivity();

        setToolBar();

        addDetailsFragment(mUri);

        fab = (FloatingActionButton) findViewById(R.id.fabButton);

        fab.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {

                                       Log.w(LOG_TAG, " OnFabPressed " + vehicleReg);
                                       startTransactionsActivity(vehicleReg);
                                   }
                               }
        );
    }

    private void getDataFromMainActivity() {
        //get vehicle value from uri path
//        String[] path = getIntent().getData().getPath().split("/");
//        vehicleReg = path[path.length - 3];

        args.putParcelable(DetailsFragment.DETAIL_URI, getIntent().getData());

        Log.w(LOG_TAG, "getDataFromMainActivity "+ args);
    }

    private void setToolBar() {
        if (args != null) {
            mUri = args.getParcelable(DetailsFragment.DETAIL_URI);
            vehicleReg = OffloadContract.VehicleEntry.getVehicleRegistrationFromUri(mUri);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_activity_toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(vehicleReg);

        //TODO: work on UP button next to title
    }

    private void addDetailsFragment(Uri uri) {
        //TODO: Learn on use of 'putParcelable' for URI (Sunshine detail activity)
        Log.w(LOG_TAG, "addDetailsFragment " + uri);

        DetailsFragment detailFragment = new DetailsFragment();
        detailFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.vehicle_detail_container, detailFragment)
                .commit();
    }


    private void startTransactionsActivity(String vehicleReg) {
        startActivity(new Intent(getApplicationContext(), TransactionsActivity.class)
                .putExtra(TransactionsActivity.VEHICLE_REG, vehicleReg));
    }

    private void replaceWithTransactionFragment(String vehicleReg) {
        args.putString(Constants.VEHICLE_REG, vehicleReg);
        Log.w(LOG_TAG, "replaceWithTransactionFragment " + vehicleReg);

        TransactionsFragment transactionFragment = new TransactionsFragment();
        transactionFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.vehicle_detail_container, transactionFragment)
                .commit();
    }

    //    private void replaceWithDetailFragment(int vehicleId, String vehicleReg) {
//        args.putInt(Constants.VEHICLE_ID, vehicleId);
//        args.putString(Constants.VEHICLE_REG, vehicleReg);
//
//        Log.w(LOG_TAG, "replaceWithDetailFragment " + vehicleId);
//        DetailFragment detailFragment = new DetailFragment();
//        detailFragment.setArguments(args);
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.vehicle_detail_container, detailFragment);
//        fragmentTransaction.commit();
//    }
//
//    private void replaceWithEditVehicle(int vehicleId, String vehicleReg) {
//        args.putInt(Constants.VEHICLE_ID, vehicleId);
//        args.putString(Constants.VEHICLE_REG, vehicleReg);
//
//        Log.w(LOG_TAG, "replaceWithEditVehicle " + vehicleId);
//        EditVehicle editVehicle = new EditVehicle();
//        editVehicle.setArguments(args);
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.vehicle_detail_container, editVehicle);
//        fragmentTransaction.commit();
//    }


//
//    public void showDialogFragment() {
//
//    }
//
//    @Override
//    public void onEditVehicle(int vehicleId, String vehicleReg) {
//        replaceWithEditVehicle(vehicleId, vehicleReg);
//        Log.w(LOG_TAG, "onEditVehicle: " + vehicleId + ", " + vehicleId);
//    }
}
