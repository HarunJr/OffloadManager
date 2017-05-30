package com.harun.offloadmanager.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;

import com.harun.offloadmanager.Constants;
import com.harun.offloadmanager.R;
import com.harun.offloadmanager.data.LocalStore;
import com.harun.offloadmanager.fragments.CollectionFragment;
import com.harun.offloadmanager.fragments.DialogInputFragment;
import com.harun.offloadmanager.fragments.ExpenseFragment;
import com.harun.offloadmanager.fragments.TransactionsFragment;
import com.harun.offloadmanager.models.Transaction;
import com.harun.offloadmanager.service.TransactionServices;

import static com.harun.offloadmanager.activities.SplashScreenActivity.AUTH_TOKEN;

public class TransactionsActivity extends BaseActivity implements CollectionFragment.OnClickCollectionListener,
        ExpenseFragment.OnSendExpenseListener, DialogInputFragment.OnSendDescriptionListener {
    private static final String LOG_TAG = TransactionsActivity.class.getSimpleName();
    public static final String VEHICLE_ID = "_id";
    public static final String VEHICLE_REG = "vehicle_reg";
    private Transaction transaction;

    int vehicleId;
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
        vehicleId = getIntent().getIntExtra(VEHICLE_ID, 0);
        vehicleReg = getIntent().getStringExtra(VEHICLE_REG);
        Log.w(LOG_TAG, "getDataFromDetailsActivity: " +vehicleId);
    }

    private void setToolBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.transactions_activity_toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        @SuppressLint("PrivateResource") final Drawable upArrow = ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_arrow_back_white_24dp);
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

    @Override
    public void onCollectionButtonClicked(String reg, double collection, int type, String description, EditText mCollectionInput) {
        String stringType = String.valueOf(type);
        long dateTime = Constants.dateMilli;
        int sync = 1;//Not Synced
        Log.w(LOG_TAG, "vehicleId: " +vehicleId);

        //TODO: Use dateTime when offline NOT online
        transaction = new Transaction(vehicleId, collection, stringType, description, dateTime, sync);
        Log.w(LOG_TAG, "onCollectionButtonClicked: " +transaction.getVehicle_id()+"-"+ collection);

        LocalStore transactionStore = new LocalStore(this);
        AUTH_TOKEN = transactionStore.getToken();

        if (isNetworkAvailable()){
            bus.post(new TransactionServices.TransactionsServerRequest(AUTH_TOKEN, transaction));

        }else {

            //TODO:GET SUM of transactions for today and add to vehicles table
            transactionStore.storeTransactionData(transaction);
        }
//        startDetailActivity(OffloadContract.VehicleEntry.buildVehicleRegistration(reg));
//        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    public void onExpenseButtonClicked(String mVehicleReg, String method, double expense, int type) {

        DialogInputFragment dialogInputFragment = DialogInputFragment.newInstance(vehicleReg, expense, type);
        dialogInputFragment.show(getSupportFragmentManager(), "DialogInputFragment");
    }

    @Override
    public void onPositiveButtonClicked(String vehicleReg, int type, double expense, String description) {
        String method = "add_transaction";
        String stringExpense = String.valueOf(expense);
        String stringType = String.valueOf(type);
//        String description = "This is an Expense";
        long dateTime = System.currentTimeMillis();
        int sync = 1;//Not Synced

        transaction = new Transaction(vehicleId, expense, stringType, description, dateTime, sync);
        LocalStore transactionStore = new LocalStore(this);
        AUTH_TOKEN = transactionStore.getToken();

        if (isNetworkAvailable()){
            bus.post(new TransactionServices.TransactionsServerRequest(AUTH_TOKEN, transaction));

        }else {

            //TODO:GET SUM of transactions for today and add to vehicles table
            transactionStore.storeTransactionData(transaction);
        }
//        bus.post(new TransactionServices.TransactionsServerRequest(AUTH_TOKEN, transaction));
//        ServerRequest serverRequest = new ServerRequest(this);
//        serverRequest.execute(method, vehicleReg, stringExpense, stringType, description, dateTime);
        Log.w(LOG_TAG, "onExpenseButtonClicked " + expense + ": " + description);

//        startDetailActivity(OffloadContract.VehicleEntry.buildVehicleRegistration(titleKey));
//        startActivity(new Intent(getApplicationContext(), MainActivity.class));
//        bus.post(new TransactionServices.TransactionsServerRequest("transact.php", transactionsModel));
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        Log.w(LOG_TAG, "isNetworkAvailable" + networkInfo);
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
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
