package com.harun.offloadmanager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by HARUN on 8/3/2016.
 * Used for sending data to the server
 */
public class PostToServerTask extends AsyncTask<String, Void, String>{
    public static final String LOG_TAG = PostToServerTask.class.getSimpleName();
    Context mContext;

    public PostToServerTask(Context context) {
        this.mContext = context;
        Log.w(LOG_TAG, "PostToServerTask called");

    }
    @Override
    protected String doInBackground(String... params) {
        String insertV_url = "http://192.168.245.1/offloadmanager/insert_vehicle.php";
        String transact_url = "http://192.168.0.11/Offloader002/transact.php";
        String update_url = "http://192.168.0.11/Offloader002/update.php";
        String delete_url = "http://192.168.0.11/Offloader002/delete.php";
        String method = params[0];

        switch (method) {
            case "insertV": {
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
            case "transact": {
                Log.w(LOG_TAG, "doInBackground post");
                String vehicleId = params[1];
                String collection = params[2];
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

                    String data = URLEncoder.encode("vehicle_key", "UTF-8") + "=" + URLEncoder.encode(vehicleId, "UTF-8") + "&" +
                            URLEncoder.encode("amount", "UTF-8") + "=" + URLEncoder.encode(collection, "UTF-8") + "&" +
                            URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8") + "&" +
                            URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode(description, "UTF-8") + "&" +
                            URLEncoder.encode("date_time", "UTF-8") + "=" + URLEncoder.encode(dateTime, "UTF-8");

                    Log.w(LOG_TAG, "doInBackground called " + collection + ": " + dateTime);
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
        Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
    }

}
