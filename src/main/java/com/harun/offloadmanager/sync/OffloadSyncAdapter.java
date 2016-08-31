package com.harun.offloadmanager.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.harun.offloadmanager.R;
import com.harun.offloadmanager.data.OffloadDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.harun.offloadmanager.data.OffloadContract.VehicleEntry;

public class OffloadSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = OffloadSyncAdapter.class.getSimpleName();
    private OffloadDbHelper dbHelper;
    private SQLiteDatabase db;

    public OffloadSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        dbHelper = new OffloadDbHelper(context);
        Log.w(LOG_TAG, "OffloaderSyncAdapter Called.");
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync Called.");

        String JSON_STRING;
        String vehicleJsonString = null;
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        try {
            //TODO: setup condition for switching between genymotion and device
//            final String json_url = "http://192.168.56.1/offloadmanager/get_vehicles.php";
            final String json_url = "http://192.168.56.1/offloadmanager/get_data.php";
            URL url = new URL(json_url);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            Log.d(LOG_TAG, "json_url Called."+ inputStream);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringBuilder = new StringBuilder();
            while ((JSON_STRING = bufferedReader.readLine()) != null) {

                stringBuilder.append(JSON_STRING).append("\n");
            }

            inputStream.close();

            vehicleJsonString = stringBuilder.toString().trim();
            Log.w(LOG_TAG, "JSON String: " + vehicleJsonString);
            getVehicleDataFromJson(vehicleJsonString);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    private void getVehicleDataFromJson(String vehicleJsonString) throws JSONException {

        //Vehicle information. Each vehicle's detail is an element of "list" array.
        final String VEHICLE_LIST = "vehicle_list";

        //Vehicle details referenced from JSON
        final String VEHICLE_REG = "vehicle_reg";
        final String VEHICLE_REG_DATE = "sign_up_date";
        final String VEHICLE_TOTAL_DAY_COLLECTION = "v_day_total_collection";
        final String VEHICLE_TOTAL_DAY_EXPENSE = "v_day_total_expense";
        final String VEHICLE_LAST_TRANSACTION_DATE = "last_transaction_date";


        try {
            JSONObject vehicleJson = new JSONObject(vehicleJsonString);
            JSONArray vehicleArray = vehicleJson.getJSONArray(VEHICLE_LIST);

            for (int i = 0; i < vehicleArray.length(); i++) {
                String vehicleReg, regDate, vehicleCollection, vehicleExpense, vehicleLastTransaction;

                JSONObject vehicleObject = vehicleArray.getJSONObject(i);

                vehicleReg = vehicleObject.getString(VEHICLE_REG);
                regDate = vehicleObject.getString(VEHICLE_REG_DATE);
                vehicleCollection = vehicleObject.getString(VEHICLE_TOTAL_DAY_COLLECTION);
                vehicleExpense = vehicleObject.getString(VEHICLE_TOTAL_DAY_EXPENSE);
                vehicleLastTransaction = vehicleObject.getString(VEHICLE_LAST_TRANSACTION_DATE);

                Log.w(LOG_TAG, "From db: " + vehicleReg + ", " + vehicleCollection+ ", " + vehicleExpense+ ", " + vehicleLastTransaction+ ", " + regDate);

                addToVehiclesSQLitedb( vehicleReg, regDate, vehicleCollection, vehicleExpense, vehicleLastTransaction);

            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    private void addToVehiclesSQLitedb(String vehicleReg, String regDate, String vehicleCollection, String vehicleExpense, String vehicleLastTransaction) {

        open();
        ContentValues vehicleValues = new ContentValues();

        vehicleValues.put(VehicleEntry.COLUMN_VEHICLE_REGISTRATION, vehicleReg);
        vehicleValues.put(VehicleEntry.COLUMN_VEHICLE_REGISTRATION_DATE, regDate);
        vehicleValues.put(VehicleEntry.COLUMN_VEHICLE_TOTAL_COLLECTION, vehicleCollection);
        vehicleValues.put(VehicleEntry.COLUMN_VEHICLE_TOTAL_EXPENSE, vehicleExpense);
        vehicleValues.put(VehicleEntry.COLUMN_LAST_TRANSACTION_DATE_TIME, vehicleLastTransaction);

        //USE THIS for normal entries
//        long vehicleRowId=db.insert(VehicleContract.VehicleEntry.TABLE_NAME,null, vehicleValues);

        //Use this for insert with conflict replace.
//        long vehicleRowId = db.insertWithOnConflict(VehicleContract.VehicleEntry.TABLE_NAME, null, vehicleValues, SQLiteDatabase.CONFLICT_REPLACE);

        Uri vehicleUri = getContext().getContentResolver().insert(VehicleEntry.CONTENT_URI, vehicleValues);

        long vehicleRowId = ContentUris.parseId(vehicleUri);

        if (vehicleRowId > 0) {
            Log.w(LOG_TAG, "Inserted into SQLitedb: " + vehicleReg + ", " + regDate);

        } else {
            Log.w(LOG_TAG, ">>>>ERROR Inserting into SQLitedb: ");

        }

        close();

    }

    public void close() {
        dbHelper.close();
    }


    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);

        //TODO: Scheduled Synchronisation resource from sunshine
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

        }
        return newAccount;
    }
}