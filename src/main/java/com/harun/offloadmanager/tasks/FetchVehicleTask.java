package com.harun.offloadmanager.tasks;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.harun.offloadmanager.data.OffloadContract;
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

/**
 * Created by HARUN on 4/5/2016.
 */
public class FetchVehicleTask extends AsyncTask<Void, Void, String> {
    public static final String LOG_TAG = FetchVehicleTask.class.getSimpleName();
    private final Context mContext;
    ProgressDialog pd;
    HttpURLConnection httpURLConnection = null;
    BufferedReader bufferedReader = null;
    String json_url;

    private OffloadDbHelper dbHelper;
    private SQLiteDatabase db;

    public FetchVehicleTask(Context context) {
        mContext = context;
        dbHelper = new OffloadDbHelper(mContext);
    }

    @Override
    protected void onPreExecute() {
        json_url = "http://192.168.245.1/offloadmanager/get_vehicles.php";/**specific to genymotion IPV4 address**/
        pd = new ProgressDialog(mContext);
        pd.setTitle("Fetch vehicle data");
        pd.setMessage("Fetching data ... please wait");
        pd.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        String JSON_STRING;
        String vehicleJsonString = null;
        try {
            URL url = new URL(json_url);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringBuilder = new StringBuilder();

            if (bufferedReader != null){
                while ((JSON_STRING = bufferedReader.readLine()) != null) {

                    stringBuilder.append(JSON_STRING).append("\n");
                }
            }else {
                return null;
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
        return null;
    }

    private void getVehicleDataFromJson(String vehicleJsonString) throws JSONException {

        //Vehicle information. Each vehicle's detail is an element of "list" array.
        final String VEHICLE_LIST = "vehicle_list";

        //Vehicle details referenced from JSON
        final String VEHICLE_REG = "vehicle_reg";
        final String VEHICLE_REG_DATE = "sign_up_date";
        final String VEHICLE_TOTAL_DAY_COLLECTION = "vehicle_total_day_collection";
        final String VEHICLE_TOTAL_DAY_EXPENSE = "vehicle_total_day_expense";
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

                Log.w(LOG_TAG, "From db: " + vehicleReg + ", " + regDate);

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
        vehicleValues.put(VehicleEntry.COLUMN_VEHICLE_TOTAL_COLLECTION, vehicleCollection);
        vehicleValues.put(VehicleEntry.COLUMN_VEHICLE_TOTAL_EXPENSE, vehicleExpense);
        vehicleValues.put(VehicleEntry.COLUMN_LAST_TRANSACTION_DATE_TIME, vehicleLastTransaction);
        vehicleValues.put(VehicleEntry.COLUMN_VEHICLE_REGISTRATION_DATE, regDate);

        //Use this for insert with conflict replace.
//        long vehicleRowId = db.insertWithOnConflict(VehicleEntry.TABLE_NAME, null, vehicleValues, SQLiteDatabase.CONFLICT_REPLACE);
//
        Uri vehicleUri = mContext.getContentResolver().insert(OffloadContract.VehicleEntry.CONTENT_URI, vehicleValues);

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

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        pd.dismiss();

        if(result != null){

        }else {
            Toast.makeText(mContext, "Unable to retrieve data", Toast.LENGTH_LONG).show();
        }
    }
}
