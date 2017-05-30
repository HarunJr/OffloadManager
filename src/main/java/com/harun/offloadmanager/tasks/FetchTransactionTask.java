package com.harun.offloadmanager.tasks;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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

/**
 * Created by HARUN on 4/5/2016.
 */
public class FetchTransactionTask extends AsyncTask<Void, Void, String> {
    public static final String LOG_TAG = FetchTransactionTask.class.getSimpleName();
    private final Context mContext;
    HttpURLConnection httpURLConnection = null;
    BufferedReader bufferedReader = null;
    String json_url;

    private OffloadDbHelper dbHelper;
    private SQLiteDatabase db;

    public FetchTransactionTask(Context context) {
        mContext = context;
        dbHelper = new OffloadDbHelper(mContext);
    }

    @Override
    protected void onPreExecute() {
//        json_url = "http://192.168.245.1/Offloader002/getData.php";/**specific to genymotion IPV4 address**/
         json_url = "http://192.168.56.1/offloadmanager/get_data.php";
    }

    @Override
    protected String doInBackground(Void... params) {
        String JSON_STRING;
        String vehicleJsonString = null;
        try {
            URL url = new URL("http://api.doea.co.ke/items");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringBuilder = new StringBuilder();
            while ((JSON_STRING = bufferedReader.readLine()) != null) {

                stringBuilder.append(JSON_STRING + "\n");
            }

            inputStream.close();

            vehicleJsonString = stringBuilder.toString().trim();
            Log.w(LOG_TAG, "JSON String: " + vehicleJsonString);
            getTransactionDataFromJson(vehicleJsonString);
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

    private void getTransactionDataFromJson(String vehicleJsonString) throws JSONException {

        //Vehicle information. Each vehicle's detail is an element of "list" array.
        final String VEHICLE_LIST = "vehicle_list";
        final String TRANACTION_LIST = "transaction_list";

        //Vehicle details referenced from JSON
        final String TRANSACTION_ID = "id";
        final String VEHICLE_KEY = "vehicle_key";
        final String TRANSACTION_AMOUNT = "amount";
        final String TYPE = "company";
        final String DESCRIPTION = "description";
        final String TRANSACTION_DATE_TIME = "date_time";

        try {
            JSONObject vehicleJson = new JSONObject(vehicleJsonString);
            JSONArray vehicleArray = vehicleJson.getJSONArray(VEHICLE_LIST);

      //      Vector<ContentValues> cVVector = new Vector<ContentValues>(transactionArray.length());

            for (int i = 0; i < vehicleArray.length(); i++) {

                JSONObject vehicleObject = vehicleArray.getJSONObject(i);
                JSONArray transactionArray = vehicleObject.getJSONArray(TRANACTION_LIST);

                Log.w(LOG_TAG, "JSON String: " + vehicleArray);
                for (int j = 0; j <transactionArray.length(); j++){
                    String transactionId, vehicleKey, amount, type, description, dateTime;

                    JSONObject transactionObject = transactionArray.getJSONObject(j);

                    transactionId = transactionObject.getString(TRANSACTION_ID);
                    vehicleKey = transactionObject.getString(VEHICLE_KEY);
                    amount = transactionObject.getString(TRANSACTION_AMOUNT);
                    type = transactionObject.getString(TYPE);
                    description = transactionObject.getString(DESCRIPTION);
                    dateTime = transactionObject.getString(TRANSACTION_DATE_TIME);

                    Log.w(LOG_TAG, "From db: " + transactionId + ", " + vehicleKey+", "+ amount + ", " + type + ", " + description + ", " + dateTime);

                    addToTransactionSQLitedb(transactionId, vehicleKey, amount, type, description, dateTime);

                }
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    private void addToTransactionSQLitedb(String transactionId, String vehicleKey, String amount, String type, String description, String dateTime) {

        open();
        ContentValues transactionValues = new ContentValues();

        transactionValues.put(OffloadContract.TransactionEntry.COLUMN_TRANSACTION_ID, transactionId);
        transactionValues.put(OffloadContract.TransactionEntry.COLUMN_VEHICLE_KEY, vehicleKey);
        transactionValues.put(OffloadContract.TransactionEntry.COLUMN_AMOUNT, amount);
        transactionValues.put(OffloadContract.TransactionEntry.COLUMN_TYPE, type);
        transactionValues.put(OffloadContract.TransactionEntry.COLUMN_DESCRIPTION, description);
        transactionValues.put(OffloadContract.TransactionEntry.TIMESTAMP, dateTime);

        //USE THIS for normal entries
//        long vehicleRowId=db.insert(VehicleContract.VehicleEntry.TABLE_NAME,null, vehicleValues);

        //Use this for insert with conflict replace.
//        long vehicleRowId = db.insertWithOnConflict(VehicleContract.VehicleEntry.TABLE_NAME, null, vehicleValues, SQLiteDatabase.CONFLICT_REPLACE);

        Uri transactionUri = mContext.getContentResolver().insert(OffloadContract.TransactionEntry.CONTENT_URI, transactionValues);

        long transactionRowId = ContentUris.parseId(transactionUri);

        if (transactionRowId > 0) {
            Log.w(LOG_TAG, "Inserted into SQLitedb: " + transactionId + ", " + vehicleKey + ", " + amount+ ", " + type+ ", " + description+ ", " + dateTime);

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
    }

}