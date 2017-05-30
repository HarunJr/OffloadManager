package com.harun.offloadmanager.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.harun.offloadmanager.R;
import com.harun.offloadmanager.data.LocalStore;
import com.harun.offloadmanager.service.UserServices;

public class SplashScreenActivity extends BaseActivity {
    public static final String LOG_TAG = SplashScreenActivity.class.getSimpleName();
    public static String AUTH_TOKEN = null;
    LocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (isNetworkAvailable()) {
            userLocalStore = new LocalStore(getApplicationContext());
            AUTH_TOKEN = userLocalStore.getToken();

            bus.post(new UserServices.UserServerRequest("login", userLocalStore.getStoredUser()));

        } else {
            application.startActivity(new Intent(application, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            finish();
            Toast.makeText(application, "No Internet Connection! ", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        Log.w(LOG_TAG, "isNetworkAvailable" + networkInfo);
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}