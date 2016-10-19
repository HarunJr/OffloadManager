package com.harun.offloadmanager.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.harun.offloadmanager.R;
import com.harun.offloadmanager.User;
import com.harun.offloadmanager.UserLocalStore;
import com.harun.offloadmanager.activities.MainActivity;
import com.harun.offloadmanager.data.OffloadContract;
import com.harun.offloadmanager.data.OffloadDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by HARUN on 8/3/2016.
 * Used for sending data to the server
 */
public class ServerRequest extends AsyncTask<String, Void, String> {
    public static final String LOG_TAG = ServerRequest.class.getSimpleName();
    private Context mContext;
    private OffloadDbHelper dbHelper;
    private SQLiteDatabase db;
    String userJsonString;
    Activity activity;
    private AlertDialog.Builder builder;
    private ProgressDialog progressDialog;

    private String phone_no;
    private String pin;

    public ServerRequest(Context context) {
        this.mContext = context;
        this.activity = (Activity) context;
        dbHelper = new OffloadDbHelper(mContext);
        Log.w(LOG_TAG, "PostToServerTask called");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        builder = new AlertDialog.Builder(activity);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Connecting to server ...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        String insertV_url = "http://192.168.56.1/offloadmanager/insert_vehicle.php";
        String transact_url = "http://192.168.56.1/offloadmanager/transact.php";
        String register_user_url = "http://192.168.56.1/offloadmanager/register_user.php";
        String login_user_url = "http://192.168.56.1/offloadmanager/login_user.php";
        String vehicle_transaction_url = "http://192.168.56.1/offloadmanager/get_data.php";
        String update_url = "http://192.168.56.1/offloadmanager/update.php";
        String delete_url = "http://192.168.56.1/offloadmanager/delete.php";
        String method = params[0];

        switch (method) {
            case "add_vehicle": {
                Log.w(LOG_TAG, "doInBackground post");
                String vehicleReg = params[1];
                String make = params[2];
                String model = params[3];
                String YOM = params[4];
                String passCap = params[5];
                String dateTime = params[6];

                try {
                    URL url = new URL(insertV_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                    String data = URLEncoder.encode("vehicle_reg", "UTF-8") + "=" + URLEncoder.encode(vehicleReg, "UTF-8") + "&" +
                            URLEncoder.encode("vehicle_make", "UTF-8") + "=" + URLEncoder.encode(make, "UTF-8") + "&" +
                            URLEncoder.encode("vehicle_model", "UTF-8") + "=" + URLEncoder.encode(model, "UTF-8") + "&" +
                            URLEncoder.encode("manufacture_year", "UTF-8") + "=" + URLEncoder.encode(YOM, "UTF-8") + "&" +
                            URLEncoder.encode("passenger_cap", "UTF-8") + "=" + URLEncoder.encode(passCap, "UTF-8") + "&" +
                            URLEncoder.encode("sign_up_date", "UTF-8") + "=" + URLEncoder.encode(dateTime, "UTF-8");

                    Log.w(LOG_TAG, "doInBackground called " + vehicleReg + ": " + dateTime);
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    OS.close();

                    InputStream IS = httpURLConnection.getInputStream();
                    IS.close();
                    httpURLConnection.disconnect();

                    return "Post Vehicle success";
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "register_user": {
                Log.w(LOG_TAG, "doInBackground register_user");
                String name = params[1];
                String phone_no = params[2];
                String email = params[3];
                String pin = params[4];

                String JSON_STRING;
                userJsonString = null;

                HttpURLConnection httpURLConnection = null;
                BufferedReader bufferedReader = null;

                try {
                    URL url = new URL(register_user_url);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                    String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&" +
                            URLEncoder.encode("phone_no", "UTF-8") + "=" + URLEncoder.encode(phone_no, "UTF-8") + "&" +
                            URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
                            URLEncoder.encode("pin", "UTF-8") + "=" + URLEncoder.encode(pin, "UTF-8");

                    Log.w(LOG_TAG, "method register_user " + name + "," + email + "," + phone_no + "," + pin);

                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    OS.close();

                    InputStream IS = httpURLConnection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(IS));

                    StringBuilder stringBuilder = new StringBuilder();

                    while ((JSON_STRING = bufferedReader.readLine()) != null) {

                        stringBuilder.append(JSON_STRING).append("\n");
                    }

                    IS.close();
                    userJsonString = stringBuilder.toString().trim();
                    Log.w(LOG_TAG, "JSON String: " + userJsonString);

                    return userJsonString;//"User Registration Successful!";

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }
                break;
            }
            case "login_user": {
                Log.w(LOG_TAG, "doInBackground login_user");
                phone_no = params[1];
                pin = params[2];

                String JSON_STRING;
                userJsonString = null;

                HttpURLConnection httpURLConnection = null;
                BufferedReader bufferedReader = null;

                try {
                    URL url = new URL(login_user_url);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                    String data = URLEncoder.encode("phone_no", "UTF-8") + "=" + URLEncoder.encode(phone_no, "UTF-8") + "&" +
                            URLEncoder.encode("pin", "UTF-8") + "=" + URLEncoder.encode(pin, "UTF-8");

                    Log.w(LOG_TAG, "method login_user " + phone_no + "," + pin);

                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    OS.close();

                    InputStream IS = httpURLConnection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(IS));

                    StringBuilder stringBuilder = new StringBuilder();

                    while ((JSON_STRING = bufferedReader.readLine()) != null) {

                        stringBuilder.append(JSON_STRING).append("\n");
                    }

                    IS.close();
                    userJsonString = stringBuilder.toString().trim();
                    Log.w(LOG_TAG, "JSON String: " + userJsonString);

                    return userJsonString;//"User Registration Successful!";

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }
                break;
            }
            case "add_transaction": {
                Log.w(LOG_TAG, "doInBackground transact");
                String vehicleReg = params[1];
                String amount = params[2];
                String type = params[3];
                String description = params[4];
                String dateTime = params[5];

                try {
                    URL url = new URL(transact_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                    String data = URLEncoder.encode("vehicle_key", "UTF-8") + "=" + URLEncoder.encode(vehicleReg, "UTF-8") + "&" +
                            URLEncoder.encode("amount", "UTF-8") + "=" + URLEncoder.encode(amount, "UTF-8") + "&" +
                            URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8") + "&" +
                            URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode(description, "UTF-8") + "&" +
                            URLEncoder.encode("date_time", "UTF-8") + "=" + URLEncoder.encode(dateTime, "UTF-8");

                    Log.w(LOG_TAG, "doInBackground called " + amount + ": " + vehicleReg + ": " + dateTime);
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    OS.close();

                    InputStream IS = httpURLConnection.getInputStream();
                    IS.close();
                    httpURLConnection.disconnect();

                    return "Post Transaction success";
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }

            case "update": {
                Log.w(LOG_TAG, "doInBackground update");
                String vehicleId = params[1];
                String vehicleReg = params[2];

                try {
                    URL url = new URL(update_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                    String data = URLEncoder.encode("vehicleId", "UTF-8") + "=" + URLEncoder.encode(vehicleId, "UTF-8") + "&" +
                            URLEncoder.encode("registration", "UTF-8") + "=" + URLEncoder.encode(vehicleReg, "UTF-8");

                    Log.w(LOG_TAG, "doInBackground called " + vehicleId + ": " + vehicleReg);
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    OS.close();

                    InputStream IS = httpURLConnection.getInputStream();
                    IS.close();
                    httpURLConnection.disconnect();

                    return "Post Update success";
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "delete": {
                Log.w(LOG_TAG, "doInBackground update");
                String vehicleId = params[1];

                try {
                    URL url = new URL(delete_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                    String data = URLEncoder.encode("vehicleId", "UTF-8") + "=" + URLEncoder.encode(vehicleId, "UTF-8");

                    Log.w(LOG_TAG, "doInBackground called " + vehicleId);
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    OS.close();

                    InputStream IS = httpURLConnection.getInputStream();
                    IS.close();
                    httpURLConnection.disconnect();

                    return "Post Update success";
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        if (!result.equals(userJsonString)) {
            Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
        } else {
            try {
                authenticateJSON(userJsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void authenticateJSON(String userJsonString) throws JSONException {

        final String SERVER_RESPONSE = "server_response";

        final String CODE = "code";

        try {
            progressDialog.dismiss();
            JSONObject responseJson = new JSONObject(userJsonString);
            JSONArray responseArray = responseJson.getJSONArray(SERVER_RESPONSE);

            Log.w(LOG_TAG, "Response JSON String: " + responseArray );
                String userId, name, email, code, message;

            for (int i = 0; i < responseArray.length(); i++){
                JSONObject responseObject = responseArray.getJSONObject(i);

                code = responseObject.getString(CODE);
                message = responseObject.getString("message");

                switch (code) {
                    case "reg_true":
                        showDialog("Registration Successful", message, code);
                        break;

                    case "reg_false":
                        showDialog("Registration Failed", message, code);
                        break;

                    case "login_true":
                        JSONObject userObject = responseJson.getJSONObject("user_details");
                        name = userObject.getString("name");
                        email = userObject.getString("email");

                        User returnedUser = new User(name, phone_no, email, pin);

                        Log.w(LOG_TAG, "Response USER String: Name: " + name+" Phone: "+phone_no+" email: "+email+" Pin: "+pin);
                        Log.w(LOG_TAG, "returnedUser=" + returnedUser);

                        logUserIn(returnedUser, message);

                        break;

                    case "login_false":
                        showDialog("Login Error...", message, code);
                        break;
                }
            }
//            Log.w(LOG_TAG, "From db: " + userId + ", " + email + ", " + password + ", " + phone_no + ", ");
//            addToTransactionSQLitedb(userId, email, phone_no, password);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void logUserIn(User returnedUser, String message) {
        UserLocalStore userLocalStore = new UserLocalStore(mContext);
        userLocalStore.storeUserData(returnedUser);
        userLocalStore.setUserLoggedIn(true);

        Intent intent = new Intent(activity, MainActivity.class);
        intent.putExtra("message", message);
        activity.startActivity(intent);

    }

    private void showDialog(String title, String message, String code) {
        builder.setTitle(title);
        if (code.equals("reg_true") || code.equals("reg_false")) {
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    activity.finish();
                }
            });

        } else if (code.equals("login_false")) {
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EditText etPhoneNo, etPin;
                    etPhoneNo = (EditText) activity.findViewById(R.id.phone_no);
                    etPin = (EditText) activity.findViewById(R.id.pin);

                    etPhoneNo.setText("");
                    etPin.setText("");

                    dialogInterface.dismiss();
                }
            });
        }

        builder.create().show();
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    private void close() {
        dbHelper.close();
    }

    private void addToTransactionSQLitedb(String userId, String email, String phone_no, String password) {

        open();
        ContentValues userValues = new ContentValues();

        userValues.put(OffloadContract.UserEntry.COLUMN_USERS_ID, userId);
        userValues.put(OffloadContract.UserEntry.COLUMN_EMAIL, email);
        userValues.put(OffloadContract.UserEntry.COLUMN_PHONE_NO, phone_no);
        userValues.put(OffloadContract.UserEntry.COLUMN_TYPE, password);

        //USE THIS for normal entries
//        long vehicleRowId=db.insert(VehicleContract.VehicleEntry.TABLE_NAME,null, vehicleValues);

        //Use this for insert with conflict replace.
//        long vehicleRowId = db.insertWithOnConflict(VehicleContract.VehicleEntry.TABLE_NAME, null, vehicleValues, SQLiteDatabase.CONFLICT_REPLACE);

        Uri transactionUri = mContext.getContentResolver().insert(OffloadContract.UserEntry.CONTENT_URI, userValues);

        long userRowId = ContentUris.parseId(transactionUri);

        if (userRowId > 0) {
            Log.w(LOG_TAG, "Inserted into Table UserEntry: " + userId + ", " + email + ", " + phone_no + ", " + password);

        } else {
            Log.w(LOG_TAG, ">>>>ERROR Inserting into UserEntry: ");
        }

        close();

    }
}
