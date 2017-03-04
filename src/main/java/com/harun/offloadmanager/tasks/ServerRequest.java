package com.harun.offloadmanager.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.harun.offloadmanager.R;
import com.harun.offloadmanager.activities.LoginActivity;
import com.harun.offloadmanager.activities.MainActivity;
import com.harun.offloadmanager.data.LocalStore;
import com.harun.offloadmanager.data.OffloadDbHelper;
import com.harun.offloadmanager.models.Transaction;
import com.harun.offloadmanager.models.User;
import com.harun.offloadmanager.models.Vehicle;

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
    public static final String BASE_URL = "http://192.168.56.1/offloadmanager/";//Genymotion IPV4 address
    public static final String BASE_WIFI_URL = "http://192.168.0.13/offloadmanager/"; //Ipconfig LAN
    //    private static final String BASE_WIFI_URL = "http://169.254.221.34/offloadmanager/";
    private Context mContext;
    private OffloadDbHelper dbHelper;
    private Activity activity;
    private AlertDialog.Builder builder;
    private ProgressDialog progressDialog;

    private String phone_no;
    private String pin;

    private String userJsonString;
    private String vehicleJsonString;

    private Transaction transaction;
    String exception;
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
//        isInternetAvailable();

        String insertV_url = BASE_WIFI_URL + "insert_vehicle.php";
        String transact_url = BASE_URL + "transact.php";
        String register_user_url = BASE_URL + "register_user.php";
        String login_user_url = BASE_URL + "login_user.php";
        String get_data = BASE_URL + "get_data.php";
        String update_url = BASE_URL + "update.php";
        String delete_url = BASE_URL + "delete.php";
        String method = params[0];

        switch (method) {
            case "register_user": {
                Log.w(LOG_TAG, "doInBackground register_user");
                String name = params[1];
                String phone_no = params[2];
                String email = params[3];
                String pin = params[4];
                String type = params[5];
                String token = params[6];

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
                            URLEncoder.encode("pin", "UTF-8") + "=" + URLEncoder.encode(pin, "UTF-8") + "&" +
                            URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8") + "&" +
                            URLEncoder.encode("fcm_token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8");

                    Log.w(LOG_TAG, "method register_user " + name + "," + email + "," + phone_no + "," + pin +","+type);

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

                    return "Registration Successful";//"User Registration Successful!";

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
                StringBuilder stringBuilder= null;
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

                    stringBuilder = new StringBuilder();

                    while ((JSON_STRING = bufferedReader.readLine()) != null) {

                        stringBuilder.append(JSON_STRING).append("\n");
                    }

                    IS.close();
                    userJsonString = stringBuilder.toString().trim();
                    Log.w(LOG_TAG, "JSON String: " + userJsonString);

                    return "Login Successful";//"User Registration Successful!";

                } catch (IOException e) {
                    e.printStackTrace();
//                    exceptionToBeThrown = e;
//                    if (stringBuilder != null) {
//                        stringBuilder.append(e);
//                        exception = stringBuilder.toString().trim();
//                    }
                    Log.w(LOG_TAG, "IOException " + e);
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
            case "add_vehicle": {
                Log.w(LOG_TAG, "doInBackground add_vehicle");
                String vehicleReg = params[1];
                String userKey = params[2];
                String make = params[3];
                String model = params[4];
                String YOM = params[5];
                String passCap = params[6];
                String dateTime = params[7];

                try {
                    URL url = new URL(insertV_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                    String data = URLEncoder.encode("vehicle_reg", "UTF-8") + "=" + URLEncoder.encode(vehicleReg, "UTF-8") + "&" +
                            URLEncoder.encode("user_key", "UTF-8") + "=" + URLEncoder.encode(userKey, "UTF-8") + "&" +
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
            case "fetch_all_data": {
                Log.w(LOG_TAG, "doInBackground fetch_vehicles");
                String phone_no = params[1];

                String JSON_STRING;

                HttpURLConnection httpURLConnection = null;
                BufferedReader bufferedReader = null;

                try {
                    URL url = new URL(get_data);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                    String data = URLEncoder.encode("phone_no", "UTF-8") + "=" + URLEncoder.encode(phone_no, "UTF-8") + "&" +

                            Log.w(LOG_TAG, "method register_user " + phone_no + "," + phone_no);

                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    OS.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder stringBuilder = new StringBuilder();

                    while ((JSON_STRING = bufferedReader.readLine()) != null) {

                        stringBuilder.append(JSON_STRING).append("\n");
                    }

                    inputStream.close();

                    vehicleJsonString = stringBuilder.toString().trim();
                    Log.w(LOG_TAG, "JSON String: " + vehicleJsonString);

                    return "Vehicles Fetch Success";//"User Registration Successful!";

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.w(LOG_TAG, "IOException " + e);

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
                Log.w(LOG_TAG, "doInBackground add_transaction");

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

                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    OS.close();

                    InputStream IS = httpURLConnection.getInputStream();
                    Log.w(LOG_TAG, "doInBackground called " + amount + ": " + vehicleReg + ": " + dateTime + OS);
                    IS.close();
                    httpURLConnection.disconnect();

                    return "Post Transaction success";
                } catch (IOException e) {
                    e.printStackTrace();
//                    Toast.makeText(mContext, "Unable To Connect To Internet", Toast.LENGTH_LONG).show();
                    Log.w(LOG_TAG, "IOException called " + amount + ": " + vehicleReg + ": " + dateTime );
                    Log.w(LOG_TAG, "saveToLocal" + vehicleReg +"-"+amount+"-"+type+"-"+description+"-"+dateTime);

                    int Sync = 1;//Not Synced
                    transaction = new Transaction(vehicleReg, amount, type, description, dateTime, Sync);

                    //store in SQLite Before Syncing
                    LocalStore transactionStore = new LocalStore(mContext);
                    transactionStore.storeTransactionData(transaction);
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
        dismissProgressDialog();
        Log.w(LOG_TAG, "onPostExecute Authentication Successful!!! " + result);

        if (result != null){
            switch (result) {
                case "Registration Successful":
                    try {
                        authenticateJSON(userJsonString);
//                        Toast.makeText(mContext, "User Authentication Successful", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.w(LOG_TAG, "Authentication Failed!!! " + userJsonString);
                    }

                    break;
                case "Login Successful":
                    try {
                        authenticateJSON(userJsonString);
                        Toast.makeText(mContext, "User Authentication Successful", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.w(LOG_TAG, "Authentication Failed!!! " + userJsonString);
                    }

                    break;
                case "Vehicles Fetch Success":
                    try {
                        getVehicleDataFromJson(vehicleJsonString);
                        Toast.makeText(mContext, "Vehicles Fetched Successfully", Toast.LENGTH_LONG).show();
                        Log.w(LOG_TAG, "onPostExecute Vehicles Fetched Successfully" + vehicleJsonString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.w(LOG_TAG, "Error Retrieving vehicleJsonString" + vehicleJsonString);
                    }
                    break;
                case "Post Transaction success":
                    Log.w(LOG_TAG, "Post Transaction success" + vehicleJsonString);
                    break;
                default:
                    Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
                    break;
            }

        }else {
            Toast.makeText(mContext, "Unable To Connect To Server", Toast.LENGTH_LONG).show();

        }
    }


    private void authenticateJSON(String userJsonString) throws JSONException {
        Log.w(LOG_TAG, "authenticateJSON " + userJsonString);

        final String SERVER_RESPONSE = "server_response";

        final String CODE = "code";

        try {
            JSONObject responseJson = new JSONObject(userJsonString);
            JSONArray responseArray = responseJson.getJSONArray(SERVER_RESPONSE);

            Log.w(LOG_TAG, "Response JSON String: " + responseArray);
            String userId, name, email, code, message;

            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject responseObject = responseArray.getJSONObject(i);

                code = responseObject.getString(CODE);
                message = responseObject.getString("message");

                switch (code) {
                    case "reg_true":
//                        showDialog("Registration Successful", message, code);
                        activity.startActivity(new Intent(activity, LoginActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        break;

                    case "reg_false":
                        showDialog("Registration Failed", message, code);
                        break;

                    case "login_true":
                        JSONObject userObject = responseJson.getJSONObject("user_details");
                        name = userObject.getString("name");
                        email = userObject.getString("email");

                        User returnedUser = new User(name, phone_no, email, pin);

                        Log.w(LOG_TAG, "Response USER String: Name: " + name + " Phone: " + phone_no + " email: " + email + " Pin: " + pin);
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
        LocalStore userLocalStore = new LocalStore(mContext);
        userLocalStore.storeUserData(returnedUser);
        userLocalStore.setUserLoggedIn(true);

        Intent intent = new Intent(activity, MainActivity.class);
        intent.putExtra("message", message);
        activity.startActivity(intent);
    }

    private void getVehicleDataFromJson(String vehicleJsonString) throws JSONException {

        //Vehicle information. Each vehicle's detail is an element of "vehicle_list" array.
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

                Log.w(LOG_TAG, "getVehicleDataFromJson: " + vehicleReg + ", " + vehicleCollection + ", " + vehicleExpense + ", " + vehicleLastTransaction + ", " + regDate);

                Vehicle vehicle = new Vehicle(vehicleReg, regDate, vehicleCollection, vehicleExpense, vehicleLastTransaction);

                LocalStore vehicleLocalStore = new LocalStore(mContext);
                vehicleLocalStore.storeVehicleData(vehicle);

                getCurrentDayTransactionFromJson(vehicleObject);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void dismissProgressDialog() {
        //TODO: How to handle progress dialogue in asynctask
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
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
        SQLiteDatabase db = dbHelper.getWritableDatabase();
    }

    private void close() {
        dbHelper.close();
    }


    private void getCurrentDayTransactionFromJson(JSONObject vehicleObject) {

        //Transaction information. Each Transaction's detail is an element of "transaction_list" array.
        final String TRANACTION_LIST = "transaction_list";

        //Transaction details referenced from JSON
        final String TRANSACTION_ID = "id";
        final String VEHICLE_KEY = "vehicle_key";
        final String TRANSACTION_AMOUNT = "amount";
        final String TYPE = "type";
        final String DESCRIPTION = "description";
        final String TRANSACTION_DATE_TIME = "date_time";

        JSONArray transactionArray = null;
        try {
            transactionArray = vehicleObject.getJSONArray(TRANACTION_LIST);
            for (int j = 0; j < transactionArray.length(); j++) {
                String transactionId, vehicleKey, amount, type, description, dateTime;

                JSONObject transactionObject = transactionArray.getJSONObject(j);

                transactionId = transactionObject.getString(TRANSACTION_ID);
                vehicleKey = transactionObject.getString(VEHICLE_KEY);
                amount = transactionObject.getString(TRANSACTION_AMOUNT);
                type = transactionObject.getString(TYPE);
                description = transactionObject.getString(DESCRIPTION);
                dateTime = transactionObject.getString(TRANSACTION_DATE_TIME);

                Log.w(LOG_TAG, "From db: " + transactionId + ", " + vehicleKey + ", " + amount + ", " + type + ", " + description + ", " + dateTime);

                int sync = 0; //Synced
                transaction = new Transaction(transactionId, vehicleKey, amount, type, description, dateTime, sync);

                LocalStore transactionStore = new LocalStore(mContext);
                transactionStore.storeTransactionData(transaction);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}