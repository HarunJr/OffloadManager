package com.harun.offloadmanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.harun.offloadmanager.models.Transaction;
import com.harun.offloadmanager.models.User;
import com.harun.offloadmanager.models.Vehicle;

import java.util.ArrayList;

/**
 * Created by HARUN on 10/12/2016.
 */

public class LocalStore {
    public static final String LOG_TAG = LocalStore.class.getSimpleName();
    private static final String SP_NAME = "userDetails";

    private Context mContext;
    private SharedPreferences userLocal_SP;
    private OffloadDbHelper dbHelper;

    public LocalStore(Context context) {
        this.mContext = context;
        userLocal_SP = context.getSharedPreferences(SP_NAME, 0);
        dbHelper = new OffloadDbHelper(mContext);
    }

    //User Data access
    public void storeUserData(User user) {
        SharedPreferences.Editor spEditor = userLocal_SP.edit();
        spEditor.putString("name", user.name);
        spEditor.putString("phoneNo", user.phoneNo);
        spEditor.putString("email", user.email);
        spEditor.putString("pin", user.pin);
        spEditor.apply();
    }

    public User getLoggedInUser() {
        String name = userLocal_SP.getString("name", "");
        String phoneNo = userLocal_SP.getString("phoneNo", "");
        String email = userLocal_SP.getString("email", "");
        String pin = userLocal_SP.getString("pin", "");

        return new User(name, phoneNo, email, pin);
    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor spEditor = userLocal_SP.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.apply();
    }

    public boolean getUserLoggedIn() {
        if (userLocal_SP.getBoolean("loggedIn", false)) {
            return true;
        } else {
            return false;
        }
    }

    public void clearUserData() {
        SharedPreferences.Editor spEditor = userLocal_SP.edit();
        spEditor.clear();
        spEditor.apply();
    }

    public void open() throws SQLException {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

    }

    private void close() {
        dbHelper.close();
    }

    //Vehicle Data
    public void storeVehicleData(Vehicle vehicle) {
        open();
        ArrayList<ContentValues> vehicleList = new ArrayList<>();
        ContentValues vehicleValues = new ContentValues();


        vehicleValues.put(OffloadContract.VehicleEntry.COLUMN_VEHICLE_REGISTRATION, vehicle.registration);
        vehicleValues.put(OffloadContract.VehicleEntry.COLUMN_VEHICLE_TOTAL_COLLECTION, vehicle.collection);
        vehicleValues.put(OffloadContract.VehicleEntry.COLUMN_VEHICLE_TOTAL_EXPENSE, vehicle.expense);
        vehicleValues.put(OffloadContract.VehicleEntry.COLUMN_LAST_TRANSACTION_DATE_TIME, vehicle.transDate);
        vehicleValues.put(OffloadContract.VehicleEntry.COLUMN_VEHICLE_REGISTRATION_DATE, vehicle.regDate);

        vehicleList.add(vehicleValues);

        if (vehicleList.size() > 0) {
            ContentValues[] cvArray = new ContentValues[vehicleList.size()];
            vehicleList.toArray(cvArray);

            //TODO: bulkInsert
            long vehicleRowId = mContext.getContentResolver().bulkInsert(OffloadContract.VehicleEntry.CONTENT_URI, cvArray);

            if (vehicleRowId > 0) {
                Log.w(LOG_TAG, "storeVehicleData: " + vehicle.registration + ", " + vehicle.regDate);

            } else {
                Log.w(LOG_TAG, ">>>>ERROR Inserting into SQLitedb: ");
            }
            close();
        }
    }

    public void storeTransactionData(Transaction transaction) {
        open();
        ArrayList<ContentValues> transactionList = new ArrayList<ContentValues>();
        ContentValues transactionValues = new ContentValues();

        transactionValues.put(OffloadContract.TransactionEntry.COLUMN_TRANSACTION_ID, transaction.id);
        transactionValues.put(OffloadContract.TransactionEntry.COLUMN_VEHICLE_KEY, transaction.vehicleKey);
        transactionValues.put(OffloadContract.TransactionEntry.COLUMN_AMOUNT, transaction.amount);
        transactionValues.put(OffloadContract.TransactionEntry.COLUMN_TYPE, transaction.type);
        transactionValues.put(OffloadContract.TransactionEntry.COLUMN_DESCRIPTION, transaction.description);
        transactionValues.put(OffloadContract.TransactionEntry.COLUMN_DATE_TIME, transaction.dateTime);

        transactionList.add(transactionValues);

        if (transactionList.size() > 0) {
            ContentValues[] cvArray = new ContentValues[transactionList.size()];
            transactionList.toArray(cvArray);

            //TODO: bulkInsert

            long transactionRowId = mContext.getContentResolver().bulkInsert(OffloadContract.TransactionEntry.CONTENT_URI, cvArray);

            if (transactionRowId > 0) {
                Log.w(LOG_TAG, "storeTransactionData " + transaction.vehicleKey + ", " + transaction.amount);

            } else {
                Log.w(LOG_TAG, ">>>>ERROR Inserting into SQLitedb: ");
            }
            close();

            close();
        }
    }
}
