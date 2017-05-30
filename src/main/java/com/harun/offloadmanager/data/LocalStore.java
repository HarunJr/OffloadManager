package com.harun.offloadmanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.harun.offloadmanager.Constants;
import com.harun.offloadmanager.DateHelper;
import com.harun.offloadmanager.fragments.DetailsFragment;
import com.harun.offloadmanager.models.Transaction;
import com.harun.offloadmanager.models.User;
import com.harun.offloadmanager.models.Vehicle;

import java.util.ArrayList;

/**
 * Created by HARUN on 10/12/2016.
 */

public class LocalStore {
    public static final String LOG_TAG = LocalStore.class.getSimpleName();
    private static final String FCM_PREF = "com.harun.offloadmanager.fcm_pref";
    private static final String FCM_TOKEN = "com.harun.offloadmanager.fcm_token";

    private Context mContext;
    private SharedPreferences userLocal_SP;
    private OffloadDbHelper dbHelper;

    SQLiteDatabase db;
    private Transaction transaction = null;
    private Vehicle vehicle = null;

    public LocalStore(Context context) {
        this.mContext = context;
        userLocal_SP = context.getSharedPreferences(FCM_PREF, Context.MODE_PRIVATE);
        dbHelper = new OffloadDbHelper(mContext);
    }

    public void storeToken(String token) {
        Log.d(LOG_TAG, "storeToken: " + token);
        SharedPreferences.Editor spEditor = userLocal_SP.edit();
        spEditor.putString(FCM_TOKEN, token);
        spEditor.apply();
    }

    public String getToken() {
        return userLocal_SP.getString(FCM_TOKEN, "");
    }

    //User Data access
    public void storeUserData(User user) {
        SharedPreferences.Editor spEditor = userLocal_SP.edit();
        spEditor.putString("name", user.name);
        spEditor.putString("phoneNo", user.phoneNo);
        spEditor.putString("email", user.email);
        spEditor.putString("pin", user.pin);
        spEditor.putString("company", user.company);
        spEditor.apply();
    }

    public User getStoredUser() {
        String name = userLocal_SP.getString("name", "");
        String phoneNo = userLocal_SP.getString("phoneNo", "");
        String email = userLocal_SP.getString("email", "");
        String pin = userLocal_SP.getString("pin", "");
        Log.w(LOG_TAG, name + ", " + phoneNo + ", " + pin);

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

    public void clearData() {
        SharedPreferences.Editor spEditor = userLocal_SP.edit();
        spEditor.clear();
        spEditor.apply();
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();

    }

    private void close() {
        dbHelper.close();
    }

    //Vehicle Data
    public void storeVehicleData(Vehicle vehicle) {
        open();
        ArrayList<ContentValues> vehicleList = new ArrayList<>();
        ContentValues vehicleValues = new ContentValues();
        Log.w(LOG_TAG, "storeVehicleData: " + vehicle.get_id()
                + ", " + vehicle.getRegistration() + ", " + vehicle.getCollection() + ", " + vehicle.getExpense() + ", " + vehicle.getLastTransactionDate());

        vehicleValues.put(OffloadContract.VehicleEntry.COLUMN_VEHICLE_ID, vehicle.get_id());
        vehicleValues.put(OffloadContract.VehicleEntry.COLUMN_VEHICLE_REGISTRATION, vehicle.getRegistration());
        vehicleValues.put(OffloadContract.VehicleEntry.COLUMN_VEHICLE_TOTAL_COLLECTION, vehicle.getCollection());
        vehicleValues.put(OffloadContract.VehicleEntry.COLUMN_VEHICLE_TOTAL_EXPENSE, vehicle.getExpense());
        vehicleValues.put(OffloadContract.VehicleEntry.COLUMN_LAST_TRANSACTION_DATE_TIME, vehicle.getLastTransactionDate());
        vehicleValues.put(OffloadContract.VehicleEntry.COLUMN_VEHICLE_REGISTRATION_DATE, vehicle.getCreated_at());

        vehicleList.add(vehicleValues);
        if (vehicleList.size() > 0) {
            ContentValues[] cvArray = new ContentValues[vehicleList.size()];
            vehicleList.toArray(cvArray);

            //TODO: bulkInsert
            long vehicleRowId = mContext.getContentResolver().bulkInsert(OffloadContract.VehicleEntry.CONTENT_URI, cvArray);

            if (vehicleRowId > 0) {
                Log.w(LOG_TAG, "vehicleRowId: " + vehicleValues);
            } else {
                Log.w(LOG_TAG, ">>>>ERROR Inserting into SQLitedb: ");
            }
        }
        close();
    }

    public void deleteTransactionData(Transaction transaction) {
        open();

        Log.w(LOG_TAG, "deleteTransactionData " + transaction.vehicleReg + ", " + transaction.getAmount());

        mContext.getContentResolver().delete(OffloadContract.TransactionEntry.CONTENT_URI,
                OffloadContract.TransactionEntry.TIMESTAMP + " = " + transaction.getTimestamp(), null);

        close();
    }

    public void updateTransactionData(Transaction transaction) {
        open();

        Log.w(LOG_TAG, "updateTransactionData " + transaction.vehicleReg + ", " + transaction.getAmount());

        ContentValues transactionValues = new ContentValues();
        transactionValues.put(OffloadContract.TransactionEntry.COLUMN_VEHICLE_KEY, transaction.vehicleReg);
        transactionValues.put(OffloadContract.TransactionEntry.COLUMN_AMOUNT, transaction.getAmount());
        transactionValues.put(OffloadContract.TransactionEntry.COLUMN_TYPE, transaction.getType());
        transactionValues.put(OffloadContract.TransactionEntry.COLUMN_DESCRIPTION, transaction.getDescription());
        transactionValues.put(OffloadContract.TransactionEntry.TIMESTAMP, transaction.getTimestamp());
        transactionValues.put(OffloadContract.TransactionEntry.COLUMN_SYNC, transaction.sync);

        mContext.getContentResolver().update(OffloadContract.TransactionEntry.CONTENT_URI,
                transactionValues, OffloadContract.TransactionEntry.TIMESTAMP + " = " + transaction.getTimestamp(), null);

        close();
    }

    public long storeTransactionData(Transaction transaction) {
        open();
        ArrayList<ContentValues> transactionList = new ArrayList<ContentValues>();
        ContentValues transactionValues = new ContentValues();

        transactionValues.put(OffloadContract.TransactionEntry.COLUMN_TRANSACTION_ID, transaction.get_id());
        transactionValues.put(OffloadContract.TransactionEntry.COLUMN_VEHICLE_KEY, transaction.getVehicle_id());
        transactionValues.put(OffloadContract.TransactionEntry.COLUMN_AMOUNT, transaction.getAmount());
        transactionValues.put(OffloadContract.TransactionEntry.COLUMN_TYPE, transaction.getType());
        transactionValues.put(OffloadContract.TransactionEntry.COLUMN_DESCRIPTION, transaction.getDescription());
        transactionValues.put(OffloadContract.TransactionEntry.TIMESTAMP, transaction.getTimestamp());
        transactionValues.put(OffloadContract.TransactionEntry.DATE_TIME, transaction.getCreated_at());
        transactionValues.put(OffloadContract.TransactionEntry.COLUMN_SYNC, transaction.sync);

        transactionList.add(transactionValues);
        if (transactionList.size() > 0) {
            ContentValues[] cvArray = new ContentValues[transactionList.size()];
            transactionList.toArray(cvArray);

            long transactionRowId = mContext.getContentResolver().bulkInsert(OffloadContract.TransactionEntry.CONTENT_URI, cvArray);

            if (transactionRowId > 0) {
                Log.w(LOG_TAG, "storeTransactionData " + transaction.getVehicle_id()
                        + ",\n amount: " + transaction.getAmount()+ ", type: " + transaction.getType() + ", timestamp: " + transaction.getTimestamp()
                        + ",\n amount: " + transaction.getCreated_at());
                return transactionRowId;
            } else {
                Log.w(LOG_TAG, ">>>>ERROR Inserting into SQLitedb: ");
            }

            close();
            return 0;
        }

        //TODO: Update if record exists
//        if (recordExists(transaction)){
//            updateTransactionData(transaction);
//        }else {
//            if (transactionList.size() > 0) {
//                ContentValues[] cvArray = new ContentValues[transactionList.size()];
//                transactionList.toArray(cvArray);
//
//                long transactionRowId = mContext.getContentResolver().bulkInsert(OffloadContract.TransactionEntry.CONTENT_URI, cvArray);
//
//                if (transactionRowId > 0) {
//                    Log.w(LOG_TAG, "storeTransactionData " + transaction.vehicleReg + ", " + transaction.getAmount());
//                    close();
//                    return transactionRowId;
////                    String method = "add_transaction";
////                    ServerRequest postToServerTask = new ServerRequest(mContext);
////                    postToServerTask.execute(method, transaction.vehicleReg, transaction.getAmount()
////                            , transaction.type, transaction.description, transaction.dateTime);
//
//                } else {
//                    Log.w(LOG_TAG, ">>>>ERROR Inserting into SQLitedb: ");
//                    close();
//                    return 0;
//                }
//            }
//        }
        return 0;
    }

    public Transaction getTransactionsNotSynced() {
        open();
        String notSyncedData = "SELECT * FROM " + OffloadContract.TransactionEntry.TABLE_NAME
                + " WHERE " + OffloadContract.TransactionEntry.COLUMN_SYNC + " = " + 1; //columns does not exist at table
        Cursor cursor = db.rawQuery(notSyncedData, null);
        Log.w(LOG_TAG, "cursor: " + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                int VehicleKey = cursor.getInt(DetailsFragment.COL_VEHICLE_KEY);
                double amount = cursor.getDouble(DetailsFragment.COL_AMOUNT);
                String type = cursor.getString(DetailsFragment.COL_TYPE);
                String description = cursor.getString(DetailsFragment.COL_DESCRIPTION);
                long dateTime = cursor.getLong(DetailsFragment.COL_DATE_TIME);

                transaction = new Transaction(VehicleKey, amount, type, description, dateTime);

                Log.w(LOG_TAG, "getTransactionsNotSynced: " + VehicleKey + ">>" + amount + ">>" + description);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        Log.w(LOG_TAG, "getTransactionsNotSynced: " + transaction);

        return transaction;
    }

    public Vehicle getVehicleTransactionTime(Vehicle vehicle) {
        Log.w(LOG_TAG, "getVehicleTransactionTime "+ vehicle.getUpdated_at());
        open();

        Uri transactionWithId = OffloadContract.TransactionEntry.buildKeyTransactionWithDateUri(vehicle.get_id(), "", "");
        String[] projection = new String[]{OffloadContract.VehicleEntry.COLUMN_VEHICLE_REGISTRATION,
                OffloadContract.TransactionEntry.COLUMN_VEHICLE_KEY,
                OffloadContract.TransactionEntry.COLUMN_AMOUNT,
                OffloadContract.TransactionEntry.COLUMN_TYPE,
                OffloadContract.TransactionEntry.TIMESTAMP};
        Cursor cursor = mContext.getContentResolver().query(transactionWithId, projection, null, null, null);
        //declare variables before loop
        double collection = 0;
        double expense = 0;

        assert cursor != null;
        if (cursor.getCount() > 0) {
            Log.w(LOG_TAG, "cursor: " + cursor.getCount() + "\ntransactionWithId: " + transactionWithId);
            cursor.moveToFirst();
            do {
                String registration = cursor.getString(cursor.getColumnIndex(OffloadContract.VehicleEntry.COLUMN_VEHICLE_REGISTRATION));
                int vehicleKey = cursor.getInt(cursor.getColumnIndex(OffloadContract.TransactionEntry.COLUMN_VEHICLE_KEY));
                double amount = cursor.getDouble(cursor.getColumnIndex(OffloadContract.TransactionEntry.COLUMN_AMOUNT));
                String type = cursor.getString(cursor.getColumnIndex(OffloadContract.TransactionEntry.COLUMN_TYPE));
                long dateTime = cursor.getLong(cursor.getColumnIndex(OffloadContract.TransactionEntry.TIMESTAMP));
                Log.w(LOG_TAG, "getVehicleTransactionTimeCollection: " + vehicleKey + ">>" + registration + ">>" + collection + ">>" + type + ">>" + dateTime);

                switch (type){
                    case "vehicle_collection":
                        collection +=  amount;
                        vehicle = new Vehicle(vehicleKey, registration, collection, expense, dateTime);
                        Log.w(LOG_TAG, "getVehicleCollection: " +vehicleKey+">>"+ registration + ">>" + collection + ">>" + type + ">>" + dateTime);
                        break;
                    case "vehicle_expense":
                        expense += amount;
                        vehicle = new Vehicle(vehicleKey, registration, collection, expense, dateTime);
                        Log.w(LOG_TAG, "getVehicleExpense: " +vehicleKey+">>"+ registration + ">>" + expense + ">>" + type + ">>" + dateTime);
                        break;
                    default:
                        return null;
                }
            } while (cursor.moveToNext());

//            Log.w(LOG_TAG, "getVehicleTransaction: "+ vehicle.get_id()+">>"+vehicle.getRegistration()+">>"+vehicle.getCollection()+">>"+vehicle.getExpense());
            return vehicle;
        } else {
            Log.w(LOG_TAG, "cursor is null: " + cursor.getCount() + " \ntransactionWithId: " + transactionWithId);
        }
        cursor.close();
        close();
        return vehicle;
    }

    public Vehicle getVehicleDataFromTransactions(int id, String todayDate) {
        Log.w(LOG_TAG, "getVehicleDataFromTransactions " + todayDate);
        open();
        String startDate = null;
        String endDate = null;

        //24 hours in a day
        //minutes in a day = hrs * 60
        //seconds in a day = minutes * 60
        //milliseconds in a day = seconds * 1000
        long dateTimeMillis = Constants.dateMilli;
        long dayInMilli = (24 * 60 * 60) * 1000;
        long yesterday = dateTimeMillis - dayInMilli;
        long tomorrow = dateTimeMillis + dayInMilli;

        //TODO: Run app at 3:01 am;
        final String yesterdayStartDate = DateHelper.getFormattedDateHyphenString(yesterday) + " 03:00:00";
        final String todayStartDate = todayDate + " 03:00:00";
        final String tomorrowEndDate = DateHelper.getFormattedDateHyphenString(tomorrow) + " 03:00:00";

        long todayStartDateMilli = DateHelper.getMilliDateTimeFromString(todayStartDate);
        long todayDateMilli = DateHelper.getMilliDateTimeFromString(todayDate + " 00:00:00");
        long tomorrowEndDateMilli = DateHelper.getMilliDateTimeFromString(tomorrowEndDate);

        if (dateTimeMillis > todayDateMilli && dateTimeMillis < todayStartDateMilli) {
            startDate = yesterdayStartDate;
            endDate = todayStartDate;
        } else if (dateTimeMillis > todayStartDateMilli && dateTimeMillis < tomorrowEndDateMilli) {
            startDate = todayStartDate;
            endDate = tomorrowEndDate;
        }
        Uri transactionWithId = OffloadContract.TransactionEntry.buildKeyTransactionWithDateUri(id, startDate, endDate);
        String[] projection = new String[]{OffloadContract.VehicleEntry.COLUMN_VEHICLE_REGISTRATION,
                OffloadContract.TransactionEntry.COLUMN_VEHICLE_KEY,
                OffloadContract.TransactionEntry.COLUMN_AMOUNT,
                OffloadContract.TransactionEntry.COLUMN_TYPE,
                OffloadContract.TransactionEntry.TIMESTAMP};
                String sortOrder = OffloadContract.VehicleEntry.COLUMN_LAST_TRANSACTION_DATE_TIME + " DESC";

        Cursor cursor = mContext.getContentResolver().query(transactionWithId, projection, null, null, sortOrder);
        //declare variables before loop
        double collection = 0;
        double expense = 0;
        long dateTime;

        assert cursor != null;
        if (cursor.getCount() > 0) {
            Log.w(LOG_TAG, "cursor: " + cursor.getCount() + "\ntransactionWithId: " + transactionWithId);
            cursor.moveToFirst();
            do {
                String registration = cursor.getString(cursor.getColumnIndex(OffloadContract.VehicleEntry.COLUMN_VEHICLE_REGISTRATION));
                int vehicleKey = cursor.getInt(cursor.getColumnIndex(OffloadContract.TransactionEntry.COLUMN_VEHICLE_KEY));
                double amount = cursor.getDouble(cursor.getColumnIndex(OffloadContract.TransactionEntry.COLUMN_AMOUNT));
                String type = cursor.getString(cursor.getColumnIndex(OffloadContract.TransactionEntry.COLUMN_TYPE));
                dateTime = cursor.getLong(cursor.getColumnIndex(OffloadContract.TransactionEntry.TIMESTAMP));

                switch (type) {
                    case "vehicle_collection":
                        collection += amount;
                        vehicle = new Vehicle(vehicleKey, registration, collection, expense, dateTime);

                        Log.w(LOG_TAG, "getVehicleCollection: " + vehicleKey + ">>" + registration + ">>" + collection + ">>" + expense + ">>" + type + ">>" + dateTime);
                        break;
                    case "vehicle_expense":
                        expense += amount;
                        vehicle = new Vehicle(vehicleKey, registration, collection, expense, dateTime);
                        Log.w(LOG_TAG, "getVehicleExpense: " + vehicleKey + ">>" + registration + ">>" + collection + ">>" + expense + ">>" + type + ">>" + dateTime);
                        break;
                    default:
                        return null;
                }
            } while (cursor.moveToNext());

            Log.w(LOG_TAG, "getVehicleTransaction: " + vehicle.get_id() + ">>" + vehicle.getRegistration() + ">>" + vehicle.getCollection() + ">>" + vehicle.getExpense()+ ">>" + vehicle.getLastTransactionDate());
            return vehicle;
        } else {
            Log.w(LOG_TAG, "cursor is null: " + cursor.getCount() + " \ntransactionWithId: " + transactionWithId);
        }
        cursor.close();
        close();
        return vehicle;
    }

//    public Vehicle getVehicleTransaction(int id, String registration, String type) {
//        open();
//        //24 hours in a day
//        //minutes in a day = hrs * 60
//        //seconds in a day = minutes * 60
//        //milliseconds in a day = seconds * 1000
//        long dateTimeMillis = System.currentTimeMillis();
//        long dayInMilli = (24 * 60 * 60) * 1000;
//        long dayBefore3am = dateTimeMillis - dayInMilli;
//        long tomorrow = dateTimeMillis + dayInMilli;
//        final String yesterdayStartDate = DateHelper.getFormattedDateHyphenString(dayBefore3am) + " 03:00:00";
//        final String startDate = DateHelper.getFormattedDateHyphenString(dateTimeMillis) + " 03:00:00";
//        final String endDate = DateHelper.getFormattedDateHyphenString(tomorrow) + " 03:00:00";
//        double collection = 0;
//        double expense = 0;
//        String description;
//        int vehicleKey = 0;
//        String dateTime;
//        String[] projection = new String[0];
//        Cursor cursor = null;
//        Uri transactionWithId;
//        Log.w(LOG_TAG, "getVehicleTransaction " + type);
//
//        Log.w(LOG_TAG, "vehicle_collection type: " + type);
//        projection = new String[]{OffloadContract.TransactionEntry.COLUMN_VEHICLE_KEY,
//                "SUM(" + OffloadContract.TransactionEntry.COLUMN_AMOUNT + ") AS COLLECTION",
//                OffloadContract.TransactionEntry.TIMESTAMP};
//
//        transactionWithId = OffloadContract.TransactionEntry.buildKeyTransactionWithDateUri(id, startDate, endDate, type);
//        cursor = mContext.getContentResolver().query(transactionWithId, projection, null, null, null);
//
//        assert cursor != null;
//        if (cursor.getCount() > 0) {
////            Log.w(LOG_TAG, "cursor: "+ cursor.getCount() + " \ntransactionWithId: "+ transactionWithId);
//            cursor.moveToFirst();
//            vehicleKey = cursor.getInt(cursor.getColumnIndex(OffloadContract.TransactionEntry.COLUMN_VEHICLE_KEY));
//            collection = cursor.getDouble(cursor.getColumnIndex("COLLECTION"));
//            dateTime = cursor.getString(cursor.getColumnIndex(OffloadContract.TransactionEntry.TIMESTAMP));
//
//            vehicle = new Vehicle(vehicleKey, registration, collection, expense, dateTime);
//            Log.w(LOG_TAG, "getVehicleCollection: " + vehicleKey + ">>" + collection + ">>" + dateTime);
////            Log.w(LOG_TAG, "getVehicleTransaction: "+ vehicle.get_id()+">>"+vehicle.getRegistration()+">>"+vehicle.getCollection()+">>"+vehicle.getExpense());
//            return vehicle;
//        }
//            cursor.close();
//            close();
//
//
////        switch (type) {
////            case "vehicle_collection":
////                Log.w(LOG_TAG, "vehicle_collection type: " + type);
////                projection = new String[]{OffloadContract.TransactionEntry.COLUMN_VEHICLE_KEY,
////                        "SUM(" + OffloadContract.TransactionEntry.COLUMN_AMOUNT + ") AS COLLECTION",
////                        OffloadContract.TransactionEntry.TIMESTAMP};
////
////                transactionWithId = OffloadContract.TransactionEntry.buildKeyTransactionWithDateUri(id, startDate, endDate, type);
////                cursor = mContext.getContentResolver().query(transactionWithId, projection, null, null, null);
////
////                assert cursor != null;
////                if (cursor.getCount() > 0) {
//////            Log.w(LOG_TAG, "cursor: "+ cursor.getCount() + " \ntransactionWithId: "+ transactionWithId);
////                    cursor.moveToFirst();
////                    vehicleKey = cursor.getInt(cursor.getColumnIndex(OffloadContract.TransactionEntry.COLUMN_VEHICLE_KEY));
////                    collection = cursor.getDouble(cursor.getColumnIndex("COLLECTION"));
////                    dateTime = cursor.getString(cursor.getColumnIndex(OffloadContract.TransactionEntry.TIMESTAMP));
////
////                    vehicle = new Vehicle(vehicleKey, registration, collection, expense, dateTime);
////                    Log.w(LOG_TAG, "getVehicleCollection: " + vehicleKey + ">>" + collection + ">>" + dateTime);
//////            Log.w(LOG_TAG, "getVehicleTransaction: "+ vehicle.get_id()+">>"+vehicle.getRegistration()+">>"+vehicle.getCollection()+">>"+vehicle.getExpense());
////                    return vehicle;
////                } else {
////                    Log.w(LOG_TAG, "null cursor: " + cursor.getCount() + " \ntransactionWithId: " + transactionWithId);
////
////                }
////                cursor.close();
////                close();
////                Log.w(LOG_TAG, "getVehicle: " + transaction);
////                break;
////
////            case "vehicle_expense":
////                Log.w(LOG_TAG, "vehicle_expense type: " + type);
////                projection = new String[]{OffloadContract.TransactionEntry.COLUMN_VEHICLE_KEY,
////                        "SUM(" + OffloadContract.TransactionEntry.COLUMN_AMOUNT + ") AS EXPENSE",
////                        OffloadContract.TransactionEntry.TIMESTAMP};
////
////                transactionWithId = OffloadContract.TransactionEntry.buildKeyTransactionWithDateUri(id, startDate, endDate, type);
////                cursor = mContext.getContentResolver().query(transactionWithId, projection, null, null, null);
////
////                assert cursor != null;
////                if (cursor.getCount() > 0) {
////                    cursor.moveToFirst();
////                    vehicleKey = cursor.getInt(cursor.getColumnIndex(OffloadContract.TransactionEntry.COLUMN_VEHICLE_KEY));
////                    expense = cursor.getDouble(cursor.getColumnIndex("EXPENSE"));
////                    dateTime = cursor.getString(cursor.getColumnIndex(OffloadContract.TransactionEntry.TIMESTAMP));
////
//////                    do {
//////                        vehicleKey = cursor.getInt(cursor.getColumnIndex(OffloadContract.TransactionEntry.COLUMN_VEHICLE_KEY));
//////                        expense = cursor.getDouble(cursor.getColumnIndex("EXPENSE"));
//////                        dateTime = cursor.getString(cursor.getColumnIndex(OffloadContract.TransactionEntry.TIMESTAMP));
////////
//////                    } while (cursor.moveToNext());
////                    vehicle = new Vehicle(vehicleKey, registration, collection, expense, dateTime);
////                    Log.w(LOG_TAG, "getVehicleExpense: " + vehicleKey + ">>" + expense + ">>" + dateTime);
//////            Log.w(LOG_TAG, "getVehicleTransaction: "+ vehicle.get_id()+">>"+vehicle.getRegistration()+">>"+vehicle.getCollection()+">>"+vehicle.getExpense());
////                    return vehicle;
////                } else {
////                    Log.w(LOG_TAG, "null cursor: " + cursor.getCount() + " \ntransactionWithId: " + transactionWithId);
////                }
////                cursor.close();
////                close();
////                Log.w(LOG_TAG, "getVehicle: " + transaction);
////                break;
////
////            default:
////                Log.w(LOG_TAG, "///////////NO TYPE //////////////");
////
////
////        }
//        return null;
//
////        if (transaction.getType().equals("vehicle_collection")) {
////            type = transaction.getType();
////            Log.w(LOG_TAG, "vehicle_collection type: " + type);
////
////            projection = new String[]{OffloadContract.TransactionEntry.COLUMN_VEHICLE_KEY,
////                    "SUM(" + OffloadContract.TransactionEntry.COLUMN_AMOUNT + ") AS COLLECTION",
////                    OffloadContract.TransactionEntry.TIMESTAMP};
////
////            Uri transactionWithId = OffloadContract.TransactionEntry.buildKeyTransactionWithDateUri(id, startDate, endDate, type);
////            cursor = mContext.getContentResolver().query(transactionWithId, projection, null, null, null);
////
////            assert cursor != null;
////            if (cursor.getCount() > 0) {
//////            Log.w(LOG_TAG, "cursor: "+ cursor.getCount() + " \ntransactionWithId: "+ transactionWithId);
////                cursor.moveToFirst();
//////            vehicleKey = cursor.getInt(DetailsFragment.COL_VEHICLE_KEY);
//////            amount = cursor.getDouble(DetailsFragment.COL_AMOUNT);
//////            dateTime = cursor.getString(DetailsFragment.COL_DATE_TIME);
////                do {
//////                int transactionId = cursor.getInt(DetailsFragment.COL_TRANSACTION_ID);
////                    vehicleKey = cursor.getInt(cursor.getColumnIndex(OffloadContract.TransactionEntry.COLUMN_VEHICLE_KEY));
////                    collection = cursor.getDouble(cursor.getColumnIndex("COLLECTION"));
//////                type = cursor.getString(DetailsFragment.COL_TYPE);
//////                description = cursor.getString(DetailsFragment.COL_DESCRIPTION);
////                    dateTime = cursor.getString(cursor.getColumnIndex(OffloadContract.TransactionEntry.TIMESTAMP));
//////
////                } while (cursor.moveToNext());
////                vehicle = new Vehicle(vehicleKey, registration, collection, expense, dateTime);
////                Log.w(LOG_TAG, "getVehicleTransaction: " + vehicleKey + ">>" + collection + ">>" + dateTime);
//////            Log.w(LOG_TAG, "getVehicleTransaction: "+ vehicle.get_id()+">>"+vehicle.getRegistration()+">>"+vehicle.getCollection()+">>"+vehicle.getExpense());
////                return vehicle;
////            } else {
////                Log.w(LOG_TAG, "null cursor: " + cursor.getCount() + " \ntransactionWithId: " + transactionWithId);
////
////            }
////            cursor.close();
////            close();
////            Log.w(LOG_TAG, "getVehicle: " + transaction);
////            return null;
////
////        } else if (transaction.getType().equals("vehicle_expense")) {
////
////            type = transaction.getType();
////            Log.w(LOG_TAG, "vehicle_expense type: " + type);
////
////            projection = new String[]{OffloadContract.TransactionEntry.COLUMN_VEHICLE_KEY,
////                    "SUM(" + OffloadContract.TransactionEntry.COLUMN_AMOUNT + ") AS EXPENSE",
////                    OffloadContract.TransactionEntry.TIMESTAMP};
////
////            Uri transactionWithId = OffloadContract.TransactionEntry.buildKeyTransactionWithDateUri(id, startDate, endDate, type);
////            cursor = mContext.getContentResolver().query(transactionWithId, projection, null, null, null);
////
////            assert cursor != null;
////            if (cursor.getCount() > 0) {
////                cursor.moveToFirst();
////                do {
////                    vehicleKey = cursor.getInt(cursor.getColumnIndex(OffloadContract.TransactionEntry.COLUMN_VEHICLE_KEY));
////                    expense = cursor.getDouble(cursor.getColumnIndex("EXPENSE"));
////                    dateTime = cursor.getString(cursor.getColumnIndex(OffloadContract.TransactionEntry.TIMESTAMP));
//////
////                } while (cursor.moveToNext());
////                vehicle = new Vehicle(vehicleKey, registration, collection, expense, dateTime);
////                Log.w(LOG_TAG, "getVehicleExpense: " + vehicleKey + ">>" + collection + ">>" + dateTime);
//////            Log.w(LOG_TAG, "getVehicleTransaction: "+ vehicle.get_id()+">>"+vehicle.getRegistration()+">>"+vehicle.getCollection()+">>"+vehicle.getExpense());
////                return vehicle;
////            } else {
////                Log.w(LOG_TAG, "null cursor: " + cursor.getCount() + " \ntransactionWithId: " + transactionWithId);
////            }
////            cursor.close();
////            close();
////            Log.w(LOG_TAG, "getVehicle: " + transaction);
////            return null;
////
////
////        }
////        return null;
//    }

    //       Log.w(LOG_TAG, "type: "+ transaction.getType());
    //columns does not exist at table
//        Cursor cursor = mContext.getContentResolver().query(transactionWithId, projection, null, null, null);
//        Log.w(LOG_TAG, "sumData: "+ sumData);
//        assert cursor != null;
//        if (cursor.getCount() > 0){
////            Log.w(LOG_TAG, "cursor: "+ cursor.getCount() + " \ntransactionWithId: "+ transactionWithId);
//            cursor.moveToFirst();
////            vehicleKey = cursor.getInt(DetailsFragment.COL_VEHICLE_KEY);
////            amount = cursor.getDouble(DetailsFragment.COL_AMOUNT);
////            dateTime = cursor.getString(DetailsFragment.COL_DATE_TIME);
//            do{
////                int transactionId = cursor.getInt(DetailsFragment.COL_TRANSACTION_ID);
//                vehicleKey = cursor.getInt(cursor.getColumnIndex(OffloadContract.TransactionEntry.COLUMN_VEHICLE_KEY));
//                collection = cursor.getDouble(cursor.getColumnIndex("COLLECTION"));
////                type = cursor.getString(DetailsFragment.COL_TYPE);
////                description = cursor.getString(DetailsFragment.COL_DESCRIPTION);
//                dateTime = cursor.getString(cursor.getColumnIndex(OffloadContract.TransactionEntry.TIMESTAMP));
////
//            }while (cursor.moveToNext());
//            vehicle = new Vehicle(vehicleKey, registration, collection, expense, dateTime);
//            Log.w(LOG_TAG, "getVehicleTransaction: "+ vehicleKey+">>"+collection+">>"+dateTime);
////            Log.w(LOG_TAG, "getVehicleTransaction: "+ vehicle.get_id()+">>"+vehicle.getRegistration()+">>"+vehicle.getCollection()+">>"+vehicle.getExpense());
//            return vehicle;

//        }else {
//            Log.w(LOG_TAG, "null cursor: "+ cursor.getCount()+ " \ntransactionWithId: "+ transactionWithId);
//
//        }
//        cursor.close();
//        close();
//        Log.w(LOG_TAG, "getVehicle: "+ transaction);
//        return null;
//}


//    public Transaction getVehicleTransaction(String id, long dateTimeMillis) {
//        open();
//        String sumData = "SELECT SUM("+OffloadContract.TransactionEntry.TABLE_NAME+"."+ OffloadContract.TransactionEntry.COLUMN_AMOUNT
//                +") FROM " + OffloadContract.TransactionEntry.TABLE_NAME
//                + " WHERE " + OffloadContract.TransactionEntry.TABLE_NAME+"."+OffloadContract.TransactionEntry.COLUMN_VEHICLE_KEY + " = ? "
//                + " AND strftime('%Y-%m-%d', " + OffloadContract.TransactionEntry.TABLE_NAME + "." + OffloadContract.TransactionEntry.TIMESTAMP
//                + "/1000, 'unixepoch') = strftime('%Y-%m-%d', ?/1000, 'unixepoch') AND "
//                + OffloadContract.TransactionEntry.TABLE_NAME + "." + OffloadContract.TransactionEntry.COLUMN_TYPE +" = ?";
//        //columns does not exist at table
//        Cursor cursor = db.rawQuery(sumData, new String[]{id,String.valueOf(dateTimeMillis),"vehicle_collection"});
////        Log.w(LOG_TAG, "sumData: "+ sumData);
//        Log.w(LOG_TAG, "cursor: "+ cursor.getCount());
//        if (cursor.getCount()>0 && cursor.moveToFirst()){
//            do {
//                int transactionId = cursor.getInt(DetailsFragment.COL_TRANSACTION_ID);
//                int VehicleKey = cursor.getInt(DetailsFragment.COL_VEHICLE_KEY);
//                double amount = cursor.getDouble(DetailsFragment.COL_AMOUNT);
//                String type = cursor.getString(DetailsFragment.COL_TYPE);
//                String description = cursor.getString(DetailsFragment.COL_DESCRIPTION);
//                long dateTime = cursor.getLong(DetailsFragment.COL_DATE_TIME);
//
////                transaction = new Transaction(VehicleKey, amount, type, description, dateTime);
//
//                Log.w(LOG_TAG, "getVehicleTransaction: "+ VehicleKey+">>"+amount+">>"+description);
//            }while (cursor.moveToNext());
//        }
//        cursor.close();
//        close();
//        Log.w(LOG_TAG, "getTransactionsNotSynced: "+ transaction);
//
//        return transaction;
//    }

    public boolean recordExists(Transaction transaction) {
        open();
        String notSyncedData = "SELECT * FROM " + OffloadContract.TransactionEntry.TABLE_NAME
                + " WHERE " + OffloadContract.TransactionEntry.TIMESTAMP + " = " + transaction.getTimestamp(); //columns does not exist at table
        Cursor cursor = db.rawQuery(notSyncedData, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            close();
            Log.w(LOG_TAG, "recordExists: " + transaction.getAmount());
            return true;

        } else {
            cursor.close();
            close();
            return false;
        }
    }
}
