package com.harun.offloadmanager.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.harun.offloadmanager.R;
import com.harun.offloadmanager.fragments.DialogInputFragment;
import com.harun.offloadmanager.fragments.ExpenseFragment;
import com.harun.offloadmanager.fragments.IncomeFragment;
import com.harun.offloadmanager.fragments.TransactionsFragment;
import com.harun.offloadmanager.tasks.ServerRequest;

public class TransactionsActivity extends AppCompatActivity implements IncomeFragment.OnSendCollectionListener,
        ExpenseFragment.OnSendExpenseListener, DialogInputFragment.OnSendDescriptionListener{
    private static final String LOG_TAG = TransactionsActivity.class.getSimpleName();
    public static final String VEHICLE_REG = "vehicle_reg";

    String vehicleReg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        getDataFromDetailsActivity();

        setToolBar();

        addTransactionFragment(vehicleReg);
    }

    private void getDataFromDetailsActivity() {
         vehicleReg = getIntent().getStringExtra(VEHICLE_REG);
    }

    private void setToolBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.transactions_activity_toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        @SuppressLint("PrivateResource") final Drawable upArrow = ContextCompat.getDrawable(getBaseContext(), R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setTitle(vehicleReg);

    }

    private void addTransactionFragment(String vehicleReg) {
        Bundle args = new Bundle();
        args.putString(TransactionsActivity.VEHICLE_REG, vehicleReg);

        TransactionsFragment transactionsFragment = new TransactionsFragment();
        transactionsFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_transactions, transactionsFragment)
                .commit();
    }

    private void startDetailActivity(Uri vehicleReg) {

        Log.w(LOG_TAG, "startDetailActivity " + vehicleReg);
        startActivity(new Intent(getApplicationContext(), DetailsActivity.class)
                .setData(vehicleReg));
    }

    @Override
    public void onCollectionButtonClicked(String reg, String method, int collection, int type, String description) {
        Log.w(LOG_TAG, "onCollectionButtonClicked: " + method);
        String stringCollection = String.valueOf(collection);
        String stringType = String.valueOf(type);
        String dateTime = String.valueOf(System.currentTimeMillis());

        ServerRequest postToServerTask = new ServerRequest(this);
        postToServerTask.execute(method, reg, stringCollection, stringType, description, dateTime);

//        startDetailActivity(OffloadContract.VehicleEntry.buildVehicleRegistration(reg));
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    public void onExpenseButtonClicked(String mVehicleReg, String method, int expense, int type) {

        DialogInputFragment dialogInputFragment = DialogInputFragment.newInstance(vehicleReg, expense, type);
        dialogInputFragment.show(getSupportFragmentManager(), "DialogInputFragment");
    }

    @Override
    public void onPositiveButtonClicked(String vehicleReg, int type, int expense, String description) {
        String method = "add_transaction";
        String stringExpense = String.valueOf(expense);
        String stringType = String.valueOf(type);
//        String description = "This is an Expense";
        String dateTime = String.valueOf(System.currentTimeMillis());

        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.execute(method, vehicleReg, stringExpense, stringType, description, dateTime);
        Log.w(LOG_TAG, "create button clicked " + expense + ": " + description);

//        startDetailActivity(OffloadContract.VehicleEntry.buildVehicleRegistration(vehicleReg));
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }



//    @Override
//    public void respond(String data) {
//
//        SmartTransactionFragmentStatePagerAdapter pagerAdapter;
//        pagerAdapter = new SmartTransactionFragmentStatePagerAdapter(getSupportFragmentManager(), getApplicationContext());
//
////        AddCollectionFragment addCollectionFragment = (AddCollectionFragment) getSupportFragmentManager().findFragmentById(R.id.addCollectionFragment);
////        addCollectionFragment.changeText(data);
//        ((AddCollectionFragment)pagerAdapter.getItem(0)).changeText(data);
//        Log.w(LOG_TAG, "respond");
//    }
}
