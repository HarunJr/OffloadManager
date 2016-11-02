package com.harun.offloadmanager.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.harun.offloadmanager.R;
import com.harun.offloadmanager.fragments.LoginFragment;

public class LoginActivity extends AppCompatActivity {
    public static final String LOG_TAG = LoginActivity.class.getSimpleName();
    private Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.w(LOG_TAG, "LoginActivity");

        displayLoginFragment();
    }

    private void displayLoginFragment() {

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_login, new LoginFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        //do nothing
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }
}
