package com.harun.offloadmanager.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.harun.offloadmanager.R;
import com.harun.offloadmanager.fragments.DetailsFragment;

public class DetailsActivity extends AppCompatActivity {
    public static final String LOG_TAG = DetailsActivity.class.getSimpleName();
    public static final String VEHICLE_REG = "vehicle_reg";
    //From mainActivity
    String vehicleReg;
    Bundle args = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Log.w(LOG_TAG, "onCreate " + getIntent().getData());

        getDataFromMainActivity();

        setToolBar();

        addDetailsFragment(vehicleReg);
    }

    private void getDataFromMainActivity() {
        //get vehicle value from uri path
        String[] path = getIntent().getData().getPath().split("/");
        vehicleReg = path[path.length -3];

        args.putParcelable(DetailsFragment.DETAIL_URI, getIntent().getData());

        Log.w(LOG_TAG, "getDataFromMainActivity " + vehicleReg);
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_activity_toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(vehicleReg);

    }

    private void addDetailsFragment(String vehicleReg) {
        //TODO: Learn on use of 'putParcelable' for URI (Sunshine detail activity)
        Log.w(LOG_TAG, "addDetailsFragment " + vehicleReg);

        DetailsFragment detailFragment = new DetailsFragment();
        detailFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
        .add(R.id.vehicle_detail_container, detailFragment)
        .commit();
    }

//    private void replaceWithTransactionFragment(int vehicleId, String vehicleReg) {
//        args.putInt(Constants.VEHICLE_ID, vehicleId);
//        args.putString(Constants.VEHICLE_REG, vehicleReg);
//        Log.w(LOG_TAG, "replaceWithTransactionFragment " + vehicleId + ", " + vehicleReg);
//
//        TransactionFragment transactionFragment = new TransactionFragment();
//        transactionFragment.setArguments(args);
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.replace(R.id.vehicle_detail_container, transactionFragment);
//        fragmentTransaction.commit();
//    }
//
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
//    private void startDetailActivity(int vehicleId, String vehicleReg) {
//
//        Log.w(LOG_TAG, "startDetailActivity " + vehicleId + ", " + vehicleReg);
//        startActivity(new Intent(getApplicationContext(), DetailsActivity.class)
//                .putExtra(Constants.VEHICLE_ID, vehicleId)
//                .putExtra(Constants.VEHICLE_REG, vehicleReg));
//    }
//
//    @Override
//    public void onFabPressed(int vehicleId, String vehicleReg) {
//        replaceWithTransactionFragment(vehicleId, vehicleReg);
//    }
//
//    @Override
//    public void onCollectionButtonClicked(int vehicleId, String vehicleReg, String method, String collection, String stringType, String description, String dateTime) {
//        String stringVehicleId = String.valueOf(vehicleId);
//
//        PostToServerTask postToServerTask = new PostToServerTask(getApplicationContext());
//        postToServerTask.execute(method, stringVehicleId, collection, stringType, description, dateTime);
//
//        startDetailActivity(vehicleId, vehicleReg);
//        Log.w(LOG_TAG, "startDetailActivity " + vehicleId + ", " + vehicleReg);
//    }
//
//    @Override
//    public void onExpenseButtonClicked(int vehicleId, String vehicleReg, int expense, int type) {
//
//        DialogInputFragment dialogInputFragment = DialogInputFragment.newInstance(vehicleId, vehicleReg, expense, type);
//        dialogInputFragment.show(getSupportFragmentManager(), "DialogInputFragment");
//
//    }
//
//    @Override
//    public void onPositiveButtonClicked(int vehicleId, String vehicleReg, int type, int expense, String description) {
//        String method = "transact";
//        String stringVehicleId = String.valueOf(vehicleId);
//        String stringExpense = String.valueOf(expense);
//        String stringType = String.valueOf(type);
////        String description = "This is an Expense";
//        String dateTime = String.valueOf(System.currentTimeMillis());
//
//        PostToServerTask postToServerTask = new PostToServerTask(this);
//        postToServerTask.execute(method, stringVehicleId, stringExpense, stringType, description, dateTime);
//        Log.w(LOG_TAG, "create button clicked " + expense + ": " + description);
//
//        startDetailActivity(vehicleId, vehicleReg);
//    }
//
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
